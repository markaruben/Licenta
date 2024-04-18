package com.fashionfinds.backendbun.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", productUrl='" + productUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userProducts=" + userProducts +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private String price;
    private String productUrl;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @OneToMany(mappedBy = "product")
    private Set<UserProduct> userProducts = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public Set<UserProduct> getUserProducts() {
        return userProducts;
    }

    public void setUserProducts(Set<UserProduct> userProducts) {
        this.userProducts = userProducts;
    }
}
