package core;

import models.Route;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class MoovItTests {
    private interface InternalTest {
        void execute();
    }

    private MoovIt moovIt;

    private List<String> getRandomLocationPoints() {
        int randomLength = (int) Math.max(5, Math.random() * 10);

        List<String> randomLocationPoints = new ArrayList<>();

        for (int i = 0; i < randomLength; i++) {
            randomLocationPoints.add(UUID.randomUUID().toString());
        }

        return randomLocationPoints;
    }

    private Route getRandomRoute() {
        return new Route(
                UUID.randomUUID().toString(),
                Math.min(1, Math.random() * 1_000_000),
                (int) Math.min(1, Math.random() * 1_000),
                (int) Math.min(1, Math.random() * 1_000) > 500,
                getRandomLocationPoints());
    }

    @Before
    public void setup() {
        this.moovIt = new MoovItImpl();
    }

    // Correctness Tests

    @Test
    public void testAddRoute_WithCorrectData_ShouldSuccessfullyAddRoute() {
        this.moovIt.addRoute(getRandomRoute());
        this.moovIt.addRoute(getRandomRoute());

        assertEquals(2, this.moovIt.size());
    }

    @Test
    public void testContains_WithEqualRoute_ShouldReturnTrue() {
        Route route = new Route("Test1", 10D, 1, false, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route2 = new Route("Test2", 10D, 1, false, List.of("Sofia", "Pleven", "Veliko Turnovo", "Varna", "Burgas"));

        this.moovIt.addRoute(route);

        assertTrue(this.moovIt.contains(route2));
    }

    @Test
    public void testCount_With5Routes_ShouldReturn5() {
        this.moovIt.addRoute(this.getRandomRoute());
        this.moovIt.addRoute(this.getRandomRoute());
        this.moovIt.addRoute(this.getRandomRoute());
        this.moovIt.addRoute(this.getRandomRoute());
        this.moovIt.addRoute(this.getRandomRoute());

        assertEquals(5, this.moovIt.size());
    }

    @Test
    public void testChooseRoute_WithCorrectRoute_ShouldReactToRoute() {
        Route route = this.getRandomRoute();

        Integer expected = route.getPopularity() + 1;

        this.moovIt.addRoute(route);

        this.moovIt.chooseRoute(route.getId());

        Route received = this.moovIt.getRoute(route.getId());

        assertEquals(received.getPopularity(), expected);
    }

    @Test
    public void testSearchRoutes_WithContainedPoints_ShouldReturnCorrectRoutes() {
        Route route = new Route("Test1", 10D, 200, false, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route2 = new Route("Test2", 10D, 1, false, List.of("Vidin", "Pleven", "Veliko Turnovo", "Varna", "Burgas"));
        Route route3 = new Route("Test3", 10D, 400, false, List.of("Vraca", "Plovdiv", "Stara Zagora", "Varna", "Burgas"));
        Route route4 = new Route("Test4", 500D, 500, false, List.of("Sofia", "Plovdiv", "Stara Zagora", "Varna", "Burgas"));

        this.moovIt.addRoute(route);
        this.moovIt.addRoute(route2);
        this.moovIt.addRoute(route3);
        this.moovIt.addRoute(route4);

        List<Route> routes = StreamSupport.stream(this.moovIt.searchRoutes("Plovdiv", "Burgas").spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(3, routes.size());
        assertEquals(route, routes.get(0));
        assertEquals(route4, routes.get(1));
        assertEquals(route3, routes.get(2));
    }

    @Test
    public void testRemoveRoute_WithCorrectData_ShouldSuccessfullyRemoveRoute() {
        Route route = new Route("Test1", 100D, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route2 = new Route("Test2", 100D, 50, true, List.of("Vidin", "Pleven", "Burgas"));
        Route route3 = new Route("Test3", 10D, 100, true, List.of("Vraca", "Plovdiv", "Stara Zagora", "Burgas"));

        this.moovIt.addRoute(route);
        this.moovIt.addRoute(route2);
        this.moovIt.addRoute(route3);

        this.moovIt.removeRoute("Test2");

        assertFalse(this.moovIt.contains(route2));
    }

    // Performance Tests

    @Test
    public void testAddRoute_With100000Results_ShouldPassQuickly() {
        List<Route> routesToAdd = new ArrayList<>();

        int count = 100000;

        for (int i = 0; i < count; i++) {
            routesToAdd.add(new Route(i + "", i * 1000D, i * 100, false, getRandomLocationPoints()));
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            this.moovIt.addRoute(routesToAdd.get(i));
        }

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime < 450);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addRoute_shouldThrowException_whenRouteAlreadyExists() {
        Route route = new Route("Test1", 100d, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route nextRoute = new Route("Test1", 200d, 100, false, List.of("Ruse", "Stara Zagora", "Plovdiv"));
        this.moovIt.addRoute(route);
        this.moovIt.addRoute(nextRoute);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeRoute_shouldThrowException_whenRouteIdDoesNotExists() {
        Route route_1 = new Route("Test1", 100d, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route_2 = new Route("Test2", 200d, 100, false, List.of("Ruse", "Stara Zagora", "Plovdiv"));
        this.moovIt.addRoute(route_1);
        this.moovIt.addRoute(route_2);

        this.moovIt.removeRoute(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getRoute_shouldThrowException_whenRouteIdDoesNotExists() {
        Route route_1 = new Route("Test1", 100d, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route_2 = new Route("Test2", 200d, 100, false, List.of("Ruse", "Stara Zagora", "Plovdiv"));
        this.moovIt.addRoute(route_1);
        this.moovIt.addRoute(route_2);

        this.moovIt.getRoute(UUID.randomUUID().toString());
    }

    @Test
    public void test_getFavoriteRoutes_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Route> routeIterable = this.moovIt.getFavoriteRoutes("New York");
        List<Route> routes = StreamSupport.stream(routeIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, routes.size());
    }

    @Test
    public void test_getFavoriteRoutes_shouldReturnEmptyCollection_whenDestinationPointDoesNotExists() {
        Route route_1 = new Route("Test1", 100d, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route_2 = new Route("Test2", 200d, 100, false, List.of("Ruse", "Stara Zagora", "Plovdiv"));

        this.moovIt.addRoute(route_1);
        this.moovIt.addRoute(route_2);

        Iterable<Route> routeIterable = this.moovIt.getFavoriteRoutes("New York");
        List<Route> routes = StreamSupport.stream(routeIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, routes.size());
    }

    @Test
    public void test_getFavoriteRoutes_shouldReturnSortedCollection() {
        Route route_1 = new Route("Test1", 200d, 50, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route_2 = new Route("Test2", 200d, 100, true, List.of("Ruse", "Stara Zagora", "Plovdiv"));
        Route route_3 = new Route("Test3", 200d, 100, true, List.of("Plovdiv", "Stara Zagora", "Burgas"));
        Route route_4 = new Route("Test4", 100d, 100, true, List.of("Burgas", "Pleven", "Plovdiv"));
        Route route_5 = new Route("Test5", 200d, 100, false, List.of("Haskovo", "Plovdiv", "Ruse"));

        this.moovIt.addRoute(route_1);
        this.moovIt.addRoute(route_2);
        this.moovIt.addRoute(route_3);
        this.moovIt.addRoute(route_4);
        this.moovIt.addRoute(route_5);

        String[] expected = {route_4.getId(), route_2.getId(), route_1.getId()};

        Iterable<Route> routeIterable = this.moovIt.getFavoriteRoutes("Plovdiv");
        List<Route> routes = StreamSupport.stream(routeIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, routes.size());
        int counter = 0;
        for (Route route : routes) {
            Assert.assertEquals(expected[counter++], route.getId());
        }
    }

    @Test
    public void test_getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints_shouldReturnEmptyCollection_whenNoEntities() {
        Iterable<Route> routeIterable = this.moovIt.getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints();
        List<Route> routes = StreamSupport.stream(routeIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, routes.size());
    }

    @Test
    public void test_getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints_shouldReturnSortedCollection() {
        Route route_1 = new Route("Test1", 200d, 100, true, List.of("Sofia", "Plovdiv", "Stara Zagora", "Burgas"));
        Route route_2 = new Route("Test2", 100d, 200, true, List.of("Ruse", "Stara Zagora", "Plovdiv"));
        Route route_3 = new Route("Test3", 100d, 200, true, List.of("Plovdiv", "Stara Zagora", "Burgas"));
        Route route_4 = new Route("Test4", 200d, 100, true, List.of("Burgas", "Pleven", "Plovdiv"));
        Route route_5 = new Route("Test5", 100d, 200, false, List.of("Haskovo", "Plovdiv"));
        Route route_6 = new Route("Test6", 150d, 200, true, List.of("Haskovo", "Plovdiv", "Ruse"));

        this.moovIt.addRoute(route_1);
        this.moovIt.addRoute(route_2);
        this.moovIt.addRoute(route_3);
        this.moovIt.addRoute(route_4);
        this.moovIt.addRoute(route_5);
        this.moovIt.addRoute(route_6);

        String[] expected = {route_5.getId(), route_2.getId(), route_3.getId(), route_6.getId(), route_4.getId() };

        Iterable<Route> routeIterable = this.moovIt.getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints();
        List<Route> routes = StreamSupport.stream(routeIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(5, routes.size());
        int counter = 0;
        for (Route route : routes) {
            Assert.assertEquals(expected[counter++], route.getId());
        }
    }
}
