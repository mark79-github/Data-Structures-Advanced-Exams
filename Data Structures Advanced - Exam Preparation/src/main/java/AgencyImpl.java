import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AgencyImpl implements Agency {

    private final Map<String, Invoice> invoices;
    private final TreeMap<LocalDate, Set<Invoice>> dueDates;
    private final Map<Department, Set<Invoice>> departments;
    private final Map<String, Set<Invoice>> companies;
    private final Map<Double, Set<Invoice>> subTotals;
    private static final double ZERO = 0;

    public AgencyImpl() {
        this.invoices = new LinkedHashMap<>();
        this.dueDates = new TreeMap<>();
        this.departments = new LinkedHashMap<>();
        this.companies = new LinkedHashMap<>();
        this.subTotals = new LinkedHashMap<>();
    }

    @Override
    public void create(Invoice invoice) {
        if (this.invoices.containsKey(invoice.getNumber())) {
            throw new IllegalArgumentException();
        }
        this.invoices.put(invoice.getNumber(), invoice);
        this.dueDates.computeIfAbsent(invoice.getDueDate(), localDate -> new LinkedHashSet<>()).add(invoice);
        this.departments.computeIfAbsent(invoice.getDepartment(), department -> new LinkedHashSet<>()).add(invoice);
        this.companies.computeIfAbsent(invoice.getCompanyName(), company -> new LinkedHashSet<>()).add(invoice);
        this.subTotals.computeIfAbsent(invoice.getSubtotal(), subTotal -> new LinkedHashSet<>()).add(invoice);
    }

    @Override
    public boolean contains(String number) {
        return this.invoices.containsKey(number);
    }

    @Override
    public int count() {
        return this.invoices.size();
    }

    @Override
    public void payInvoice(LocalDate dueDate) {
        if (!this.dueDates.containsKey(dueDate)) {
            throw new IllegalArgumentException();
        }
        this.dueDates.get(dueDate).forEach(invoice -> {
            this.subTotals.get(invoice.getSubtotal()).remove(invoice);
            if (this.subTotals.get(invoice.getSubtotal()).isEmpty()) {
                this.subTotals.remove(invoice.getSubtotal());
            }
            invoice.setSubtotal(ZERO);
            this.subTotals.computeIfAbsent(invoice.getSubtotal(), subTotal -> new LinkedHashSet<>()).add(invoice);
        });
    }

    @Override
    public void throwInvoice(String number) {
        if (!this.invoices.containsKey(number)) {
            throw new IllegalArgumentException();
        }
        Invoice invoice = this.invoices.remove(number);
        this.dueDates.get(invoice.getDueDate()).remove(invoice);
        if (this.dueDates.get(invoice.getDueDate()).isEmpty()) {
            this.dueDates.remove(invoice.getDueDate());
        }
        this.departments.get(invoice.getDepartment()).remove(invoice);
        if (this.departments.get(invoice.getDepartment()).isEmpty()) {
            this.departments.remove(invoice.getDepartment());
        }
        this.companies.get(invoice.getCompanyName()).remove(invoice);
        if (this.companies.get(invoice.getCompanyName()).isEmpty()) {
            this.companies.remove(invoice.getCompanyName());
        }
        this.subTotals.get(invoice.getSubtotal()).remove(invoice);
        if (this.subTotals.get(invoice.getSubtotal()).isEmpty()) {
            this.subTotals.remove(invoice.getSubtotal());
        }
    }

    @Override
    public void throwPayed() {
        if (this.subTotals.containsKey(ZERO)) {
            List<Invoice> invoiceList = new ArrayList<>(this.subTotals.get(ZERO));
            for (Invoice invoice : invoiceList) {
                this.throwInvoice(invoice.getNumber());
            }
        }
    }

    @Override
    public Iterable<Invoice> getAllInvoiceInPeriod(LocalDate startDate, LocalDate endDate) {
        return this.invoices.values()
                .stream()
                .filter(invoice -> (invoice.getIssueDate().isEqual(startDate) || invoice.getIssueDate().isAfter(startDate) && (invoice.getIssueDate().isBefore(endDate) || invoice.getIssueDate().isEqual(endDate))))
                .sorted((o1, o2) -> {
                    if (o1.getIssueDate().equals(o2.getIssueDate())) {
                        return o1.getDueDate().compareTo(o2.getDueDate());
                    }
                    return o1.getIssueDate().compareTo(o2.getIssueDate());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Invoice> searchByNumber(String number) {
        List<Invoice> invoiceList = this.invoices.values()
                .stream()
                .filter(invoice -> invoice.getNumber().contains(number))
                .collect(Collectors.toList());
        if (invoiceList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return invoiceList;
    }

    @Override
    public Iterable<Invoice> throwInvoiceInPeriod(LocalDate startDate, LocalDate endDate) {
        List<Invoice> invoiceList = this.dueDates.subMap(startDate, false, endDate, false)
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if (invoiceList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (Invoice invoice : invoiceList) {
            this.throwInvoice(invoice.getNumber());
        }
        return invoiceList;
    }

    @Override
    public Iterable<Invoice> getAllFromDepartment(Department department) {
        if (this.departments.containsKey(department)) {
            return this.departments.get(department)
                    .stream()
                    .sorted((o1, o2) -> {
                        if (Double.compare(o2.getSubtotal(), o1.getSubtotal()) == 0) {
                            return o1.getIssueDate().compareTo(o2.getIssueDate());
                        }
                        return Double.compare(o2.getSubtotal(), o1.getSubtotal());
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Iterable<Invoice> getAllByCompany(String companyName) {
        if (this.companies.containsKey(companyName)) {
            return this.companies.get(companyName)
                    .stream()
                    .sorted((o1, o2) -> o2.getNumber().compareTo(o1.getNumber()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void extendDeadline(LocalDate endDate, int days) {
        if (!this.dueDates.containsKey(endDate)) {
            throw new IllegalArgumentException();
        }
        this.dueDates.get(endDate)
                .forEach(invoice -> invoice.setDueDate(invoice.getDueDate().plusDays(days)));
    }
}
