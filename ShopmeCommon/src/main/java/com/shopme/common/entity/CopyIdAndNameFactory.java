package com.shopme.common.entity;

public class CopyIdAndNameFactory implements CategoryAbstractFactory {
    private final Integer id;
    private final String name;

    public CopyIdAndNameFactory(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Category createCategory() {
        CategoryDirector director = new CategoryDirector();
        return director.constructCopyIdAndName(id, name);
    }
}
