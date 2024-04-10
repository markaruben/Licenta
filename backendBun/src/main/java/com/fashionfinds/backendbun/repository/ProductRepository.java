package com.fashionfinds.backendbun.repository;

import com.fashionfinds.backendbun.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByProductUrl(String productUrl);
    Product findByProductUrl(String productUrl);
}
