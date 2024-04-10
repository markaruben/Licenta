package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.ProductDTO;
import com.fashionfinds.backendbun.repository.UserProductRepository;
import com.fashionfinds.backendbun.services.ProductService;
import com.fashionfinds.backendbun.utils.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserProductRepository userProductRepository;

//    @PostMapping("/create")
//    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
//        Product createdProduct = productService.createProduct(
//                productDTO.getName(),
//                productDTO.getDescription(),
//                productDTO.getPrice(),
//                productDTO.getImageUrl()
//        );
//        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
//    }

    @PostMapping("/callPythonScript")
    public ResponseEntity<String> callPythonScript(@RequestParam String url) {
        try {
            String pythonScriptPath = "C:\\Users\\marka\\OneDrive\\Desktop\\Facultate\\Licenta\\testscraping\\addProd.py";

            String[] command = {"python", pythonScriptPath, url};

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok(output.toString());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Python script");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Python script: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/favorite-products")
    public ResponseEntity<List<ProductDTO>> getFavoriteProductsByUserId(@PathVariable Integer userId) {
        try {
            Set<Product> favoriteProducts = productService.getFavoriteProductsByUserId(userId);
            ProductMapper productMapper = new ProductMapper();
            List<ProductDTO> productDTOs = productMapper.convertToDTO(favoriteProducts);
            return ResponseEntity.ok(productDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/removeFavorite/{userId}/{productId}")
    public ResponseEntity<String> removeFavoriteProduct(@PathVariable Integer userId, @PathVariable Integer productId) {
        try {
            Integer userProductId = userProductRepository.findUserProductIdByUserAndProduct(userId, productId);

            if (userProductId != null) {
                productService.removeProductFromFavorites(userProductId);
                return ResponseEntity.ok("Favorite product removed successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user ID or product ID.");
        }
    }


    @PostMapping("/addProduct/{userId}")
    public ResponseEntity<String> addProduct(@PathVariable Integer userId, @RequestBody Map<String, String> requestParams) {
        try {

            String productUrl = requestParams.get("productUrl");
            String thresholdPrice = requestParams.get("thresholdPrice");
            boolean productExists = productService.checkProductExists(productUrl);
            if (!productExists) {
                String pythonScriptPath = "C:\\Users\\\\marka\\OneDrive\\Desktop\\Facultate\\Licenta\\testscraping\\addProd.py";
                String[] command = {"python", pythonScriptPath, productUrl};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    Integer productId = productService.getProductIdByProductUrl(productUrl);
                    if (userProductRepository.existsByUser_userIdAndProduct_id(userId, productId)) {
                        return ResponseEntity.ok("User has already favorited this product!");
                    }
                    productService.addProductToFavorites(userId, productId, thresholdPrice);
                    return ResponseEntity.ok(output.toString());
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Python script");
                }
            } else {
                Integer productId = productService.getProductIdByProductUrl(productUrl);
                if (userProductRepository.existsByUser_userIdAndProduct_id(userId, productId)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already favorited this product!");
                }
                productService.addProductToFavorites(userId, productId, thresholdPrice);
                return ResponseEntity.ok("Product added to favorites!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Python script: " + e.getMessage());
        }
    }


    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/fetchAll")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

}
