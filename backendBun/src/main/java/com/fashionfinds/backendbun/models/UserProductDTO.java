package com.fashionfinds.backendbun.models;

public class UserProductDTO {
    private Integer id;
    private Integer userId;
    private Integer productId;

    private String thresholdPrice;

    public Integer getUserId() {
        return userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getThresholdPrice() {
        return thresholdPrice;
    }

    public void setThresholdPrice(String thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
    }
}