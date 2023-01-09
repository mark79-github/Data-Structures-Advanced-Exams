package core;

import models.Package;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackageManagerImplTest {

    private final PackageManagerImpl packageManager;

    public PackageManagerImplTest() {
        this.packageManager = new PackageManagerImpl();
    }

    @Test
    public void test_getIndependentPackages() {
        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getIndependentPackages_v2() {
        Package package_1 = new Package(UUID.randomUUID().toString(), "1", "1", LocalDateTime.now());
        Package package_2 = new Package(UUID.randomUUID().toString(), "2", "2", LocalDateTime.now());
        Package package_3 = new Package(UUID.randomUUID().toString(), "3", "3", LocalDateTime.now());

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(3, packageList.size());
    }

    @Test
    public void test_getIndependentPackages_v3() {
        Package package_1 = new Package(UUID.randomUUID().toString(), "1", "1", LocalDateTime.now());
        Package package_2 = new Package(UUID.randomUUID().toString(), "2", "2", LocalDateTime.now());
        Package package_3 = new Package(UUID.randomUUID().toString(), "3", "3", LocalDateTime.now());

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        this.packageManager.addDependency(package_1.getId(), package_3.getId());
        this.packageManager.addDependency(package_2.getId(), package_3.getId());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(1, packageList.size());
        Assert.assertEquals(package_3.getId(), packageList.get(0).getId());
    }

    @Test
    public void test_getIndependentPackages_v4() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        this.packageManager.addDependency(package_1.getId(), package_2.getId());
        this.packageManager.addDependency(package_2.getId(), package_3.getId());
        this.packageManager.removePackage(package_3.getId());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(1, packageList.size());
        Assert.assertEquals(package_2.getId(), packageList.get(0).getId());
    }

    @Test
    public void test_registerPackage_shouldAddPackagesCorrectly() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        Assert.assertEquals(3, this.packageManager.size());
        Assert.assertTrue(this.packageManager.contains(package_3));
        Assert.assertTrue(this.packageManager.contains(package_2));
        Assert.assertTrue(this.packageManager.contains(package_1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_registerPackage_shouldThrowException_WhenNameAndVersionAlreadyExists() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "1", "1", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removePackage_shouldThrowException_whenPackageIdDoesNotExists() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "1", "1", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);

        this.packageManager.removePackage(package_3.getId());
    }

    @Test
    public void test_removePackage_shouldRemovePackageSuccessfully_whenRemovingIndependentPackage() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        this.packageManager.removePackage(package_1.getId());

        Assert.assertEquals(2, this.packageManager.size());
        Assert.assertFalse(this.packageManager.contains(package_1));
        Assert.assertTrue(this.packageManager.contains(package_2));
        Assert.assertTrue(this.packageManager.contains(package_3));
    }

    @Test
    public void test_removePackage_shouldRemovePackageSuccessfully_whenRemovingRootWithItsDependencies() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.addDependency(package_2.getId(), package_1.getId());
        this.packageManager.addDependency(package_3.getId(), package_1.getId());

        this.packageManager.removePackage(package_1.getId());

        Assert.assertEquals(2, this.packageManager.size());
        Assert.assertFalse(this.packageManager.contains(package_1));
        Assert.assertTrue(this.packageManager.contains(package_2));
        Assert.assertTrue(this.packageManager.contains(package_3));
    }

    @Test
    public void test_removePackage_shouldRemovePackageSuccessfully_whenRemoveDependantPackage() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.addDependency(package_2.getId(), package_1.getId());

        this.packageManager.removePackage(package_2.getId());

        Assert.assertEquals(1, this.packageManager.size());
        Assert.assertTrue(this.packageManager.contains(package_1));
        Assert.assertFalse(this.packageManager.contains(package_2));

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(1, packageList.size());
        Assert.assertEquals(package_1.getId(), packageList.get(packageList.size() - 1).getId());
    }


    @Test
    public void test_getDependants_shouldReturnEmptyList_whenRepoIsEmpty() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Iterable<Package> packageIterable = this.packageManager.getDependants(package_1);

        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getDependants_shouldReturnEmptyList_whenNoDependencies() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);

        Assert.assertEquals(3, this.packageManager.size());
        Assert.assertTrue(this.packageManager.contains(package_1));
        Assert.assertTrue(this.packageManager.contains(package_2));
        Assert.assertTrue(this.packageManager.contains(package_3));

        Iterable<Package> packageIterable = this.packageManager.getDependants(package_1);
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());

        packageIterable = this.packageManager.getDependants(package_2);
        packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());

        packageIterable = this.packageManager.getDependants(package_3);
        packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getDependants_shouldReturnCorrectly() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.addDependency(package_2.getId(), package_1.getId());
        this.packageManager.addDependency(package_3.getId(), package_1.getId());

        Assert.assertEquals(3, this.packageManager.size());
        Assert.assertTrue(this.packageManager.contains(package_1));
        Assert.assertTrue(this.packageManager.contains(package_2));
        Assert.assertTrue(this.packageManager.contains(package_3));

        Iterable<Package> packageIterable = this.packageManager.getDependants(package_1);
        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(2, packageList.size());
        String[] expected = {package_2.getId(), package_3.getId()};
        int counter = 0;
        for (Package _package : packageIterable) {
            Assert.assertEquals(expected[counter++], _package.getId());
        }

        packageIterable = this.packageManager.getDependants(package_2);
        packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());

        packageIterable = this.packageManager.getDependants(package_3);
        packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addDependency_shouldThrowException_WhenParentPackageIdDoesNotExists() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);

        this.packageManager.addDependency(package_2.getId(), package_3.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addDependency_shouldThrowException_WhenChildPackageIdDoesNotExists() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);

        this.packageManager.addDependency(package_2.getId(), package_1.getId());
        this.packageManager.addDependency(package_3.getId(), package_1.getId());
    }

    @Test
    public void test_getIndependentPackages_shouldReturnEmptyCollection_WhenRepoIsEmpty() {
        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();

        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getIndependentPackages_shouldReturnCorrectly_AfterDeletingDependentPackages() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        Package package_4 = new Package("4", "4", "4", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);
        this.packageManager.addDependency(package_2.getId(), package_1.getId());
        this.packageManager.addDependency(package_3.getId(), package_1.getId());
        this.packageManager.addDependency(package_4.getId(), package_2.getId());
        this.packageManager.removePackage(package_2.getId());

        Assert.assertEquals(3, this.packageManager.size());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();

        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(2, packageList.size());
    }

    @Test
    public void test_getIndependentPackages_shouldReturnSortedCorrectlyByVersion() {
        LocalDateTime dateTime = LocalDateTime.now();
        Package package_1 = new Package("1", "1", "AA", dateTime);
        Package package_2 = new Package("2", "2", "aA", dateTime);
        Package package_3 = new Package("3", "3", "Aa", dateTime);
        Package package_4 = new Package("4", "4", "aa", dateTime);
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);

        Assert.assertEquals(4, this.packageManager.size());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();

        String[] expected = {package_1.getId(), package_3.getId(), package_2.getId(), package_4.getId()};
        int counter = 0;
        for (Package _package : packageIterable) {
            Assert.assertEquals(expected[counter++], _package.getId());
        }
    }

    @Test
    public void test_getIndependentPackages_shouldReturnSortedCorrectlyByReleaseDate() {
        LocalDateTime dateTime = LocalDateTime.now();
        Package package_1 = new Package("1", "1", "a", dateTime.plusHours(4));
        Package package_2 = new Package("2", "2", "a", dateTime.plusHours(1));
        Package package_3 = new Package("3", "3", "a", dateTime.plusHours(2));
        Package package_4 = new Package("4", "4", "a", dateTime.plusHours(3));
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);

        Assert.assertEquals(4, this.packageManager.size());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();

        String[] expected = {package_1.getId(), package_4.getId(), package_3.getId(), package_2.getId()};
        int counter = 0;
        for (Package _package : packageIterable) {
            Assert.assertEquals(expected[counter++], _package.getId());
        }
    }

    @Test
    public void test_getIndependentPackages_shouldReturnSortedCorrectly() {
        LocalDateTime dateTime = LocalDateTime.now();
        Package package_1 = new Package("1", "1", "a", dateTime.plusHours(4));
        Package package_2 = new Package("2", "2", "aa", dateTime.plusHours(1));
        Package package_3 = new Package("3", "3", "a", dateTime.plusHours(2));
        Package package_4 = new Package("4", "4", "aA", dateTime.plusHours(1));
        Package package_5 = new Package("5", "5", "Aa", dateTime.plusHours(1));
        Package package_6 = new Package("6", "6", "AA", dateTime.plusHours(1));

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);
        this.packageManager.registerPackage(package_5);
        this.packageManager.registerPackage(package_6);


        Assert.assertEquals(6, this.packageManager.size());

        Iterable<Package> packageIterable = this.packageManager.getIndependentPackages();

        String[] expected = {package_1.getId(), package_3.getId(), package_6.getId(), package_5.getId(), package_4.getId(), package_2.getId()};
        int counter = 0;
        for (Package _package : packageIterable) {
            Assert.assertEquals(expected[counter++], _package.getId());
        }
    }

    @Test
    public void test_getOrderedPackagesByReleaseDateThenByVersion_shouldReturnEmptyList_whenRepoIsEmpty() {
        Iterable<Package> packageIterable = this.packageManager.getOrderedPackagesByReleaseDateThenByVersion();

        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getOrderedPackagesByReleaseDateThenByVersion_shouldReturnEmptyList_whenAllPackagesAreDeleted() {
        Package package_1 = new Package("1", "1", "1", LocalDateTime.now());
        Package package_2 = new Package("2", "2", "2", LocalDateTime.now());
        Package package_3 = new Package("3", "3", "3", LocalDateTime.now());
        Package package_4 = new Package("4", "4", "4", LocalDateTime.now());
        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);
        this.packageManager.removePackage(package_1.getId());
        this.packageManager.removePackage(package_2.getId());
        this.packageManager.removePackage(package_3.getId());
        this.packageManager.removePackage(package_4.getId());
        Iterable<Package> packageIterable = this.packageManager.getOrderedPackagesByReleaseDateThenByVersion();

        List<Package> packageList = StreamSupport.stream(packageIterable.spliterator(), false).collect(Collectors.toList());
        Assert.assertEquals(0, packageList.size());
    }

    @Test
    public void test_getOrderedPackagesByReleaseDateThenByVersion_shouldReturnSortedCorrectly() {
        LocalDateTime dateTime = LocalDateTime.now();
        Package package_1 = new Package("1", "1", "a", dateTime.plusHours(4));
        Package package_2 = new Package("2", "2", "aa", dateTime.plusHours(1));
        Package package_3 = new Package("3", "3", "a", dateTime.plusHours(2));
        Package package_4 = new Package("4", "4", "aA", dateTime.plusHours(1));
        Package package_5 = new Package("5", "5", "Aa", dateTime.plusHours(1));
        Package package_6 = new Package("6", "6", "AA", dateTime.plusHours(1));

        this.packageManager.registerPackage(package_1);
        this.packageManager.registerPackage(package_2);
        this.packageManager.registerPackage(package_3);
        this.packageManager.registerPackage(package_4);
        this.packageManager.registerPackage(package_5);
        this.packageManager.registerPackage(package_6);


        Assert.assertEquals(6, this.packageManager.size());

        Iterable<Package> packageIterable = this.packageManager.getOrderedPackagesByReleaseDateThenByVersion();

        String[] expected = {package_1.getId(), package_3.getId(), package_6.getId(), package_5.getId(), package_4.getId(), package_2.getId()};
        int counter = 0;
        for (Package _package : packageIterable) {
            Assert.assertEquals(expected[counter++], _package.getId());
        }
    }
}