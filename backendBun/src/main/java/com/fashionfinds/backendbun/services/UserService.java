package com.fashionfinds.backendbun.services;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.Role;
import com.fashionfinds.backendbun.repository.ProductRepository;
import com.fashionfinds.backendbun.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user is not valid"));
    }

    @Transactional
    public void addProductToFavorites(Integer userId, Integer productId) {
        ApplicationUser user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        user.getFavoriteProducts().add(product);
        userRepository.save(user);
    }

    @Transactional
    public void removeProductFromFavorites(Integer userId, Integer productId) {
        ApplicationUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if the user's favorites contain the product before attempting to remove it
        if (user.getFavoriteProducts().contains(product)) {
            user.getFavoriteProducts().remove(product);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Product not found in user's favorites");
        }
    }

    public Set<Product> findFavoriteProductsByUserId(Integer userId) {
        Optional<ApplicationUser> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            ApplicationUser user = userOptional.get();
            return user.getFavoriteProducts(); // Assuming getFavoriteProducts() returns a Set<Product>
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
