package core;

import models.Vehicle;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class VehicleRepositoryImpl implements VehicleRepository {

    private final Map<String, Vehicle> vehicles;
    private final Map<String, Map<String, Vehicle>> sellers;
    private final Map<String, Map<String, Vehicle>> brands;

    public VehicleRepositoryImpl() {
        this.vehicles = new LinkedHashMap<>();
        this.sellers = new LinkedHashMap<>();
        this.brands = new LinkedHashMap<>();
    }

    @Override
    public void addVehicleForSale(Vehicle vehicle, String sellerName) {
        vehicle.setSeller(sellerName);
        this.vehicles.put(vehicle.getId(), vehicle);
        this.sellers.computeIfAbsent(sellerName, s -> new LinkedHashMap<>()).put(vehicle.getId(), vehicle);
        this.brands.computeIfAbsent(vehicle.getBrand(), s -> new LinkedHashMap<>()).put(vehicle.getId(), vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        if (!this.vehicles.containsKey(vehicleId)) {
            throw new IllegalArgumentException();
        }
        Vehicle vehicle = this.vehicles.remove(vehicleId);
        this.sellers.get(vehicle.getSeller()).remove(vehicle.getId());
        this.brands.get(vehicle.getBrand()).remove(vehicle.getId());
    }

    @Override
    public int size() {
        return this.vehicles.size();
    }

    @Override
    public boolean contains(Vehicle vehicle) {
        return this.vehicles.containsKey(vehicle.getId());
    }

    @Override
    public Iterable<Vehicle> getVehicles(List<String> keywords) {
        return this.vehicles.values()
                .stream()
                .filter(vehicle -> keywords.contains(vehicle.getBrand()) ||
                        keywords.contains(vehicle.getColor()) ||
                        keywords.contains(vehicle.getLocation()) ||
                        keywords.contains(vehicle.getModel()))
                .sorted((o1, o2) -> {
                    if (Boolean.compare(o2.getIsVIP(), o1.getIsVIP()) == 0) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                    return Boolean.compare(o2.getIsVIP(), o1.getIsVIP());
                })
                .collect(toList());
    }

    @Override
    public Iterable<Vehicle> getVehiclesBySeller(String sellerName) {
        if (!this.sellers.containsKey(sellerName)) {
            throw new IllegalArgumentException();
        }
        return this.sellers.get(sellerName).values();
    }

    @Override
    public Iterable<Vehicle> getVehiclesInPriceRange(double lowerBound, double upperBound) {
        return this.vehicles.values()
                .stream()
                .filter(vehicle -> vehicle.getPrice() >= lowerBound && vehicle.getPrice() <= upperBound)
                .sorted(Comparator.comparingDouble(Vehicle::getHorsepower).reversed())
                .collect(toList());
    }

    @Override
    public Map<String, List<Vehicle>> getAllVehiclesGroupedByBrand() {
        if (this.brands.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return this.brands.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().values().stream().sorted(Comparator.comparingDouble(Vehicle::getPrice)).collect(toList()),
                        (v1, v2) -> {
                            throw new IllegalArgumentException();
                        },
                        LinkedHashMap::new
                ));
    }

    @Override
    public Iterable<Vehicle> getAllVehiclesOrderedByHorsepowerDescendingThenByPriceThenBySellerName() {
        return this.vehicles.values()
                .stream()
                .sorted((o1, o2) -> {
                    if (o2.getHorsepower() == o1.getHorsepower()) {
                        if (Double.compare(o1.getPrice(), o2.getPrice()) == 0) {
                            return o1.getSeller().compareTo(o2.getSeller());
                        }
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                    return Integer.compare(o2.getHorsepower(), o1.getHorsepower());
                })
                .collect(toList());
    }

    @Override
    public Vehicle buyCheapestFromSeller(String sellerName) {
        if (!this.sellers.containsKey(sellerName) || this.sellers.get(sellerName).isEmpty()) {
            throw new IllegalArgumentException();
        }
        Vehicle vehicle = this.sellers.get(sellerName).values()
                .stream()
                .min(Comparator.comparingDouble(Vehicle::getPrice))
                .orElseThrow(IllegalArgumentException::new);
        this.removeVehicle(vehicle.getId());
        return vehicle;
    }
}
