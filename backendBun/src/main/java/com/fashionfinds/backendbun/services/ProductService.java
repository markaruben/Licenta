package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.UserProduct;
import com.fashionfinds.backendbun.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProductService userProductService;

    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        productRepository.delete(product);
    }

    @Transactional
    public void removeProductFromFavorites(Integer userProductId) {
        UserProduct userProduct = userProductService.findUserProductById(userProductId);

        userProductService.deleteUserProduct(userProductId);
        Set<UserProduct> userProductsByProdId = userProductService.findUserProductsByProductId(userProduct.getProduct().getId());
        if (userProductsByProdId.isEmpty()) {
            productRepository.delete(userProduct.getProduct());
        }
    }

    @Transactional
    public void addProductToFavorites(Integer userId, Integer productId, String thresholdPrice) {
        ApplicationUser user = userService.findUserById(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        UserProduct favorite = new UserProduct();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setThresholdPrice(thresholdPrice);
        userProductService.createUserProduct(favorite);
    }

    public Set<Product> getFavoriteProductsByUserId(Integer userId) {
        Set<UserProduct> userProducts = userProductService.findUserProductsByUserId(userId);
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
        System.out.println();
        return product != null ? product.getId() : null;
    }

    private String truncateUrl(String url) {
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
