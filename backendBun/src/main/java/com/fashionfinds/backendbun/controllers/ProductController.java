package com.fashionfinds.backendbun.controllers;

import com.fashionfinds.backendbun.models.Product;
import com.fashionfinds.backendbun.models.ProductDTO;
import com.fashionfinds.backendbun.models.UserProduct;
import com.fashionfinds.backendbun.models.UserProductDTO;
import com.fashionfinds.backendbun.repository.PriceHistoryRepository;
import com.fashionfinds.backendbun.repository.UserProductRepository;
import com.fashionfinds.backendbun.services.EmailService;
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

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    EmailService emailService;

    @DeleteMapping("/deleteProductAndNotifyUsers/{productId}")
    public ResponseEntity<String> deleteProductAndNotifyUsers(@PathVariable Integer productId) {
        try {
            Set<UserProduct> userProducts = userProductRepository.findUserProductsByProduct_Id(productId);

            // Notify users about product removal
            for (UserProduct userProduct : userProducts) {
                notifyUserProductRemoval(userProduct.getUser().getEmail(), userProduct.getProduct().getTitle(), userProduct.getProduct().getProductUrl());
            }

            userProductRepository.deleteByProductId(productId);

            priceHistoryRepository.deleteByProductId(productId);

            productService.deleteProduct(productId);

            return ResponseEntity.ok("Product and related data deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting product and related data: " + e.getMessage());
        }
    }

    private void notifyUserProductRemoval(String userEmail, String productTitle, String productUrl) {
        String subject = "Product Removal Notification";
        String body = "Hello!\n\nWe regret to inform you that the product '" + productTitle + "' is no longer available and has been removed from our service.\n\n"
                + "Product URL: " + productUrl + "\n\n"
                + "Thank you for using our service.";
        emailService.sendMail(userEmail, new String[0], subject, body);
    }

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

    @PostMapping("/notifyPriceChange")
    public ResponseEntity<String> notifyPriceChange(@RequestBody Map<String, String> requestParams) {
        Integer productId = Integer.parseInt(requestParams.get("productId"));
        double newPrice = Double.parseDouble(requestParams.get("price"));
        Set<UserProduct> userProducts = userProductRepository.findUserProductsByProduct_Id(productId);
        for (UserProduct userProduct : userProducts) {
            if (newPrice <= Double.parseDouble(userProduct.getThresholdPrice())) {
                notifyUser(userProduct.getUser().getEmail(), userProduct.getProduct().getTitle(), Double.parseDouble(userProduct.getThresholdPrice()), newPrice, userProduct.getProduct().getProductUrl());
            }
        }
        return ResponseEntity.ok("Price change notifications sent successfully");
    }

    private void notifyUser(String userEmail, String productTitle, double thresholdPrice, double newPrice, String productUrl) {
        String subject = "Price Drop Notification";
        String body = "Hello!\n\nThe price of the product '" + productTitle + "' has dropped below your threshold price.\n\n"
                + "Threshold Price: " + thresholdPrice + "Lei \n"
                + "New Price: " + newPrice + "Lei \n"
                + "Product URL: " + productUrl + "\n\n"
                + "Visit our website to update your preferences.\n\n"
                + "Thank you for using our service.";
        emailService.sendMail(userEmail, new String[0], subject, body);
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
                System.out.println(productId);
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

    @GetMapping("/getUserProduct/{userId}/{productId}")
    public ResponseEntity<UserProductDTO> getUserProduct(@PathVariable Integer userId, @PathVariable Integer productId) {
        UserProduct userProduct = userProductRepository.findUserProductByUser_userIdAndProduct_Id(userId, productId);
        if (userProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        UserProductDTO userProductDTO = new UserProductDTO();
        userProductDTO.setId(userProduct.getId());
        userProductDTO.setUserId(userProduct.getUser().getUserId());
        userProductDTO.setProductId(userProduct.getProduct().getId());
        userProductDTO.setThresholdPrice(userProduct.getThresholdPrice());

        return ResponseEntity.ok(userProductDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);
        ProductMapper productMapper = new ProductMapper();
        return ResponseEntity.ok(productMapper.singleConvertToDTO(product));
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

    @PutMapping("/{userProductId}/threshold-price")
    public ResponseEntity<String> updateUserProductThresholdPrice(@PathVariable Integer userProductId, @RequestBody String thresholdPrice) {
        Double numericThresholdPrice = Double.parseDouble(thresholdPrice.replaceAll("[^0-9.]", ""));

        userProductRepository.updateUserProductThresholdPrice(userProductId, numericThresholdPrice.toString());
        return ResponseEntity.status(HttpStatus.OK).body("Threshold price updated successfully");
    }


}
