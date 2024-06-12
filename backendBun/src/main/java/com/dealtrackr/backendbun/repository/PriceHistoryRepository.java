package com.dealtrackr.backendbun.repository;

import com.dealtrackr.backendbun.models.PriceHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {
    List<PriceHistory> findByProductIdOrderByDateAsc(Integer productId);
    @Transactional
    void deleteByProductId(Integer productId);
}