package core;

import models.Doodle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class DoodleSearchTests {
    private interface InternalTest {
        void execute();
    }

    private DoodleSearch doodleSearch;

    private Doodle getRandomDoodle() {
        return new Doodle(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                (int) Math.min(1, Math.random() * 2_000),
                ((int) Math.min(1, Math.random() * 2_000_000_000) % 2 == 1),
                Math.min(1, Math.random() * 1000));
    }

    @Before
    public void setup() {
        this.doodleSearch = new DoodleSearchImpl();
    }

    public void performCorrectnessTesting(InternalTest[] methods) {
        Arrays.stream(methods)
                .forEach(method -> {
                    this.doodleSearch = new DoodleSearchImpl();

                    try {
                        method.execute();
                    } catch (IllegalArgumentException ignored) {
                    }
                });

        this.doodleSearch = new DoodleSearchImpl();
    }

    // Correctness Tests

    @Test
    public void testAddDoodle_WithCorrectData_ShouldSuccessfullyAddDoodle() {
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());

        assertEquals(2, this.doodleSearch.size());
    }

    @Test
    public void testContains_WithExistentDoodle_ShouldReturnTrue() {
        Doodle randomDoodle = this.getRandomDoodle();

        this.doodleSearch.addDoodle(randomDoodle);

        assertTrue(this.doodleSearch.contains(randomDoodle));
    }

    @Test
    public void testContains_WithNonexistentDoodle_ShouldReturnFalse() {
        Doodle randomDoodle = this.getRandomDoodle();

        this.doodleSearch.addDoodle(randomDoodle);

        assertFalse(this.doodleSearch.contains(this.getRandomDoodle()));
    }

    @Test
    public void testCount_With5Doodles_ShouldReturn5() {
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());
        this.doodleSearch.addDoodle(this.getRandomDoodle());

        assertEquals(5, this.doodleSearch.size());
    }

    @Test
    public void testCount_WithEmpty_ShouldReturnZero() {
        assertEquals(0, this.doodleSearch.size());
    }

    @Test
    public void testSearchDoodles_WithCorrectDoodles_ShouldReturnCorrectlyOrderedData() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        List<Doodle> Doodles = StreamSupport.stream(this.doodleSearch.searchDoodles("sd").spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(5, Doodles.size());
        assertEquals(Doodle5, Doodles.get(0));
        assertEquals(Doodle4, Doodles.get(1));
        assertEquals(Doodle, Doodles.get(2));
        assertEquals(Doodle3, Doodles.get(3));
        assertEquals(Doodle2, Doodles.get(4));
    }

    @Test
    public void test_getTotalRevenueFromDoodleAds_shouldReturnCorrect() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        double revenue = this.doodleSearch.getTotalRevenueFromDoodleAds();
        double actual = Doodle.getRevenue() * Doodle.getVisits() + Doodle4.getRevenue() * Doodle4.getVisits() + Doodle5.getRevenue() * Doodle5.getVisits();
        Assert.assertEquals(revenue, actual, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeDoodle_shouldThrowException_whenIdDoesNotExists() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        this.doodleSearch.removeDoodle(UUID.randomUUID().toString());
    }

    @Test
    public void test_removeDoodle_shouldReturnCorrect() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        this.doodleSearch.removeDoodle("hsd");
        Assert.assertEquals(5, this.doodleSearch.size());
        Assert.assertFalse(this.doodleSearch.contains(Doodle4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getDoodle_shouldThrowException_whenIdDoesNotExists() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        this.doodleSearch.getDoodle(UUID.randomUUID().toString());
    }

    @Test
    public void test_getDoodle_shouldReturnCorrect() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        Doodle doodle = this.doodleSearch.getDoodle("hsd");
        Assert.assertEquals(6, this.doodleSearch.size());
        Assert.assertTrue(this.doodleSearch.contains(Doodle4));
        Assert.assertEquals(doodle.getId(), Doodle4.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_visitDoodle_shouldThrowException_whenIdDoesNotExists() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        this.doodleSearch.visitDoodle(UUID.randomUUID().toString());
    }

    @Test
    public void test_visitDoodle_shouldReturnCorrect() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        this.doodleSearch.visitDoodle("qsd");
        Assert.assertEquals(6, this.doodleSearch.size());
        Assert.assertTrue(this.doodleSearch.contains(Doodle5));
        Assert.assertEquals(4002, Doodle5.getVisits());
    }

    @Test
    public void test_getDoodleAds_shouldReturnEmptyCollection_whenAllDoodlesAreNotAdds() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, false, 5.5);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, false, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, false, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);

        Iterable<Doodle> doodleAds = this.doodleSearch.getDoodleAds();
        List<Doodle> doodles = StreamSupport.stream(doodleAds.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, doodles.size());
    }

    @Test
    public void test_getDoodleAds_shouldReturnEmptyCollection_onEmptyCollection() {
        Iterable<Doodle> doodleAds = this.doodleSearch.getDoodleAds();
        List<Doodle> doodles = StreamSupport.stream(doodleAds.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, doodles.size());
    }

    @Test
    public void test_getDoodleAds_shouldReturnSortedCollection() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 4.8);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);
        Doodle Doodle6 = new Doodle("zsd", "ds", 5000, false, 5.7);
        Doodle Doodle7 = new Doodle("fsd", "asd", 5000, true, 5.6);
        Doodle Doodle8 = new Doodle("msd", "dsm", 5000, true, 5.7);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);
        this.doodleSearch.addDoodle(Doodle6);
        this.doodleSearch.addDoodle(Doodle7);
        this.doodleSearch.addDoodle(Doodle8);

        String[] expected = {Doodle8.getId(), Doodle7.getId(), Doodle5.getId(), Doodle.getId(), Doodle4.getId()};

        Iterable<Doodle> doodleAds = this.doodleSearch.getDoodleAds();
        List<Doodle> doodles = StreamSupport.stream(doodleAds.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(5, doodles.size());
        int counter = 0;
        for (Doodle doodle : doodles) {
            Assert.assertEquals(expected[counter++], doodle.getId());
        }
    }

    @Test
    public void test_getTop3DoodlesByRevenueThenByVisits_shouldReturnEmptyCollection_onEmptyCollection() {
        Iterable<Doodle> doodleAds = this.doodleSearch.getTop3DoodlesByRevenueThenByVisits();
        List<Doodle> doodles = StreamSupport.stream(doodleAds.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, doodles.size());
    }

    @Test
    public void test_getTop3DoodlesByRevenueThenByVisits_shouldReturnSortedCollection() {
        Doodle Doodle = new Doodle("asd", "bbbsd", 4000, true, 4.8);
        Doodle Doodle2 = new Doodle("nsd", "eesd", 4000, false, 5.6);
        Doodle Doodle3 = new Doodle("dsd", "ddsd", 5000, false, 5.7);
        Doodle Doodle4 = new Doodle("hsd", "zsd", 4000, true, 4.8);
        Doodle Doodle5 = new Doodle("qsd", "qsd", 4001, true, 4.8);

        this.doodleSearch.addDoodle(Doodle);
        this.doodleSearch.addDoodle(Doodle2);
        this.doodleSearch.addDoodle(Doodle3);
        this.doodleSearch.addDoodle(Doodle4);
        this.doodleSearch.addDoodle(Doodle5);

        String[] expected = {Doodle3.getId(), Doodle2.getId(), Doodle5.getId()};

        Iterable<Doodle> doodleAds = this.doodleSearch.getTop3DoodlesByRevenueThenByVisits();
        List<Doodle> doodles = StreamSupport.stream(doodleAds.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, doodles.size());
        int counter = 0;
        for (Doodle doodle : doodles) {
            Assert.assertEquals(expected[counter++], doodle.getId());
        }
    }
}
