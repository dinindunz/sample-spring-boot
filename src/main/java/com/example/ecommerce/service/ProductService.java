package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(product.getCategory().getId());
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return productRepository.findByCategoryAndActiveTrue(category);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchActiveProducts(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetweenAndActive(minPrice, maxPrice, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getInStockProducts(Pageable pageable) {
        return productRepository.findInStockProducts(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());
        product.setFeatured(productDetails.getFeatured());

        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(productDetails.getCategory().getId());
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    public Product updateProductStock(Long id, Integer newStock) {
        Product product = getProductById(id);
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }

    public Product increaseStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        product.increaseStock(quantity);
        return productRepository.save(product);
    }

    public Product reduceStock(Long id, Integer quantity) {
        Product product = getProductById(id);

        if (!product.isInStock(quantity)) {
            throw new InsufficientStockException(
                "Insufficient stock for product: " + product.getName() +
                ". Available: " + product.getStockQuantity() + ", Requested: " + quantity
            );
        }

        product.reduceStock(quantity);
        return productRepository.save(product);
    }

    public Product activateProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(true);
        return productRepository.save(product);
    }

    public Product deactivateProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        return productRepository.save(product);
    }

    public Product setFeatured(Long id, boolean featured) {
        Product product = getProductById(id);
        product.setFeatured(featured);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Product getProductWithReviews(Long id) {
        return productRepository.findByIdWithReviews(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Product getProductWithCategory(Long id) {
        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategoryIds(List<Long> categoryIds, Pageable pageable) {
        return productRepository.findByCategoryIdsAndActive(categoryIds, pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> getMostPopularProducts(Pageable pageable) {
        return productRepository.findMostPopularProducts(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> getLatestProductsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryService.getCategoryById(categoryId);
        return productRepository.findLatestProductsByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public long getProductCountByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return productRepository.countByCategoryAndActive(category);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAveragePriceByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return productRepository.findAveragePriceByCategory(category);
    }

    @Transactional(readOnly = true)
    public boolean isProductInStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        return product.isInStock(quantity);
    }
}