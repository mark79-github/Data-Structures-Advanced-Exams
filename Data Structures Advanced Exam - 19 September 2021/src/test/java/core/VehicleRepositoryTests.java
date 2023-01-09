package core;

import models.Vehicle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class VehicleRepositoryTests {
    private VehicleRepository vehicleRepository;

    @Before
    public void setup() {
        this.vehicleRepository = new VehicleRepositoryImpl();
    }

    @Test
    public void testAddVehicle_WithCorrectData_ShouldCorrectlyAddVehicle() {
        Vehicle vehicle = new Vehicle(1 + "", "BMW", "X5", "Sofia", "Blue", 400, 50000, true);

        this.vehicleRepository.addVehicleForSale(vehicle, "George");

        assertTrue(this.vehicleRepository.contains(vehicle));
    }

    @Test
    public void testContains_WithNonexistentVehicle_ShouldReturnFalse() {
        Vehicle vehicle = new Vehicle(1 + "", "BMW", "X5", "Sofia", "Blue", 400, 50000, true);
        Vehicle vehicle2 = new Vehicle(2 + "", "BMW2", "X52", "Sofia2", "Blue2", 500, 60000, false);

        this.vehicleRepository.addVehicleForSale(vehicle, "George");

        assertFalse(this.vehicleRepository.contains(vehicle2));
    }

    @Test
    public void testRemoveVehicle_WithNonexistentVehicle_ShouldThrowException() {
        Vehicle vehicle = new Vehicle(1 + "", "BMW", "X5", "Sofia", "Blue", 400, 50000, true);
        Vehicle vehicle2 = new Vehicle(2 + "", "BMW", "X52", "Sofia2", "Blue2", 500, 60000, false);
        Vehicle vehicle3 = new Vehicle(3 + "", "Audi", "A3", "Sofia3", "Blue3", 300, 70000, false);

        this.vehicleRepository.addVehicleForSale(vehicle3, "George");
        this.vehicleRepository.addVehicleForSale(vehicle2, "George");
        this.vehicleRepository.addVehicleForSale(vehicle, "George");

        assertThrows(IllegalArgumentException.class, () -> this.vehicleRepository.removeVehicle("non-existent"));
    }

    @Test
    public void testGetVehiclesOrdered_WithExistentVehicles_ShouldCorrectlyOrderedVehicles() {
        Vehicle vehicle = new Vehicle(1 + "", "BMW", "X5", "Sofia", "Blue", 400, 50000, true);
        Vehicle vehicle2 = new Vehicle(2 + "", "BMW", "X52", "Sofia2", "Blue2", 500, 61000, false);
        Vehicle vehicle3 = new Vehicle(3 + "", "Audi", "A3", "Sofia3", "Blue3", 300, 70000, false);
        Vehicle vehicle4 = new Vehicle(4 + "", "Audi", "A3", "Sofia3", "Blue3", 500, 88000, false);
        Vehicle vehicle5 = new Vehicle(5 + "", "Audi", "A3", "Sofia3", "Blue3", 500, 61000, false);

        this.vehicleRepository.addVehicleForSale(vehicle, "George");
        this.vehicleRepository.addVehicleForSale(vehicle2, "Jack");
        this.vehicleRepository.addVehicleForSale(vehicle3, "Phill");
        this.vehicleRepository.addVehicleForSale(vehicle4, "Isacc");
        this.vehicleRepository.addVehicleForSale(vehicle5, "Igor");

        List<Vehicle> orderedVehicles = StreamSupport.stream(this.vehicleRepository.getAllVehiclesOrderedByHorsepowerDescendingThenByPriceThenBySellerName().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(5, orderedVehicles.size());

        assertEquals(vehicle5, orderedVehicles.get(0));
        assertEquals(vehicle2, orderedVehicles.get(1));
        assertEquals(vehicle4, orderedVehicles.get(2));
        assertEquals(vehicle, orderedVehicles.get(3));
        assertEquals(vehicle3, orderedVehicles.get(4));
    }

    @Test
    public void testBuyCheapest_With1000000Vehicles_ShouldPassQuickly() {
        int count = 1000000;

        for (int i = count, j = 0; i >= 0 && j <= count; i--, j++) {
            String sellerName = "George";

            Vehicle vehicle = new Vehicle(i + "", "BMW", "X5", "Sofia", "Blue", i * 10, i, true);

            this.vehicleRepository.addVehicleForSale(vehicle, sellerName);
        }

        long start = System.currentTimeMillis();

        this.vehicleRepository.buyCheapestFromSeller("George");

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 50);
    }

    @Test
    public void test_getVehicles_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehicles(List.of(UUID.randomUUID().toString()));
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, vehicles.size());
    }

    @Test
    public void test_getVehicles_shouldReturnEmptyCollection_whenCollectionDoesNotContainAnyKeyword() {
        Vehicle vehicle_1 = new Vehicle(UUID.randomUUID().toString(), "Brand", "Model", "Location", "Color", 100, 100.00, true);
        Vehicle vehicle_2 = new Vehicle(UUID.randomUUID().toString(), "Brand", "Model", "Location", "Color", 100, 100.00, true);

        this.vehicleRepository.addVehicleForSale(vehicle_1, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_2, UUID.randomUUID().toString());

        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehicles(List.of(UUID.randomUUID().toString()));
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, vehicles.size());
    }

    @Test
    public void test_getVehicles_shouldReturnCollectionSortedCorrectly() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.02, false);
        Vehicle vehicle_4 = new Vehicle("4", "Brand", "Model", "Location", "Color", 100, 100.01, false);
        Vehicle vehicle_5 = new Vehicle("5", "Brand", "Model", "Location", "Color", 100, 100.00, true);
        Vehicle vehicle_6 = new Vehicle("6", "Brand", "Model", "Location", "Color", 100, 100.02, true);
        Vehicle vehicle_7 = new Vehicle("7", "Brand", "Model", "Location", "Color", 100, 100.00, true);
        Vehicle vehicle_8 = new Vehicle("8", "Brand", "Model", "Location", "Color", 100, 100.01, true);

        this.vehicleRepository.addVehicleForSale(vehicle_1, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_2, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_3, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_4, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_5, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_6, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_7, UUID.randomUUID().toString());
        this.vehicleRepository.addVehicleForSale(vehicle_8, UUID.randomUUID().toString());

        String[] expected = {vehicle_5.getId(), vehicle_7.getId(), vehicle_8.getId(), vehicle_6.getId(), vehicle_1.getId(), vehicle_2.getId(), vehicle_4.getId(), vehicle_3.getId()};

        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehicles(List.of("Color"));
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(8, vehicles.size());
        int counter = 0;
        for (Vehicle vehicle : vehicleIterable) {
            Assert.assertEquals(expected[counter++], vehicle.getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_buyCheapestFromSeller_whenNoSuchSeller() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "2");
        this.vehicleRepository.buyCheapestFromSeller(UUID.randomUUID().toString());
    }

    @Test
    public void test_buyCheapestFromSeller_shouldReturnCorrectVehicle() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.01, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.02, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");
        Vehicle vehicle = this.vehicleRepository.buyCheapestFromSeller("1");

        Assert.assertEquals(vehicle.getId(), vehicle_2.getId());
    }

    @Test
    public void test_buyCheapestFromSeller_shouldReturnCorrectVehicle_v2() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");
        Vehicle vehicle = this.vehicleRepository.buyCheapestFromSeller("1");

        Assert.assertEquals(vehicle.getId(), vehicle_1.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getVehiclesBySeller_shouldThrowException_whenNoEntities() {
        this.vehicleRepository.getVehiclesBySeller(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getVehiclesBySeller_shouldThrowException_whenSellerNotFound() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");

        this.vehicleRepository.getVehiclesBySeller(UUID.randomUUID().toString());
    }

    @Test
    public void test_getVehiclesBySeller_shouldReturnSortedCollection() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_4 = new Vehicle("4", "Brand", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_4, "2");
        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");

        String[] expected = {vehicle_3.getId(), vehicle_1.getId(), vehicle_2.getId()};

        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehiclesBySeller("1");
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, vehicles.size());
        int counter = 0;
        for (Vehicle vehicle : vehicles) {
            Assert.assertEquals(expected[counter++], vehicle.getId());
        }
    }

    @Test
    public void test_getVehiclesInPriceRange_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehiclesInPriceRange(101, 200);
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertTrue(vehicles.isEmpty());
    }

    @Test
    public void test_getVehiclesInPriceRange_shouldReturnEmptyCollection_whenVehiclesOutOfPriceRange() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");

        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehiclesInPriceRange(101, 200);
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertTrue(vehicles.isEmpty());
    }

    @Test
    public void test_getVehiclesInPriceRange_shouldReturnSortedCollection() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 101.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 200.00, false);
        Vehicle vehicle_4 = new Vehicle("4", "Brand", "Model", "Location", "Color", 101, 200.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_4, "2");
        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");

        String[] expected = {vehicle_4.getId(), vehicle_3.getId(), vehicle_2.getId()};

        Iterable<Vehicle> vehicleIterable = this.vehicleRepository.getVehiclesInPriceRange(101, 200);
        List<Vehicle> vehicles = StreamSupport.stream(vehicleIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, vehicles.size());
        int counter = 0;
        for (Vehicle vehicle : vehicles) {
            Assert.assertEquals(expected[counter++], vehicle.getId());
        }
    }

    @Test
    public void test_size_shouldReturnCorrectly() {
        int size = this.vehicleRepository.size();
        Assert.assertEquals(0, size);

        Vehicle vehicle_1 = new Vehicle("1", "Brand", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand", "Model", "Location", "Color", 100, 101.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand", "Model", "Location", "Color", 100, 200.00, false);
        Vehicle vehicle_4 = new Vehicle("4", "Brand", "Model", "Location", "Color", 101, 200.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_3, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_4, "2");
        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "1");

        Assert.assertEquals(4, this.vehicleRepository.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getAllVehiclesGroupedByBrand_shouldThrowException_whenNoEntities() {
        this.vehicleRepository.getAllVehiclesGroupedByBrand();
    }

    @Test
    public void test_getAllVehiclesGroupedByBrand_shouldReturnSortedCollection() {
        Vehicle vehicle_1 = new Vehicle("1", "Brand_1", "Model", "Location", "Color", 100, 200.00, false);
        Vehicle vehicle_2 = new Vehicle("2", "Brand_1", "Model", "Location", "Color", 100, 100.00, false);
        Vehicle vehicle_3 = new Vehicle("3", "Brand_1", "Model", "Location", "Color", 100, 150.00, false);
        Vehicle vehicle_4 = new Vehicle("4", "Brand_2", "Model", "Location", "Color", 100, 100.00, false);

        this.vehicleRepository.addVehicleForSale(vehicle_1, "1");
        this.vehicleRepository.addVehicleForSale(vehicle_2, "2");
        this.vehicleRepository.addVehicleForSale(vehicle_3, "3");
        this.vehicleRepository.addVehicleForSale(vehicle_4, "4");

        String[] expected = {vehicle_2.getId(), vehicle_3.getId(), vehicle_1.getId(), vehicle_4.getId()};

        Map<String, List<Vehicle>> vehiclesGroupedByBrand = this.vehicleRepository.getAllVehiclesGroupedByBrand();

        Assert.assertEquals(2, vehiclesGroupedByBrand.size());

        int counter = 0;
        List<Vehicle> vehicles = vehiclesGroupedByBrand.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (Vehicle vehicle : vehicles) {
            Assert.assertEquals(expected[counter++], vehicle.getId());
        }
    }
}
