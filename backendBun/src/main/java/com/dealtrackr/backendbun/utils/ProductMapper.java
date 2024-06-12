package com.dealtrackr.backendbun.utils;

import com.dealtrackr.backendbun.models.Product;
import com.dealtrackr.backendbun.models.ProductDTO;

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

    public ProductDTO singleConvertToDTO(Product product) {
        ProductDTO productDTOs = new ProductDTO();

        productDTOs.setId(product.getId());
        productDTOs.setTitle(product.getTitle());
        productDTOs.setPrice(product.getPrice());
        productDTOs.setProductUrl(product.getProductUrl());
        productDTOs.setImageUrl(product.getImageUrl());
        return productDTOs;
    }
}
