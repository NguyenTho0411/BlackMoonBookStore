package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;



import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository repo;
	
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repo.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(5);
		
		Category subCategory = new Category("Memory",parent);
		Category savedCategory = repo.save(subCategory);
		assertThat(savedCategory.getId()).isGreaterThan(0);
//		Category camera = new Category("Cameras",parent);
//		Category smartphones = new Category("Smart Phones",parent);
//		repo.saveAll(List.of(camera,smartphones));

	}
	@Test
	public void testGetCategory() {
		Category category = repo.findById(2).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		for(Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierachicalCategories() {
		Iterable<Category> categories = repo.findAll();
		for(Category category : categories) {
			if(category.getParent() ==null) {
				System.out.println(category.getName());
				
				Set<Category> children = category.getChildren();
				for(Category subCategory : children) {
					System.out.println("--"+subCategory.getName());
					printChildren(subCategory,1);
				}
			}
		}
	}
	private  void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel +1;
		Set<Category> children = parent.getChildren();
		for(Category subCategory :children) {
			for(int i =0 ; i< newSubLevel;i++) {
				System.out.println("--");
			}
			System.out.println("--"+subCategory.getName());
		}
	}
	
	
	@Test
	public void testListRootCategories() {
		Sort sort = null;
		List<Category> rootCategories = repo.findRootCategories(sort);
		rootCategories.forEach(cat -> System.out.println(cat.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = repo.findByName(name);
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	@Test
	public void testFindByAlias() {
		String alias = "Computers";
		Category category = repo.findByAlias(alias);
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(alias);
	}
}
