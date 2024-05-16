package com.fashionfinds.backendbun.models;

import java.time.LocalDateTime;

public class PriceHistoryDTO {
    private LocalDateTime date;
    private String price;

    // Constructori, getteri și setteri

    public PriceHistoryDTO(LocalDateTime date, String price) {
        this.date = date;
        this.price = price;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
