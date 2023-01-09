import java.util.*;
import java.util.stream.Collectors;

public class MicrosystemImpl implements Microsystem {

    private final Map<Integer, Computer> computers;
    private final EnumMap<Brand, Set<Computer>> brands;
    private static final Comparator<Computer> comparator = Comparator.comparingDouble(Computer::getPrice).reversed();

    public MicrosystemImpl() {
        this.computers = new HashMap<>();
        this.brands = new EnumMap<>(Brand.class);
    }

    @Override
    public void createComputer(Computer computer) {
        if (this.contains(computer.getNumber())) {
            throw new IllegalArgumentException();
        }
        this.computers.put(computer.getNumber(), computer);
        this.brands.computeIfAbsent(computer.getBrand(), brand -> new HashSet<>()).add(computer);
    }

    @Override
    public boolean contains(int number) {
        return this.computers.containsKey(number);
    }

    @Override
    public int count() {
        return this.computers.size();
    }

    @Override
    public Computer getComputer(int number) {
        if (!this.contains(number)) {
            throw new IllegalArgumentException();
        }
        return this.computers.get(number);
    }

    @Override
    public void remove(int number) {
        if (!this.contains(number)) {
            throw new IllegalArgumentException();
        }
        Computer computer = this.computers.remove(number);
        this.brands.get(computer.getBrand()).remove(computer);
        if (this.brands.get(computer.getBrand()).isEmpty()) {
            this.brands.remove(computer.getBrand());
        }
    }

    @Override
    public void removeWithBrand(Brand brand) {
        if (!this.brands.containsKey(brand)) {
            throw new IllegalArgumentException();
        }
        this.brands.remove(brand)
                .forEach(computer -> this.computers.remove(computer.getNumber()));
    }

    @Override
    public void upgradeRam(int ram, int number) {
        Computer computer = this.getComputer(number);
        if (ram > computer.getRAM()) {
            computer.setRAM(ram);
        }
    }

    @Override
    public Iterable<Computer> getAllFromBrand(Brand brand) {
        if (this.brands.containsKey(brand)) {
            return this.brands.get(brand);
        }
        return Collections.emptyList();
    }

    @Override
    public Iterable<Computer> getAllWithScreenSize(double screenSize) {
        return this.computers
                .values()
                .stream()
                .filter(computer -> computer.getScreenSize() == screenSize)
                .sorted((c1, c2) -> Integer.compare(c2.getNumber(), c1.getNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Computer> getAllWithColor(String color) {
        return this.computers
                .values()
                .stream()
                .filter(c -> c.getColor().equals(color))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Computer> getInRangePrice(double minPrice, double maxPrice) {
        return this.computers
                .values()
                .stream()
                .filter(c -> c.getPrice() >= minPrice && c.getPrice() <= maxPrice)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
