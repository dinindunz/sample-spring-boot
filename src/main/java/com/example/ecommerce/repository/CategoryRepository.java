package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByActiveTrue();

    List<Category> findByParentIsNull();

    List<Category> findByParent(Category parent);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.sortOrder")
    List<Category> findRootCategoriesOrderBySortOrder();

    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.active = true ORDER BY c.sortOrder")
    List<Category> findByParentOrderBySortOrder(@Param("parent") Category parent);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Category> findByIdWithChildren(@Param("id") Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Category> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent = :parent")
    long countByParent(@Param("parent") Category parent);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();

    boolean existsByName(String name);
}