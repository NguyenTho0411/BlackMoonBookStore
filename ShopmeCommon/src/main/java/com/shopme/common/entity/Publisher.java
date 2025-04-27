package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name="publisher")
public class Publisher {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length=45, unique = true)
	private String name;
	
	
	@Column(nullable = false, length=128)
	private String logo;
	
	public Publisher(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	

	public Publisher(String name) {
		this.name = name;
		this.logo = "brand-logo.png";
	}


	public Publisher() {
	}


	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name="publisher_categories",
			joinColumns = @JoinColumn(name="publisher_id"),
			inverseJoinColumns =  @JoinColumn(name = "category_id")
			)
	private Set<Category> categories = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	
	@Override
	public String toString() {
		return "Publisher [id=" + id + ", name=" + name + ", categories=" + categories + "]";
	}

	@Transient
	public String getLogoPath() {
		if(this.id == null) {
			return "/images/image-thumbnail.png";
		}
		return "/publisher-logos/" + this.id +"/"+this.logo;
	}
}
