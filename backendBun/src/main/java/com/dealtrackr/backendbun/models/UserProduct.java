package com.dealtrackr.backendbun.models;

import jakarta.persistence.*;

@Entity
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String thresholdPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserProduct{" +
                "id=" + id +
                ", thresholdPrice='" + thresholdPrice + '\'' +
                ", product=" + product +
                ", user=" + user +
                '}';
    }

    public String getThresholdPrice() {
        return thresholdPrice;
    }

    public void setThresholdPrice(String thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }
}
