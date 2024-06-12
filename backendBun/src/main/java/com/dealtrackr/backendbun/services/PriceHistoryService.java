package com.dealtrackr.backendbun.services;

import com.dealtrackr.backendbun.models.PriceHistoryDTO;
import com.dealtrackr.backendbun.repository.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceHistoryService {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    public List<PriceHistoryDTO> getPriceHistoryByProductId(Integer productId) {
        return priceHistoryRepository.findByProductIdOrderByDateAsc(productId)
                .stream()
                .map(priceHistory -> new PriceHistoryDTO(priceHistory.getDate(), priceHistory.getPrice()))
                .collect(Collectors.toList());
    }
}
