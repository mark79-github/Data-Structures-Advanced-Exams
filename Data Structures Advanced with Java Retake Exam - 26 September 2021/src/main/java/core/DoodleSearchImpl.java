package core;

import models.Doodle;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DoodleSearchImpl implements DoodleSearch {

    private final Set<Doodle> doodles;
    private final Map<String, Doodle> titlesWithDoodles;
    private final Map<String, Doodle> idsWithDoodles;

    public DoodleSearchImpl() {
        this.doodles = new LinkedHashSet<>();
        titlesWithDoodles = new LinkedHashMap<>();
        idsWithDoodles = new LinkedHashMap<>();
    }

    @Override
    public void addDoodle(Doodle doodle) {
        this.doodles.add(doodle);
        this.titlesWithDoodles.put(doodle.getTitle(), doodle);
        this.idsWithDoodles.put(doodle.getId(), doodle);
    }

    @Override
    public void removeDoodle(String doodleId) {
        if (!this.idsWithDoodles.containsKey(doodleId)){
            throw new IllegalArgumentException();
        }
        Doodle doodle = this.idsWithDoodles.remove(doodleId);
        this.titlesWithDoodles.remove(doodle.getTitle());
        this.doodles.remove(doodle);
    }

    @Override
    public int size() {
        return this.doodles.size();
    }

    @Override
    public boolean contains(Doodle doodle) {
        return this.doodles.contains(doodle);
    }

    @Override
    public Doodle getDoodle(String id) {
        Doodle doodle = this.idsWithDoodles.get(id);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }
        return doodle;
    }

    @Override
    public double getTotalRevenueFromDoodleAds() {
        return this.doodles
                .stream()
                .filter(Doodle::getIsAd)
                .mapToDouble(value -> value.getRevenue() * value.getVisits())
                .sum();
    }

    @Override
    public void visitDoodle(String title) {
        Doodle doodle = this.titlesWithDoodles.get(title);
        if (doodle == null) {
            throw new IllegalArgumentException();
        }
        doodle.setVisits(doodle.getVisits() + 1);
    }

    @Override
    public Iterable<Doodle> searchDoodles(String searchQuery) {
        return this.doodles
                .stream()
                .filter(doodle -> doodle.getTitle().contains(searchQuery))
                .sorted((o1, o2) -> {
                    if (Boolean.compare(o1.getIsAd(), o2.getIsAd()) == 0) {
                        if (o1.getTitle().length() == o2.getTitle().length()) {
                            return Integer.compare(o2.getVisits(), o1.getVisits());
                        }
                        return Integer.compare(o1.getTitle().length(), o2.getTitle().length());
                    }
                    return Boolean.compare(o2.getIsAd(), o1.getIsAd());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getDoodleAds() {
        return this.doodles
                .stream()
                .filter(Doodle::getIsAd)
                .sorted((o1, o2) -> {
                    if (Double.compare(o2.getRevenue(), o1.getRevenue()) == 0) {
                        return Integer.compare(o2.getVisits(), o1.getVisits());
                    }
                    return Double.compare(o2.getRevenue(), o1.getRevenue());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getTop3DoodlesByRevenueThenByVisits() {
        return this.doodles
                .stream()
                .sorted((o1, o2) -> {
                    if (Double.compare(o2.getRevenue(), o1.getRevenue()) == 0) {
                        return Integer.compare(o2.getVisits(), o1.getVisits());
                    }
                    return Double.compare(o2.getRevenue(), o1.getRevenue());
                })
                .limit(3)
                .collect(Collectors.toList());
    }
}
