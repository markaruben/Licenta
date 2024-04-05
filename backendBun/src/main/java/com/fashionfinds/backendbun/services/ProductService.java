package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.repository.ProductRepository;
import com.fashionfinds.backendbun.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.List;
import java.util.Optional;

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

    public Product updateProduct(Integer productId, String name, String description, double price, String imageUrl) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getPrice() > price) {
                String subject = "Product on Sale: " + product.getName();
                String body = "The price of the product \"" + product.getName() + "\" has been reduced to $" + price + "!\n\n"
                        + "Don't miss out on this amazing offer.\n"
                        + "Click the link below to view the product:\n"
                        + "https://fashionfinds.com/products/" + product.getId();

                List<ApplicationUser> users = userRepository.findByFavoriteProductsContains(product);
                for (ApplicationUser user : users) {

                    emailService.sendMail( user.getEmail(), new String[]{}, subject, body);
                }
            }
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setImageUrl(imageUrl);

            return productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }


    public Product getProductById(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public void deleteProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        List<ApplicationUser> users = userRepository.findByFavoriteProductsContains(product);

        // Remove the product from favorites for each user
        for (ApplicationUser user : users) {
            userService.removeProductFromFavorites(user.getUserId(), productId);
        }
        productRepository.delete(product);
    }
}
