package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userTho = new User("nguyenhuuductho0411@gmail.com","Tho0411@","Tho","Nguyen");
		userTho.addRole(roleAdmin);
		
		
		User savedUser = repo.save(userTho);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRoles() {
		User userHung = new User("lethaihung@gmail.com","lehung","Hung","Le");
		Role roleEditor = new Role(3);
		Role roleAssisstant = new Role(5);
		
		userHung.addRole(roleAssisstant);
		userHung.addRole(roleEditor);
		
		
		User savedUser = repo.save(userHung);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void listAllUser() {
		Iterable<User> listUser = repo.findAll();
		listUser.forEach(user -> System.out.println(user));
	}
	
	
	@Test
	public void testGetUserById() {
		User userTho = repo.findById(1).get();
		System.out.println(userTho);
		assertThat(userTho).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetail() {
		User userTho = repo.findById(1).get();
		userTho.setEnabled(true);
		repo.save(userTho);
	}
	
	@Test
	public void testUpdateUserRole() {
		User userHung = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		userHung.getRoles().remove(roleEditor);
		userHung.addRole(roleSalesperson);
		repo.save(userHung);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "nguyenhuuductho0411@gmail.com";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber =1;
		int pageSize =4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(user -> System.out.println(user));
		
		assertThat(listUser.size()).isEqualTo(pageSize);
		
	}
	
	@Test
	public void testFindUser() {
		int pageNumber =0;
		String keyword= "bruce";
		int pageSize =4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword,pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(user -> System.out.println(user));
		
		assertThat(listUser.size()).isGreaterThan(0);
		
	}
}
