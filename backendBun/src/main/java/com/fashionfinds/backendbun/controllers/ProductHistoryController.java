package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.PriceHistoryDTO;
import com.fashionfinds.backendbun.services.PriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productHistory")
@CrossOrigin("*")
public class ProductHistoryController {

    @Autowired
    private PriceHistoryService priceHistoryService;

    @GetMapping("/{productId}/price-history")
    public List<PriceHistoryDTO> getPriceHistory(@PathVariable Integer productId) {
        return priceHistoryService.getPriceHistoryByProductId(productId);
    }
}
