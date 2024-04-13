package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.UserProduct;
import com.fashionfinds.backendbun.repository.ProductRepository;
import com.fashionfinds.backendbun.repository.UserProductRepository;
import com.fashionfinds.backendbun.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Array;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserProductRepository userProductRepository;


    public Product createProduct(String title, String price, String productUrl) {
        Product newProduct = new Product();
        newProduct.setTitle(title);
        newProduct.setPrice(price);
        newProduct.setProductUrl(productUrl);
        return productRepository.save(newProduct);
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        productRepository.delete(product);
    }

    @Transactional
    public void removeProductFromFavorites(Integer userProductId) {
        UserProduct userProduct = userProductRepository.findById(userProductId)
                .orElseThrow(() -> new IllegalArgumentException("UserProduct not found with ID: " + userProductId));

        userProductRepository.delete(userProduct);

        Set<UserProduct> userProductsByProdId= userProductRepository.findUserProductsByProduct_Id(userProduct.getProduct().getId());
        if (userProductsByProdId.isEmpty()) {
            productRepository.delete(userProduct.getProduct());
        }
    }

    @Transactional
    public void addProductToFavorites(Integer userId, Integer productId, String thresholdPrice) {
        System.out.println(userId);
        System.out.println(productId);
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));


        UserProduct favorite = new UserProduct();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setThresholdPrice(thresholdPrice);
        userProductRepository.save(favorite);
    }

    public Set<Product> getFavoriteProductsByUserId(Integer userId) {
        Set<UserProduct> userProducts = userProductRepository.findUserProductsByUser_userId(userId);
        Set<Product> favoriteProducts = new HashSet<>();
        for (UserProduct userProduct : userProducts) {
            favoriteProducts.add(userProduct.getProduct());
        }
        return favoriteProducts;
    }

    public boolean checkProductExists(String productUrl) {

        return productRepository.existsByProductUrl(truncateUrl(productUrl));
    }

    public Integer getProductIdByProductUrl(String productUrl) {
        String truncatedUrl = truncateUrl(productUrl);
        Product product = productRepository.findByProductUrl(truncatedUrl);
        return product != null ? product.getId() : null;
    }

    private String truncateUrl(String url) {
        // Truncate the URL to match the length stored in the database (e.g., 255 characters)
        int maxLength = 255;
        return url.length() > maxLength ? url.substring(0, maxLength) : url;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product getProductById(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }

}
