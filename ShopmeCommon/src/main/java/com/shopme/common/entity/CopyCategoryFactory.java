package com.shopme.common.entity;

public class CopyCategoryFactory implements CategoryAbstractFactory {
    private final Category source;

    public CopyCategoryFactory(Category source) {
        this.source = source;
    }

    @Override
    public Category createCategory() {
        CategoryDirector director = new CategoryDirector();
        return director.constructCopy(source);
    }
}
