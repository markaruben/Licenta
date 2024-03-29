package com.fashionfinds.backendbun.repository;

import com.fashionfinds.backendbun.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
