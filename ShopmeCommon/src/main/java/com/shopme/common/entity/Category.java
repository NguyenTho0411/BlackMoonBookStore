package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name="categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 64, nullable = false)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    @Column(name = "all_parent_ids", length = 256)
    private String allParentIDs;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    @Transient
    private boolean hasChildren;

    public Category() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getAllParentIDs() { return allParentIDs; }
    public void setAllParentIDs(String allParentIDs) { this.allParentIDs = allParentIDs; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public Set<Category> getChildren() { return children; }
    public void setChildren(Set<Category> children) { this.children = children; }

    public boolean isHasChildren() { return hasChildren; }
    public void setHasChildren(boolean hasChildren) { this.hasChildren = hasChildren; }
    public Category(Integer id) {
        this.id = id;
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }
    @Transient
    public String getImagePath() {
        if (this.id == null) return "/images/image-thumbnail.png";
        return "/category-images/" + this.id + "/" + this.image;
    }

    // ================= Builder =================
    public static class Builder {
        private final Category category;

        public Builder() {
            category = new Category();
        }

        public Builder id(Integer id) {
            category.setId(id);
            return this;
        }

        public Builder name(String name) {
            category.setName(name);
            return this;
        }

        public Builder alias(String alias) {
            category.setAlias(alias);
            return this;
        }

        public Builder image(String image) {
            category.setImage(image);
            return this;
        }

        public Builder enabled(boolean enabled) {
            category.setEnabled(enabled);
            return this;
        }

        public Builder parent(Category parent) {
            category.setParent(parent);
            return this;
        }

        public Builder allParentIDs(String allParentIDs) {
            category.setAllParentIDs(allParentIDs);
            return this;
        }

        public Category build() {
            return category;
        }
    }
}