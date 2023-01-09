package core;

import models.Package;

import java.util.*;
import java.util.stream.Collectors;

public class PackageManagerImpl implements PackageManager {

    private final Map<String, Package> packages;
    private final Map<Package, List<Package>> dependencies;
    private final Map<Package, List<Package>> dependants;
    private final Map<String, Package> versions;
    private final Comparator<Package> packageComparator = Comparator.comparing(Package::getReleaseDate).reversed().thenComparing(Package::getVersion);

    public PackageManagerImpl() {
        this.packages = new LinkedHashMap<>();
        this.dependencies = new LinkedHashMap<>();
        this.versions = new LinkedHashMap<>();
        this.dependants = new LinkedHashMap<>();
    }

    @Override
    public void registerPackage(Package _package) {
        if (this.packages.size() > 0) {
            for (Map.Entry<String, Package> entry : this.packages.entrySet()) {
                Package aPackage = entry.getValue();
                if (aPackage.getName().equals(_package.getName()) && aPackage.getVersion().equals(_package.getVersion())) {
                    throw new IllegalArgumentException();
                }
            }
        }
        this.packages.put(_package.getId(), _package);
        this.versions.put(_package.getName(), _package);
        this.dependants.computeIfAbsent(_package, aPackage -> new ArrayList<>());
        this.dependencies.computeIfAbsent(_package, aPackage -> new ArrayList<>());
    }

    @Override
    public void removePackage(String packageId) {
        if (!this.packages.containsKey(packageId)) {
            throw new IllegalArgumentException();
        }
        Package _package = this.packages.remove(packageId);
        List<Package> packageList = this.dependencies.remove(_package);
        for (Package aPackage : packageList) {
            this.dependants.get(aPackage).remove(_package);
        }
        List<Package> list = this.dependants.remove(_package);
        for (Package aPackage : list) {
            this.dependencies.get(aPackage).remove(_package);
        }
        this.versions.remove(_package.getVersion());
    }

    @Override
    public void addDependency(String packageId, String dependencyId) {
        if (!this.packages.containsKey(packageId) || !this.packages.containsKey(dependencyId)) {
            throw new IllegalArgumentException();
        }
        Package parent = this.packages.get(packageId);
        Package child = this.packages.get(dependencyId);
        this.dependencies.computeIfAbsent(parent, aPackage -> new ArrayList<>()).add(child);
        this.dependants.get(child).add(parent);
    }

    @Override
    public boolean contains(Package _package) {
        return this.packages.containsKey(_package.getId());
    }

    @Override
    public int size() {
        return this.packages.size();
    }

    @Override
    public Iterable<Package> getDependants(Package _package) {
        if (!this.dependants.containsKey(_package)) {
            return Collections.emptyList();
        }
        return this.dependants.get(_package);
    }

    @Override
    public Iterable<Package> getIndependentPackages() {
        return this.dependencies.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .sorted(packageComparator)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Package> getOrderedPackagesByReleaseDateThenByVersion() {
        return this.versions.values()
                .stream()
                .sorted(packageComparator)
                .collect(Collectors.toList());
    }
}
