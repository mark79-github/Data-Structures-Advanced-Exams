package core;

import models.Route;

import java.util.*;
import java.util.stream.Collectors;

public class MoovItImpl implements MoovIt {

    private final Map<String, Route> routes;
    private final Set<Route> uniqueRoutes;

    public MoovItImpl() {
        this.routes = new LinkedHashMap<>();
        this.uniqueRoutes = new LinkedHashSet<>();
    }

    @Override
    public void addRoute(Route route) {
        if (this.contains(route)) {
            throw new IllegalArgumentException();
        }
        this.routes.put(route.getId(), route);
        this.uniqueRoutes.add(route);
    }

    @Override
    public void removeRoute(String routeId) {
        if (!this.routes.containsKey(routeId)) {
            throw new IllegalArgumentException();
        }
        Route route = this.routes.remove(routeId);
        this.uniqueRoutes.remove(route);
    }

    @Override
    public boolean contains(Route route) {
        return this.routes.containsKey(route.getId()) || this.uniqueRoutes.contains(route);
    }

    @Override
    public int size() {
        return this.routes.size();
    }

    @Override
    public Route getRoute(String routeId) {
        if (!this.routes.containsKey(routeId)) {
            throw new IllegalArgumentException();
        }
        return this.routes.get(routeId);
    }

    @Override
    public void chooseRoute(String routeId) {
        Route route = this.getRoute(routeId);
        route.setPopularity(route.getPopularity() + 1);
    }

    @Override
    public Iterable<Route> searchRoutes(String startPoint, String endPoint) {
        return this.routes.values()
                .stream()
                .filter(route -> {
                    List<String> locationPoints = route.getLocationPoints();
                    int startPointIndex = locationPoints.indexOf(startPoint);
                    int endPointIndex = locationPoints.indexOf(endPoint);
                    return startPointIndex != -1 && endPointIndex != -1 && startPointIndex < endPointIndex;
                })
                .sorted((o1, o2) -> {
                    if (Boolean.compare(o2.getIsFavorite(), o1.getIsFavorite()) == 0) {
                        int o1Distance = getDistance(o1.getLocationPoints(), startPoint, endPoint);
                        int o2Distance = getDistance(o2.getLocationPoints(), startPoint, endPoint);
                        if (o1Distance == o2Distance) {
                            return Integer.compare(o2.getPopularity(), o1.getPopularity());
                        }
                        return Integer.compare(o2Distance, o1Distance);
                    }
                    return Boolean.compare(o2.getIsFavorite(), o1.getIsFavorite());
                })
                .collect(Collectors.toList());
    }

    private int getDistance(List<String> locations, String startPoint, String endPoint) {
        int startPointIndex = locations.indexOf(startPoint);
        int endPointIndex = locations.indexOf(endPoint);
        return startPointIndex - endPointIndex;
    }

    @Override
    public Iterable<Route> getFavoriteRoutes(String destinationPoint) {
        return this.routes.values()
                .stream()
                .filter(route ->
                        route.getIsFavorite()
                                && route.getLocationPoints() != null
                                && !route.getLocationPoints().isEmpty()
                                && !route.getLocationPoints().get(0).equals(destinationPoint)
                                && route.getLocationPoints().contains(destinationPoint)
                )
                .sorted((o1, o2) -> {
                    if (Double.compare(o1.getDistance(), o2.getDistance()) == 0) {
                        return Integer.compare(o2.getPopularity(), o1.getPopularity());
                    }
                    return Double.compare(o1.getDistance(), o2.getDistance());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Route> getTop5RoutesByPopularityThenByDistanceThenByCountOfLocationPoints() {
        return this.routes.values()
                .stream()
                .sorted((o1, o2) -> {
                    if ((int) o2.getPopularity() == o1.getPopularity()) {
                        if (Double.compare(o1.getDistance(), o2.getDistance()) == 0) {
                            return Integer.compare(o1.getLocationPoints().size(), o2.getLocationPoints().size());
                        }
                        return Double.compare(o1.getDistance(), o2.getDistance());
                    }
                    return Integer.compare(o2.getPopularity(), o1.getPopularity());
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}
