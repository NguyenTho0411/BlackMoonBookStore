package com.shopme.common.entity;

public class WithParentCategoryFactory implements CategoryAbstractFactory {
    private final String name;
    private final Category parent;

    public WithParentCategoryFactory(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public Category createCategory() {
        CategoryDirector director = new CategoryDirector();
        return director.constructWithParent(name, parent);
    }
}
