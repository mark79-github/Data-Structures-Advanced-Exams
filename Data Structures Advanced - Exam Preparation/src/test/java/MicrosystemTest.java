import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MicrosystemTest {
    private Microsystem microsystem;

    @Before
    public void setUp() {
        this.microsystem = new MicrosystemImpl();
    }

    @Test
    public void count_should_work_correctly() {
        Computer computer1 = new Computer(2, Brand.ACER, 1120, 15.6, "grey");
        Computer computer = new Computer(1, Brand.DELL, 2300, 15.6, "grey");
        Computer computer2 = new Computer(5, Brand.HP, 2400, 13.6, "red");


        this.microsystem.createComputer(computer);
        this.microsystem.createComputer(computer1);
        this.microsystem.createComputer(computer2);

        final int expectedCount = 3;
        final int actualCount = this.microsystem.count();

        assertEquals("Incorrect count", expectedCount, actualCount);
    }

    @Test
    public void createComputer_should_return_true_with_valid_number() {
        Computer computer1 = new Computer(2, Brand.ACER, 1120, 15.6, "grey");
        Computer computer = new Computer(1, Brand.DELL, 2300, 15.6, "grey");


        this.microsystem.createComputer(computer);
        this.microsystem.createComputer(computer1);

        assertTrue("Incorrect return value", this.microsystem.contains(1));
        assertTrue("Incorrect return value", this.microsystem.contains(2));
    }

    @Test
    public void createComputer_should_increase_count() {
        Computer computer1 = new Computer(1, Brand.ACER, 1120, 15.6, "grey");
        Computer computer2 = new Computer(2, Brand.ASUS, 2000, 15.6, "red");

        this.microsystem.createComputer(computer1);
        this.microsystem.createComputer(computer2);
        final int expectedCount = 2;
        final int actualCount = this.microsystem.count();

        assertEquals("Incorrect count", expectedCount, actualCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createComputer_should_throw_exception() {
        Computer computer = new Computer(1, Brand.ASUS, 10D, 13.3, "red");
        Computer computer1 = new Computer(1, Brand.DELL, 11D, 14.3, "black");

        this.microsystem.createComputer(computer);
        this.microsystem.createComputer(computer1);
    }

    @Test
    public void test_getAllWithColor_shouldReturnEmptyList_whenNoEntities() {
        Iterable<Computer> computerIterable = this.microsystem.getAllWithColor(UUID.randomUUID().toString());
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithColor_shouldReturnEmptyList_whenNoComputerWithProvidedColor() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.DELL, 11d, 14.3, "black");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);

        Iterable<Computer> computerIterable = this.microsystem.getAllWithColor("white");
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithColor_shouldReturnEmptyList_whenComputerWithProvidedColorWasAddedButThenRemoved() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.DELL, 11d, 14.3, "black");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.remove(1);

        Iterable<Computer> computerIterable = this.microsystem.getAllWithColor("red");
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithColor_shouldReturnEmptyList_whenComputerWithProvidedColorWasAddedButThenRemovedByBrand() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.DELL, 11d, 14.3, "black");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.removeWithBrand(Brand.ASUS);

        Iterable<Computer> computerIterable = this.microsystem.getAllWithColor("red");
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithColor_shouldReturnCorrectly() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 11d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 11d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        int[] expected = {computer_3.getNumber(), computer_5.getNumber(), computer_1.getNumber()};

        Iterable<Computer> computerIterable = this.microsystem.getAllWithColor("red");
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, computers.size());

        int counter = 0;
        for (Computer computer : computerIterable) {
            Assert.assertEquals(expected[counter++], computer.getNumber());
        }
    }

    @Test
    public void test_getInRangePrice_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Computer> computerIterable = this.microsystem.getInRangePrice(1d, 2d);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getInRangePrice_shouldReturnEmptyCollection_whenEntitiesAreRemoved() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 11d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 11d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        this.microsystem.remove(computer_3.getNumber());
        this.microsystem.remove(computer_4.getNumber());
        this.microsystem.removeWithBrand(Brand.ASUS);
        this.microsystem.removeWithBrand(Brand.ACER);


        Iterable<Computer> computerIterable = this.microsystem.getInRangePrice(10d, 11d);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getInRangePrice_shouldReturnSortedCollection() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 12d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        int[] expected = {computer_5.getNumber(), computer_3.getNumber(), computer_4.getNumber(), computer_6.getNumber()};

        Iterable<Computer> computerIterable = this.microsystem.getInRangePrice(10.01, 12.00);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(4, computers.size());

        int counter = 0;
        for (Computer computer : computerIterable) {
            Assert.assertEquals(expected[counter++], computer.getNumber());
        }
    }

    @Test
    public void test_getAllFromBrand_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Computer> computerIterable = this.microsystem.getAllFromBrand(Brand.HP);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllFromBrand_shouldReturnEmptyCollection_whenEntitiesAreRemoved() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 11d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 11d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        this.microsystem.remove(computer_3.getNumber());
        this.microsystem.remove(computer_4.getNumber());
        this.microsystem.removeWithBrand(Brand.ASUS);
        this.microsystem.removeWithBrand(Brand.ACER);


        Iterable<Computer> computerIterable = this.microsystem.getAllFromBrand(Brand.HP);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllFromBrand_shouldReturnCorrectly() {
        Computer computer_1 = new Computer(1, Brand.DELL, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.ACER, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 12d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        int[] expected = {computer_5.getNumber(), computer_4.getNumber(), computer_2.getNumber()};

        Iterable<Computer> computerIterable = this.microsystem.getAllFromBrand(Brand.ACER);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, computers.size());

        for (Computer computer : computers) {
            boolean anyMatch = Arrays.stream(expected).anyMatch(value -> value == computer.getNumber());
            Assert.assertTrue(anyMatch);
        }
    }

    @Test
    public void test_getAllWithScreenSize_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Computer> computerIterable = this.microsystem.getAllWithScreenSize(1d);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithScreenSize_shouldReturnEmptyCollection_whenEntitiesAreRemoved() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 11d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 14.3, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 11d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        this.microsystem.remove(computer_3.getNumber());
        this.microsystem.remove(computer_4.getNumber());
        this.microsystem.removeWithBrand(Brand.ASUS);
        this.microsystem.removeWithBrand(Brand.ACER);


        Iterable<Computer> computerIterable = this.microsystem.getAllWithScreenSize(10d);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, computers.size());
    }

    @Test
    public void test_getAllWithScreenSize_shouldReturnSortedCollection() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 15.6, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 12d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        int[] expected = {computer_6.getNumber(), computer_5.getNumber(), computer_3.getNumber(), computer_2.getNumber()};

        Iterable<Computer> computerIterable = this.microsystem.getAllWithScreenSize(14.3);
        List<Computer> computers = StreamSupport.stream(computerIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(4, computers.size());

        int counter = 0;
        for (Computer computer : computerIterable) {
            Assert.assertEquals(expected[counter++], computer.getNumber());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_upgradeRam_shouldThrowException_whenComputerWithThatNumberDoesNotExists() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.upgradeRam(16, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_upgradeRam_shouldThrowException_whenComputerWithThatNumberHasBeenDeletedAndDoesNotExists() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.remove(1);
        this.microsystem.upgradeRam(16, 1);
    }

    @Test
    public void test_upgradeRam_shouldNotUpgradeRam_whenNewRamValueIsLessThanOnComputerWithGivenNumber() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);

        this.microsystem.upgradeRam(7, 1);
        Assert.assertEquals(8, computer_1.getRAM());
    }

    @Test
    public void test_upgradeRam_shouldUpgradeRamCorrectly() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);

        this.microsystem.upgradeRam(16, 1);
        Assert.assertEquals(16, computer_1.getRAM());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_remove_shouldThrowException_whenNoEntities() {
        this.microsystem.remove(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_remove_shouldThrowException_whenComputerWithGivenNumberDoesNotExists() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);

        this.microsystem.remove(101);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeWithBrand_shouldThrowException_whenNoEntities() {
        this.microsystem.removeWithBrand(Brand.HP);
    }

    @Test
    public void test_remove_shouldRemoveComputerSuccessfully() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);

        this.microsystem.remove(2);
        Assert.assertEquals(2, this.microsystem.count());
        Assert.assertFalse(this.microsystem.contains(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeWithBrand_shouldThrowException_whenComputersWithGivenBrandDoesNotExists() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);

        this.microsystem.removeWithBrand(Brand.DELL);
    }

    @Test
    public void test_removeWithBrand_shouldRemoveComputersWithGivenBrandSuccessfully() {
        Computer computer_1 = new Computer(1, Brand.ASUS, 10d, 13.3, "red");
        Computer computer_2 = new Computer(2, Brand.ACER, 13d, 14.3, "black");
        Computer computer_3 = new Computer(3, Brand.HP, 11d, 14.3, "red");
        Computer computer_4 = new Computer(4, Brand.DELL, 11d, 15.6, "black");
        Computer computer_5 = new Computer(5, Brand.ACER, 12d, 14.3, "red");
        Computer computer_6 = new Computer(6, Brand.ASUS, 11d, 14.3, "white");

        this.microsystem.createComputer(computer_1);
        this.microsystem.createComputer(computer_2);
        this.microsystem.createComputer(computer_3);
        this.microsystem.createComputer(computer_4);
        this.microsystem.createComputer(computer_5);
        this.microsystem.createComputer(computer_6);

        this.microsystem.removeWithBrand(Brand.ACER);
        Assert.assertEquals(4, this.microsystem.count());
        Assert.assertFalse(this.microsystem.contains(2));
        Assert.assertFalse(this.microsystem.contains(5));
    }

}
