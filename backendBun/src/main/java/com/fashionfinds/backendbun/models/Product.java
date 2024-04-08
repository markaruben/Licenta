package com.fashionfinds.backendbun.models;

import jakarta.persistence.*;
import org.apache.catalina.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private String  price;
    private String productUrl;

    @ManyToMany(mappedBy = "favoriteProducts")
    private Set<ApplicationUser> usersWhoFavorited = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String  price) {
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

    public Set<ApplicationUser> getUsersWhoFavorited() {
        return usersWhoFavorited;
    }

    public void setUsersWhoFavorited(Set<ApplicationUser> usersWhoFavorited) {
        this.usersWhoFavorited = usersWhoFavorited;
    }
}
