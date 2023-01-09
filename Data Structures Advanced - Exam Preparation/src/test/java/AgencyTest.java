import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgencyTest {
    private Agency agency;

    @Before
    public void setup() {
        this.agency = new AgencyImpl();
    }

    @Test
    public void test_contains_with_correct_data() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125D, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                1000D, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);

        final boolean expectedContains = this.agency.contains(inv1.getNumber()) &&
                this.agency.contains(inv2.getNumber()) &&
                !this.agency.contains("5");

        assertTrue("Incorrect contains behavior", expectedContains);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_create_must_throw_exception() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125D, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("1",
                "SoftUni",
                1000D, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
    }

    @Test
    public void test_create_only_with_contains_check() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125D, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                1000D, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);

        final boolean expectedContains = this.agency.contains(inv1.getNumber()) &&
                this.agency.contains(inv2.getNumber()) &&
                !this.agency.contains("5");

        assertTrue("Incorrect contains behavior", expectedContains);
    }

    @Test
    public void test_create() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125D, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                1000D, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);

        final int expectedCount = 2;
        final int actualCount = this.agency.count();

        assertEquals("Incorrect count", expectedCount, actualCount);

        final boolean expectedContains = this.agency.contains(inv1.getNumber()) &&
                this.agency.contains(inv2.getNumber()) &&
                !this.agency.contains("5");

        assertTrue("Incorrect contains behavior", expectedContains);
    }

    @Test
    public void test_throwPayed_shouldRemoveInvoiceSuccessfully() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("3",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.throwPayed();
        Assert.assertEquals(2, this.agency.count());
        Assert.assertTrue(this.agency.contains("1"));
        Assert.assertTrue(this.agency.contains("3"));
        Assert.assertFalse(this.agency.contains("2"));
    }

    @Test
    public void test_throwPayed_afterPayInvoice_shouldRemoveInvoiceSuccessfully() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("3",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.payInvoice(LocalDate.of(2022, 3, 12));
        this.agency.throwPayed();
        Assert.assertEquals(1, this.agency.count());
        Assert.assertTrue(this.agency.contains("1"));
        Assert.assertFalse(this.agency.contains("2"));
        Assert.assertFalse(this.agency.contains("3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_searchByNumber_shouldThrowException_whenNoEntities() {
//        Invoice inv1 = new Invoice("1",
//                "HRS",
//                125d, Department.INCOMES,
//                LocalDate.of(2018, 2, 12),
//                LocalDate.of(2018, 3, 12));
//
//        Invoice inv2 = new Invoice("2",
//                "SoftUni",
//                0d, Department.INCOMES,
//                LocalDate.of(2019, 2, 12),
//                LocalDate.of(2019, 3, 12));
//
//        Invoice inv3 = new Invoice("3",
//                "SoftUni",
//                100d, Department.SELLS,
//                LocalDate.of(2019, 5, 31),
//                LocalDate.of(2022, 3, 12));
//
//        this.agency.create(inv1);
//        this.agency.create(inv2);
//        this.agency.create(inv3);
//
//        this.agency.payInvoice(LocalDate.of(2022, 3, 12));
//        this.agency.throwPayed();
        this.agency.searchByNumber("1");
//        Assert.assertEquals(1, this.agency.count());
//        Assert.assertTrue(this.agency.contains("1"));
//        Assert.assertFalse(this.agency.contains("2"));
//        Assert.assertFalse(this.agency.contains("3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_searchByNumber_shouldThrowException_whenDoesNotExistsInvoicesContainsSuchNumber() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("3",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.searchByNumber("abc");
    }

    @Test
    public void test_searchByNumber_shouldReturnCorrectly() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        String[] expected = {inv1.getNumber(), inv3.getNumber()};

        Iterable<Invoice> invoiceIterable = this.agency.searchByNumber("1");
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(2, invoices.size());
        int counter = 0;
        for (Invoice invoice : invoiceIterable) {
            Assert.assertEquals(expected[counter++], invoice.getNumber());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_payInvoice_shouldThrowException_whenNoEntities() {
        this.agency.payInvoice(LocalDate.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_payInvoice_shouldThrowException_whenDoesNotExistsInvoicesContainsSuchNumber() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("3",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.payInvoice(LocalDate.EPOCH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_throwInvoice_shouldThrowException_whenNoEntities() {
        this.agency.throwInvoice("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_throwInvoice_shouldThrowException_whenDoesNotExistsInvoicesContainsSuchNumber() {
        Invoice inv1 = new Invoice("1",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("2",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("3",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.throwInvoice("0");
    }

    @Test
    public void test_getAllInvoiceInPeriod_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Invoice> invoiceIterable = this.agency.getAllInvoiceInPeriod(LocalDate.EPOCH, LocalDate.now());
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllInvoiceInPeriod_shouldReturnEmptyCollection_whenNoInvoicesInGivenPeriod() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);


        Iterable<Invoice> invoiceIterable = this.agency.getAllInvoiceInPeriod(LocalDate.EPOCH, LocalDate.ofEpochDay(100));
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllInvoiceInPeriod_shouldReturnSortedCollection() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 11),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "Software University",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        Invoice inv4 = new Invoice("44",
                "HRS",
                100d, Department.WASTAGE,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 11));

        Invoice inv5 = new Invoice("51",
                "HRS",
                100d, Department.WASTAGE,
                LocalDate.EPOCH,
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);
        this.agency.create(inv5);

        String[] expected = {inv2.getNumber(), inv1.getNumber(), inv4.getNumber(), inv3.getNumber()};

        Iterable<Invoice> invoiceIterable = this.agency.getAllInvoiceInPeriod(LocalDate.ofEpochDay(1), LocalDate.now());
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(4, invoices.size());

        int counter = 0;
        for (Invoice invoice : invoiceIterable) {
            Assert.assertEquals(expected[counter++], invoice.getNumber());
        }
    }

    @Test
    public void test_getAllFromDepartment_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Invoice> invoiceIterable = this.agency.getAllFromDepartment(Department.INCOMES);
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllFromDepartment_shouldReturnEmptyCollection_whenNoInvoicesWithGivenDepartment() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);


        Iterable<Invoice> invoiceIterable = this.agency.getAllFromDepartment(Department.OTHERS);
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllFromDepartment_shouldReturnSortedCollection() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 11),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "Software University",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        Invoice inv4 = new Invoice("44",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 11));

        Invoice inv5 = new Invoice("51",
                "HRS",
                100d, Department.INTERNALS,
                LocalDate.EPOCH,
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);
        this.agency.create(inv5);

        String[] expected = {inv4.getNumber(), inv1.getNumber(), inv2.getNumber()};

        Iterable<Invoice> invoiceIterable = this.agency.getAllFromDepartment(Department.INCOMES);
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, invoices.size());

        int counter = 0;
        for (Invoice invoice : invoiceIterable) {
            Assert.assertEquals(expected[counter++], invoice.getNumber());
        }
    }

    @Test
    public void test_getAllByCompany_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Invoice> invoiceIterable = this.agency.getAllByCompany(UUID.randomUUID().toString());
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllByCompany_shouldReturnEmptyCollection_whenNoInvoicesWithGivenDepartment() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2018, 2, 12),
                LocalDate.of(2018, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("31",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);


        Iterable<Invoice> invoiceIterable = this.agency.getAllByCompany(UUID.randomUUID().toString());
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, invoices.size());
    }

    @Test
    public void test_getAllByCompany_shouldReturnSortedCollection() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 11),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("13",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        Invoice inv4 = new Invoice("44",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 11));

        Invoice inv5 = new Invoice("31",
                "SoftUni",
                100d, Department.INTERNALS,
                LocalDate.EPOCH,
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);
        this.agency.create(inv5);

        String[] expected = {inv5.getNumber(), inv2.getNumber(), inv3.getNumber()};

        Iterable<Invoice> invoiceIterable = this.agency.getAllByCompany("SoftUni");
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, invoices.size());

        int counter = 0;
        for (Invoice invoice : invoiceIterable) {
            Assert.assertEquals(expected[counter++], invoice.getNumber());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_extendDeadline_shouldThrowException_whenNoEntities() {
        this.agency.extendDeadline(LocalDate.now(), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_extendDeadline_shouldThrowException_whenNoInvoicesWithGivenEndDate() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 2, 11),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("13",
                "SoftUni",
                100d, Department.SELLS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);

        this.agency.extendDeadline(LocalDate.now(), 1);
    }

    @Test
    public void test_extendDeadline_shouldExtendEndDateCorrectly() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.EPOCH);

        Invoice inv3 = new Invoice("44",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 5, 31),
                LocalDate.EPOCH);

        Invoice inv4 = new Invoice("31",
                "SoftUni",
                100d, Department.INTERNALS,
                LocalDate.EPOCH,
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);

        this.agency.extendDeadline(LocalDate.EPOCH, 2);

        Assert.assertNotEquals(inv1.getDueDate(), LocalDate.ofEpochDay(2));
        Assert.assertEquals(inv2.getDueDate(), LocalDate.ofEpochDay(2));
        Assert.assertEquals(inv3.getDueDate(), LocalDate.ofEpochDay(2));
        Assert.assertNotEquals(inv4.getDueDate(), LocalDate.ofEpochDay(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_throwInvoiceInPeriod_shouldThrowException_whenNoEntities() {
        this.agency.throwInvoiceInPeriod(LocalDate.EPOCH, LocalDate.now());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_throwInvoiceInPeriod_shouldThrowException_whenNoInvoicesInGivenPeriod() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 12));

        Invoice inv3 = new Invoice("44",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2019, 3, 12));

        Invoice inv4 = new Invoice("31",
                "SoftUni",
                100d, Department.INTERNALS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2022, 3, 12));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);

        this.agency.throwInvoiceInPeriod(LocalDate.EPOCH, LocalDate.ofEpochDay(2));
    }

    @Test
    public void test_throwInvoiceInPeriod_shouldReturnCorrectly() {
        Invoice inv1 = new Invoice("11",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 10));

        Invoice inv2 = new Invoice("22",
                "SoftUni",
                0d, Department.INCOMES,
                LocalDate.of(2019, 6, 12),
                LocalDate.of(2019, 3, 11));

        Invoice inv3 = new Invoice("44",
                "HRS",
                125d, Department.INCOMES,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2019, 3, 12));

        Invoice inv4 = new Invoice("31",
                "SoftUni",
                100d, Department.INTERNALS,
                LocalDate.of(2019, 5, 31),
                LocalDate.of(2019, 3, 13));

        this.agency.create(inv1);
        this.agency.create(inv2);
        this.agency.create(inv3);
        this.agency.create(inv4);

        String[] expected = {inv2.getNumber(), inv3.getNumber()};

        Iterable<Invoice> invoiceIterable = this.agency.throwInvoiceInPeriod(LocalDate.of(2019, 3, 10), LocalDate.of(2019, 3, 13));
        List<Invoice> invoices = StreamSupport.stream(invoiceIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(2, invoices.size());

        int counter = 0;
        for (Invoice invoice : invoiceIterable) {
            Assert.assertEquals(expected[counter++], invoice.getNumber());
        }
    }
}