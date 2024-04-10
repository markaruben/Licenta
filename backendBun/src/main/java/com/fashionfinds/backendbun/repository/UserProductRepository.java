package com.fashionfinds.backendbun.repository;

import com.fashionfinds.backendbun.models.ApplicationUser;
import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Integer> {
    Set<UserProduct> findUserProductsByUser_userId(Integer userId);

    boolean existsByUser_userIdAndProduct_id(Integer userId, Integer productId);
    @Query("SELECT up.id FROM UserProduct up WHERE up.user.id = :userId AND up.product.id = :productId")
    Integer findUserProductIdByUserAndProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
