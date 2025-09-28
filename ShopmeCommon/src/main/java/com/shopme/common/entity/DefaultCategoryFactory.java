package com.shopme.common.entity;

public class DefaultCategoryFactory implements CategoryAbstractFactory {
    private final String name;

    public DefaultCategoryFactory(String name) {
        this.name = name;
    }

    @Override
    public Category createCategory() {
        CategoryDirector director = new CategoryDirector();
        return director.constructDefault(name);
    }
}
