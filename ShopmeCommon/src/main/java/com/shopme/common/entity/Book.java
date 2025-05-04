package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.shopme.common.Constants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "books")
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique=true, length=256, nullable=false)
	private String name;
	
	@Column(unique=true, length=256, nullable=false)
	private String alias;
	
	@Column(length=400000, nullable=false, name="short_description")
	private String shortDescription;
	@Column(length=400000, nullable=false, name="full_description")
	private String fullDescription;
	
	@Column(name="created_time")
	private Date createdTime;
	@Column(name="updated_time")
	private Date updateTime;
	
	
	private boolean enabled;
	
	@Column(name="in_stock")
	private boolean inStock;
	private float cost;
	
	private float price;
	@Column(name="discount_percent")
	private float discountPercent;
	
	@Column(name ="main_image", nullable= false)
	private String mainImage;
	
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BookImage> images = new HashSet<>();
	private float length;
	private float width;
	private float height;
	private float weight;
	
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookDetail> details = new ArrayList<>();
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name="publisher_id")
	private Publisher publisher;
	
	private int reviewCount;
	private float averageRating;
	
	@Transient private boolean customerCanReview;
	@Transient private boolean reviewedByCustomer;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public Book(String name) {
		this.name = name;
	}
	public Set<BookImage> getImages() {
		return images;
	}
	public void setImages(Set<BookImage> images) {
		this.images = images;
	}
	public String getName() {
		return name;
	}
	
	
	public String getMainImage() {
		return mainImage;
	}
	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getFullDescription() {
		return fullDescription;
	}
	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isInStock() {
		return inStock;
	}
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getDiscountPercent() {
		return discountPercent;
	}
	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}
	public float getLength() {
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Publisher getPublisher() {
		return publisher;
	}
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	
	public void addExtraImage(String imageName) {
		this.images.add(new BookImage(imageName, this));
	}
	
	
	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null) return "/images/image-thumbnail.png";
		
		return Constants.S3_BASE_URI +"/book-images/" + this.id + "/" + this.mainImage;
	}
	public List<BookDetail> getDetails() {
		return details;
	}
	public void setDetails(List<BookDetail> details) {
		this.details = details;
	}
	public void addDetail(String name, String value) {
		this.details.add(new BookDetail(name, value, this));
	}

	public void addDetail(Integer id, String name, String value) {
		this.details.add(new BookDetail(id, name, value, this));
	}
	public boolean containsImageName(String imageName) {
		// TODO Auto-generated method stub
		Iterator<BookImage> iterator = images.iterator();
		while(iterator.hasNext()) {
			BookImage image = iterator.next();
			if(image.getName().equals(imageName)) {
				return true;
			}
		}
		return false;
	}
	@Transient
	public float getDiscountPrice() {
		if(discountPercent >0) {
			return price * ((100 - discountPercent)/100);
		}
		return this.price;
	}
	
	
	@Transient
	public String getShortName() {
		if (name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		return name;
	}
	public Book(Integer id) {
		this.id = id;
	}
	public Book() {
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	
	@Transient
	public String getURI() {
		return "/b/" + this.alias + "/";
	}

	public boolean isCustomerCanReview() {
		return customerCanReview;
	}

	public void setCustomerCanReview(boolean customerCanReview) {
		this.customerCanReview = customerCanReview;
	}

	public boolean isReviewedByCustomer() {
		return reviewedByCustomer;
	}

	public void setReviewedByCustomer(boolean reviewedByCustomer) {
		this.reviewedByCustomer = reviewedByCustomer;
	}
	
}
