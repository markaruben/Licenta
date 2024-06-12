package com.dealtrackr.backendbun.services;

import com.dealtrackr.backendbun.models.UserProduct;
import com.dealtrackr.backendbun.repository.UserProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserProductService {
    @Autowired
    private UserProductRepository userProductRepository;

    public UserProduct createUserProduct(UserProduct userProduct) {
        return userProductRepository.save(userProduct);
    }

    @Transactional
    public void deleteUserProduct(Integer userProductId) {
        UserProduct userProduct = userProductRepository.findById(userProductId)
                .orElseThrow(() -> new IllegalArgumentException("UserProduct not found with ID: " + userProductId));

        userProductRepository.delete(userProduct);
    }

    public Set<UserProduct> findUserProductsByProductId(Integer productId) {
        return userProductRepository.findUserProductsByProduct_Id(productId);
    }

    public UserProduct findUserProductById(Integer userProductId) {
        return userProductRepository.findById(userProductId).orElseThrow(() -> new IllegalArgumentException("UserProduct not found with ID: " + userProductId));
    }

    public Set<UserProduct> findUserProductsByUserId(Integer userId) {
        return userProductRepository.findUserProductsByUser_userId(userId);
    }

}
