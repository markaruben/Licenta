package com.fashionfinds.backendbun.utils;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.ProductDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductMapper {

    public List<ProductDTO> convertToDTO(Set<Product> products) {
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setTitle(product.getTitle());
            dto.setPrice(product.getPrice());
            dto.setProductUrl(product.getProductUrl());
            dto.setImageUrl(product.getImageUrl());
            productDTOs.add(dto);
        }
        return productDTOs;
    }
}
