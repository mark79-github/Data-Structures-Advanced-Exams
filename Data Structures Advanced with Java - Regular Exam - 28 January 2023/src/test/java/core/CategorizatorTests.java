package core;

import models.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

public class CategorizatorTests {
    private interface InternalTest {
        void execute();
    }

    private Categorizator categorizator;

    private Category getRandomCategory() {
        return new Category(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
    }

    @Before
    public void setup() {
        this.categorizator = new CategorizatorImpl();
    }

    public void performCorrectnessTesting(InternalTest[] methods) {
        Arrays.stream(methods)
                .forEach(method -> {
                    this.categorizator = new CategorizatorImpl();

                    try {
                        method.execute();
                    } catch (IllegalArgumentException ignored) {
                    }
                });

        this.categorizator = new CategorizatorImpl();
    }

    // Correctness Tests

    @Test
    public void testSize_ShouldReturnCorrectResults() {
        this.categorizator.addCategory(getRandomCategory());
        this.categorizator.addCategory(getRandomCategory());
        this.categorizator.addCategory(getRandomCategory());

        assertEquals(3, this.categorizator.size());
    }

    @Test
    public void testAddCategory_WithDuplicate_ShouldThrow() {
        Category category = this.getRandomCategory();
        this.categorizator.addCategory(category);

        // Little bit of hacks
        try {
            this.categorizator.addCategory(category);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }

        assertTrue(false);
    }

    @Test
    public void testContains_WithExistentCategory_ShouldReturnTrue() {
        Category category = getRandomCategory();
        this.categorizator.addCategory(category);

        assertTrue(this.categorizator.contains(category));
    }

    @Test
    public void testContains_WithNonExistentCategory_ShouldReturnFalse() {
        Category category = getRandomCategory();
        this.categorizator.addCategory(category);

        assertFalse(this.categorizator.contains(getRandomCategory()));
    }

    @Test
    public void testAssignParent_WithDuplicateCategories_ShouldReturnCorrectResults() {
        Category childCategory = getRandomCategory();
        Category parentCategory = getRandomCategory();
        this.categorizator.addCategory(childCategory);
        this.categorizator.addCategory(parentCategory);

        this.categorizator.assignParent(childCategory.getId(), parentCategory.getId());

        // Little bit of hacks
        try {
            this.categorizator.assignParent(childCategory.getId(), parentCategory.getId());
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }

        assertTrue(false);
    }

    // Performance Tests

    @Test
    public void testContains_With100000Results_ShouldPassQuickly() {
        this.performCorrectnessTesting(new InternalTest[]{
                this::testContains_WithExistentCategory_ShouldReturnTrue,
                this::testContains_WithNonExistentCategory_ShouldReturnFalse,
        });

        int count = 100000;

        Category categoryToContain = null;

        for (int i = 0; i < count; i++) {
            if (i == count / 2) {
                categoryToContain = getRandomCategory();
                this.categorizator.addCategory(categoryToContain);
            } else {
                this.categorizator.addCategory(getRandomCategory());
            }
        }

        long start = System.currentTimeMillis();

        this.categorizator.contains(categoryToContain);

        long stop = System.currentTimeMillis();

        long elapsedTime = stop - start;

        assertTrue(elapsedTime <= 5);
    }

    @Test
    public void test_getChildren() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");
        category_5.setName("5");
        Category category_6 = getRandomCategory();
        category_6.setId("6");
        category_6.setName("6");
        Category category_7 = getRandomCategory();
        category_7.setId("7");
        category_7.setName("7");


        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);
        this.categorizator.addCategory(category_6);
        this.categorizator.addCategory(category_7);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_2.getId());
        this.categorizator.assignParent(category_6.getId(), category_5.getId());
        this.categorizator.assignParent(category_7.getId(), category_3.getId());

        Iterable<Category> categoryIterable = this.categorizator.getChildren(category_2.getId());
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, categoryList.size());
        String[] expected = {category_4.getId(), category_5.getId(), category_6.getId()};
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expected[i], categoryList.get(i).getId());
        }
    }

    @Test
    public void test_getHierarchy() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");


        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_2.getId());

        Iterable<Category> categoryIterable = this.categorizator.getHierarchy(category_4.getId());
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, categoryList.size());

        String[] expected = {category_1.getId(), category_2.getId(), category_4.getId()};
        int counter = 0;
        for (Category category : categoryIterable) {
            Assert.assertEquals(expected[counter++], category.getId());
        }
    }

    @Test
    public void test_getTop3CategoriesOrderedByDepthOfChildrenThenByName() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");
        category_5.setName("5");
        Category category_6 = getRandomCategory();
        category_6.setId("6");
        category_6.setName("6");
        Category category_7 = getRandomCategory();
        category_7.setId("7");
        category_7.setName("7");


        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);
        this.categorizator.addCategory(category_6);
        this.categorizator.addCategory(category_7);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_2.getId());
        this.categorizator.assignParent(category_6.getId(), category_5.getId());
        this.categorizator.assignParent(category_7.getId(), category_3.getId());

        Iterable<Category> categoryIterable = this.categorizator.getTop3CategoriesOrderedByDepthOfChildrenThenByName();
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        String[] expected = {category_1.getId(), category_2.getId(), category_3.getId()};
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expected[i], categoryList.get(i).getId());
        }
    }

    @Test
    public void test_getTop3CategoriesOrderedByDepthOfChildrenThenByName_v2() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");

        this.categorizator.addCategory(category_1);

        Iterable<Category> categoryIterable = this.categorizator.getTop3CategoriesOrderedByDepthOfChildrenThenByName();
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        String[] expected = {category_1.getId()};
        Assert.assertEquals(1, categoryList.size());
        Assert.assertEquals(expected[0], categoryList.get(0).getId());
    }

    @Test
    public void test_getTop3CategoriesOrderedByDepthOfChildrenThenByName_v3() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("Aa");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("аA");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("АA");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());

        Iterable<Category> categoryIterable = this.categorizator.getTop3CategoriesOrderedByDepthOfChildrenThenByName();
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        String[] expected = {category_1.getId(), category_2.getId(), category_4.getId()};
        Assert.assertEquals(3, categoryList.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expected[i], categoryList.get(i).getId());
        }
    }

    @Test
    public void test_getTop3CategoriesOrderedByDepthOfChildrenThenByName_v4() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");
        category_5.setName("5");
        Category category_6 = getRandomCategory();
        category_6.setId("6");
        category_6.setName("6");
        Category category_7 = getRandomCategory();
        category_7.setId("7");
        category_7.setName("7");


        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);
        this.categorizator.addCategory(category_6);
        this.categorizator.addCategory(category_7);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_4.getId());
        this.categorizator.assignParent(category_6.getId(), category_5.getId());
        this.categorizator.assignParent(category_7.getId(), category_6.getId());

        Iterable<Category> categoryIterable = this.categorizator.getTop3CategoriesOrderedByDepthOfChildrenThenByName();
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(3, categoryList.size());
        String[] expected = {category_1.getId(), category_2.getId(), category_4.getId()};
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expected[i], categoryList.get(i).getId());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeCategory_whenCategorizatorIsEmpty() {
        this.categorizator.removeCategory(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_removeCategory_whenIdDoesNotExists() {
        Category category_1 = getRandomCategory();
        Category category_2 = getRandomCategory();
        Category category_3 = getRandomCategory();
        Category category_4 = getRandomCategory();

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());

        this.categorizator.removeCategory(category_4.getId());
    }

    @Test
    public void test_removeCategory_shouldRemoveCorrectly_whenCategoryIsALeaf() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");
        category_5.setName("5");
        Category category_6 = getRandomCategory();
        category_6.setId("6");
        category_6.setName("6");
        Category category_7 = getRandomCategory();
        category_7.setId("7");
        category_7.setName("7");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);
        this.categorizator.addCategory(category_6);
        this.categorizator.addCategory(category_7);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_4.getId());
        this.categorizator.assignParent(category_6.getId(), category_5.getId());
        this.categorizator.assignParent(category_7.getId(), category_6.getId());

        this.categorizator.removeCategory(category_7.getId());

        Assert.assertEquals(6, this.categorizator.size());

        Iterable<Category> categoryIterable = this.categorizator.getChildren(category_6.getId());
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, categoryList.size());
    }

    @Test
    public void test_removeCategory_shouldRemoveCorrectly_whenCategoryIsRoot() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");
        Category category_5 = getRandomCategory();
        category_5.setId("5");
        category_5.setName("5");
        Category category_6 = getRandomCategory();
        category_6.setId("6");
        category_6.setName("6");
        Category category_7 = getRandomCategory();
        category_7.setId("7");
        category_7.setName("7");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);
        this.categorizator.addCategory(category_4);
        this.categorizator.addCategory(category_5);
        this.categorizator.addCategory(category_6);
        this.categorizator.addCategory(category_7);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_2.getId());
        this.categorizator.assignParent(category_5.getId(), category_4.getId());
        this.categorizator.assignParent(category_6.getId(), category_5.getId());
        this.categorizator.assignParent(category_7.getId(), category_6.getId());

        this.categorizator.removeCategory(category_1.getId());

        Assert.assertEquals(0, this.categorizator.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AssignParent_shouldThrowException_whenCategoryWithParentIdDoesNotExists() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_4.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AssignParent_shouldThrowException_whenCategoryWithCategoryIdDoesNotExists() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");
        Category category_4 = getRandomCategory();
        category_4.setId("4");
        category_4.setName("4");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);

        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_4.getId(), category_1.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AssignParent_shouldThrowException_whenCategoryIsAlreadyAChildOfParentCategory() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);

        this.categorizator.assignParent(category_3.getId(), category_1.getId());
        this.categorizator.assignParent(category_2.getId(), category_1.getId());
        this.categorizator.assignParent(category_3.getId(), category_1.getId());
    }

    @Test
    public void test_AssignParent_shouldSetParentCorrectly() {
        Category category_1 = getRandomCategory();
        category_1.setId("1");
        category_1.setName("1");
        Category category_2 = getRandomCategory();
        category_2.setId("2");
        category_2.setName("2");
        Category category_3 = getRandomCategory();
        category_3.setId("3");
        category_3.setName("3");

        this.categorizator.addCategory(category_1);
        this.categorizator.addCategory(category_2);
        this.categorizator.addCategory(category_3);

        Iterable<Category> categoryIterable = this.categorizator.getChildren(category_1.getId());
        List<Category> categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(0, categoryList.size());

        this.categorizator.assignParent(category_2.getId(), category_1.getId());

        categoryIterable = this.categorizator.getChildren(category_1.getId());
        categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(1, categoryList.size());

        this.categorizator.assignParent(category_3.getId(), category_1.getId());

        categoryIterable = this.categorizator.getChildren(category_1.getId());
        categoryList = StreamSupport.stream(categoryIterable.spliterator(), false).collect(Collectors.toList());

        Assert.assertEquals(2, categoryList.size());
    }
}
