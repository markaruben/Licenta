package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/favorites/{productId}")
    public ResponseEntity<?> addProductToFavorites(@PathVariable Integer userId, @PathVariable Integer productId) {
        try {
            userService.addProductToFavorites(userId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not add product to favorites: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/favorites/{productId}")
    public ResponseEntity<?> removeProductFromFavorites(@PathVariable Integer userId, @PathVariable Integer productId) {
        try {
            userService.removeProductFromFavorites(userId, productId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Could not remove product from favorites: " + e.getMessage());
        }
    }


    @GetMapping("/{userId}/favorites")
    public ResponseEntity<?> getFavoriteProducts(@PathVariable Integer userId) {
        try {
            Set<Product> favoriteProducts = userService.findFavoriteProductsByUserId(userId);
            return ResponseEntity.ok(favoriteProducts); // Return the set of favorite products
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // User not found or other error
        }
    }
}
