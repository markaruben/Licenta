package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.ProductDTO;
import com.fashionfinds.backendbun.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

//    @PostMapping("/create")
//    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
//        Product createdProduct = productService.createProduct(
//                productDTO.getName(),
//                productDTO.getDescription(),
//                productDTO.getPrice(),
//                productDTO.getImageUrl()
//        );
//        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
//    }

    @GetMapping("/{productId}") // Endpoint to get product by ID
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/fetchAll")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

//    @PutMapping("/update/{productId}")
//    public ResponseEntity<Product> updateProduct(
//            @PathVariable Integer productId,
//            @RequestBody ProductDTO productDTO) {
//        Product updatedProduct = productService.updateProduct(
//                productId,
//                productDTO.getName(),
//                productDTO.getDescription(),
//                productDTO.getPrice(),
//                productDTO.getImageUrl()
//        );
//        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
//    }

    // Method to delete a product
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

}
