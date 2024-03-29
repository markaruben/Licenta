package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(String name, String description, double price, String imageUrl) {
        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setDescription(description);
        newProduct.setPrice(price);
        newProduct.setImageUrl(imageUrl);
        return productRepository.save(newProduct);
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
