package com.dealtrackr.backendbun.repository;

import com.dealtrackr.backendbun.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByProductUrl(String productUrl);
    Product findByProductUrl(String productUrl);
}
