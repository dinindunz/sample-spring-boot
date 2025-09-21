package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByFeaturedTrue();

    List<Product> findByCategory(Category category);

    List<Product> findByCategoryAndActiveTrue(Category category);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.active = true AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceBetweenAndActive(@Param("minPrice") BigDecimal minPrice,
                                              @Param("maxPrice") BigDecimal maxPrice,
                                              Pageable pageable);

    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    List<Product> findByStockQuantityLessThan(Integer quantity);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.active = true")
    Page<Product> findInStockProducts(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.reviews WHERE p.id = :id")
    Optional<Product> findByIdWithReviews(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.category c WHERE c.id IN :categoryIds AND p.active = true")
    Page<Product> findByCategoryIdsAndActive(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY SIZE(p.orderItems) DESC")
    List<Product> findMostPopularProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category = :category ORDER BY p.createdAt DESC")
    List<Product> findLatestProductsByCategory(@Param("category") Category category, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND p.active = true")
    long countByCategoryAndActive(@Param("category") Category category);

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.category = :category AND p.active = true")
    BigDecimal findAveragePriceByCategory(@Param("category") Category category);
}