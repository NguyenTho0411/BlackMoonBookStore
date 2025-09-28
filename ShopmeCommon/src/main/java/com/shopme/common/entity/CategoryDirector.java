package com.shopme.common.entity;

//com.shopme.common.entity.CategoryDirector.java

public class CategoryDirector {

 public Category constructDefault(String name) {
     return new Category.Builder()
             .name(name)
             .alias(name.toLowerCase().replace(" ", "-"))
             .image("default.png")
             .enabled(true)
             .build();
 }

 public Category constructWithParent(String name, Category parent) {
     return new Category.Builder()
             .name(name)
             .alias(name.toLowerCase().replace(" ", "-"))
             .image("default.png")
             .enabled(true)
             .parent(parent)
             .build();
 }

 public Category constructCopy(Category source) {
     return new Category.Builder()
             .id(source.getId())
             .name(source.getName())
             .alias(source.getAlias())
             .image(source.getImage())
             .enabled(source.isEnabled())
             .parent(source.getParent())
             .allParentIDs(source.getAllParentIDs())
             .build();
 }
 public Category constructCopyIdAndName(Integer id, String name) {
     return new Category.Builder()
             .id(id)
             .name(name)
             .build();
 }
}
