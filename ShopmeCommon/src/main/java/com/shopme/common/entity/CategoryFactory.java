package com.shopme.common.entity;

public class CategoryFactory {

    public static Category createDefaultCategory(String name) {
        return new Category.Builder()
                .name(name)
                .alias(name.toLowerCase().replace(" ", "-"))
                .image("default.png")
                .enabled(true)
                .build();
    }

    public static Category createWithParent(String name, Category parent) {
        return new Category.Builder()
                .name(name)
                .alias(name.toLowerCase().replace(" ", "-"))
                .image("default.png")
                .enabled(true)
                .parent(parent)
                .build();
    }

    public static Category createFull(Integer id, String name, String alias, String image, boolean enabled, Category parent) {
        return new Category.Builder()
                .id(id)
                .name(name)
                .alias(alias)
                .image(image)
                .enabled(enabled)
                .parent(parent)
                .build();
    }

    public static Category copyIdAndName(Category category) {
        return new Category.Builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category copyIdAndName(Integer id, String name) {
        return new Category.Builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Category copyFull(Category category) {
        return new Category.Builder()
                .id(category.getId())
                .name(category.getName())
                .alias(category.getAlias())
                .image(category.getImage())
                .enabled(category.isEnabled())
                .parent(category.getParent())
                .build();
    }

    public static Category copyFull(Category category, String nameOverride) {
        return new Category.Builder()
                .id(category.getId())
                .name(nameOverride)
                .alias(category.getAlias())
                .image(category.getImage())
                .enabled(category.isEnabled())
                .parent(category.getParent())
                .build();
    }
}