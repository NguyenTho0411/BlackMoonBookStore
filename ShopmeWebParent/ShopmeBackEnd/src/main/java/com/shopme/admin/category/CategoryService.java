package com.shopme.admin.category;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.*;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryRepository repo;

    public List<Category> listAll() {
        return repo.findAll();
    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");
        sort = "desc".equals(sortDir) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
        Page<Category> pageCategories = (keyword != null && !keyword.isEmpty()) ?
                repo.search(keyword, pageable) : repo.findRootCategories(pageable);

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        List<Category> content = pageCategories.getContent();
        if (keyword != null && !keyword.isEmpty()) {
            content.forEach(c -> c.setHasChildren(c.getChildren().size() > 0));
            return content;
        }
        return listHierachicalCategories(content, sortDir);
    }

    private List<Category> listHierachicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchical = new ArrayList<>();
        for (Category category : rootCategories) {
            hierarchical.add(new CopyCategoryFactory(category).createCategory());
            Set<Category> children = sortSubCategories(category.getChildren(), sortDir);
            for (Category subCategory : children) {
                hierarchical.add(new CopyCategoryFactory(subCategory).createCategory());
                listSubHierachicalCategories(hierarchical, subCategory, 1, sortDir);
            }
        }
        return hierarchical;
    }

    private void listSubHierachicalCategories(List<Category> hierarchical, Category parent, int subLevel, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newLevel = subLevel + 1;
        for (Category sub : children) {
            hierarchical.add(new CopyCategoryFactory(sub).createCategory());
            listSubHierachicalCategories(hierarchical, sub, newLevel, sortDir);
        }
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = repo.findRootCategories(Sort.by("name"));
        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(new CopyIdAndNameFactory(category.getId(), category.getName()).createCategory());
                Set<Category> children = sortSubCategories(category.getChildren());
                for (Category sub : children) {
                    categoriesUsedInForm.add(new CopyIdAndNameFactory(sub.getId(), "--" + sub.getName()).createCategory());
                    listSubCategoriesUsedInForm(categoriesUsedInForm, sub, 1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> list, Category parent, int level) {
        int newLevel = level + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());
        for (Category sub : children) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < newLevel; i++) name.append("--");
            name.append(sub.getName());
            list.add(new CopyIdAndNameFactory(sub.getId(), name.toString()).createCategory());
            listSubCategoriesUsedInForm(list, sub, newLevel);
        }
    }

    public Category save(Category category) {
        return repo.save(category);
    }

    public Category getCategory(Integer id) throws CategoryNotFoundException {
        return repo.findById(id).orElseThrow(() -> new CategoryNotFoundException("Could not find any category with ID " + id));
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isNew = (id == null || id == 0);
        Category byName = repo.findByName(name);
        if (isNew && byName != null) return "DuplicateName";
        if (!isNew && byName != null && !byName.getId().equals(id)) return "DuplicateName";

        Category byAlias = repo.findByAlias(alias);
        if (isNew && byAlias != null) return "DuplicateAlias";
        if (!isNew && byAlias != null && !byAlias.getId().equals(id)) return "DuplicateAlias";

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sorted = new TreeSet<>((c1, c2) ->
                "asc".equals(sortDir) ? c1.getName().compareTo(c2.getName()) : c2.getName().compareTo(c1.getName()));
        sorted.addAll(children);
        return sorted;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        if (repo.countById(id) == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
        repo.deleteById(id);
    }
}
