package core;

import models.Category;

import java.util.*;
import java.util.stream.Collectors;

public class CategorizatorImpl implements Categorizator {

    private final Map<String, Category> categories;
    private final Map<String, List<Category>> parentWithChildren;
    private final Map<Category, Category> childWithParent;

    public CategorizatorImpl() {
        this.categories = new HashMap<>();
        this.childWithParent = new LinkedHashMap<>();
        this.parentWithChildren = new HashMap<>();
    }

    // 12 tests exception
    @Override
    public void addCategory(Category category) {
        if (this.categories.containsKey(category.getId())) {
            throw new IllegalArgumentException();
        }
        this.categories.put(category.getId(), category);
        this.childWithParent.put(category, null);
    }

    //4, 16, 17 correctness
    @Override
    public void assignParent(String childCategoryId, String parentCategoryId) {
        if (!this.categories.containsKey(parentCategoryId)) {
            throw new IllegalArgumentException();
        }
        if (!this.categories.containsKey(childCategoryId)) {
            throw new IllegalArgumentException();
        }
        Category child = this.categories.get(childCategoryId);
        Category parent = this.categories.get(parentCategoryId);
        List<Category> children = this.parentWithChildren.get(parentCategoryId);
        if (children != null && children.contains(child)) {
            throw new IllegalArgumentException();
        }
        this.parentWithChildren.computeIfAbsent(parentCategoryId, cat -> new ArrayList<>()).add(child);
        this.childWithParent.putIfAbsent(child, parent);
    }

    //2,3,4,18 correctness
    @Override
    public void removeCategory(String categoryId) {
        if (!this.categories.containsKey(categoryId)) {
            throw new IllegalArgumentException();
        }
        Iterable<Category> categoryIterable = this.getChildren(categoryId);
        for (Category category : categoryIterable) {
            this.categories.remove(category.getId());
            this.childWithParent.remove(category);
            this.parentWithChildren.remove(category.getId());
        }
        Category category = this.categories.remove(categoryId);
        Category parent = this.childWithParent.remove(category);
        if (parent != null) {
            this.parentWithChildren.get(parent.getId()).remove(category);
        }
        this.parentWithChildren.remove(category.getId());
    }

    @Override
    public boolean contains(Category category) {
        return this.categories.containsKey(category.getId());
    }

    @Override
    public int size() {
        return this.categories.size();
    }

    //5 tests exception
    //5,6 correctness
    @Override
    public Iterable<Category> getChildren(String categoryId) {
        if (!this.categories.containsKey(categoryId)) {
            throw new IllegalArgumentException();
        }
        List<Category> categoryList = this.parentWithChildren.get(categoryId);
        if (categoryList == null) {
            return Collections.emptyList();
        }
        Deque<Category> deque = new ArrayDeque<>(categoryList);

        Set<Category> result = new LinkedHashSet<>(categoryList);
        while (!deque.isEmpty()) {
            Category category = deque.poll();
            result.add(category);
            List<Category> list = this.parentWithChildren.get(category.getId());
            if (list != null) {
                deque.addAll(list);
            }
        }
        return result;
    }

    //7 tests exception
    //7, 8 correctness
    @Override
    public Iterable<Category> getHierarchy(String categoryId) {
        if (!this.categories.containsKey(categoryId)) {
            throw new IllegalArgumentException();
        }

        List<Category> result = new ArrayList<>();
        Category category = this.categories.get(categoryId);
        result.add(category);
        Category parent = this.childWithParent.get(category);
        while (parent != null) {
            result.add(0, parent);
            parent = this.childWithParent.get(parent);
        }
        return result;
    }

    // empty collection fix test 9
    // 10 Correctness
    @Override
    public Iterable<Category> getTop3CategoriesOrderedByDepthOfChildrenThenByName() {
        Map<Category, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Category, Category> entry : this.childWithParent.entrySet()) {
            Category current = entry.getKey();
            Category parent = entry.getValue();
            result.put(current, 0);
            while (parent != null) {
                int value = Math.max(result.get(parent), result.get(current) + 1);
                result.put(parent, value);
                current = parent;
                parent = this.childWithParent.get(current);
            }
        }
        return result.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    if ((int) o2.getValue() == o1.getValue()) {
                        return o1.getKey().getName().compareTo(o2.getKey().getName());
                    }
                    return Integer.compare(o2.getValue(), o1.getValue());
                })
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());
    }
}
