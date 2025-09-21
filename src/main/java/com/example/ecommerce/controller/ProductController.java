package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<Page<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            Pageable pageable) {
        Page<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<Page<Product>> getInStockProducts(Pageable pageable) {
        Page<Product> products = productService.getInStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Product>> getOutOfStockProducts() {
        List<Product> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Product>> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getMostPopularProducts(Pageable pageable) {
        List<Product> products = productService.getMostPopularProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}/latest")
    public ResponseEntity<List<Product>> getLatestProductsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        List<Product> products = productService.getLatestProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> updateProductStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer newStock = request.get("stock");
        Product updatedProduct = productService.updateProductStock(id, newStock);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/stock/increase")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> increaseStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        Product updatedProduct = productService.increaseStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/stock/reduce")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> reduceStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        Product updatedProduct = productService.reduceStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> activateProduct(@PathVariable Long id) {
        Product product = productService.activateProduct(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> deactivateProduct(@PathVariable Long id) {
        Product product = productService.deactivateProduct(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}/featured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> setFeatured(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean featured = request.get("featured");
        Product product = productService.setFeatured(id, featured);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Product> getProductWithReviews(@PathVariable Long id) {
        Product product = productService.getProductWithReviews(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/category")
    public ResponseEntity<Product> getProductWithCategory(@PathVariable Long id) {
        Product product = productService.getProductWithCategory(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/categories/multiple")
    public ResponseEntity<Page<Product>> getProductsByCategoryIds(
            @RequestParam List<Long> categoryIds,
            Pageable pageable) {
        Page<Product> products = productService.getProductsByCategoryIds(categoryIds, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<Map<String, Long>> getProductCountByCategory(@PathVariable Long categoryId) {
        long count = productService.getProductCountByCategory(categoryId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/category/{categoryId}/average-price")
    public ResponseEntity<Map<String, BigDecimal>> getAveragePriceByCategory(@PathVariable Long categoryId) {
        BigDecimal averagePrice = productService.getAveragePriceByCategory(categoryId);
        return ResponseEntity.ok(Map.of("averagePrice", averagePrice));
    }

    @GetMapping("/{id}/stock-check")
    public ResponseEntity<Map<String, Boolean>> checkProductStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        boolean inStock = productService.isProductInStock(id, quantity);
        return ResponseEntity.ok(Map.of("inStock", inStock));
    }
}