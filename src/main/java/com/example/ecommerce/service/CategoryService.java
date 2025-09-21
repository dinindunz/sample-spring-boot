package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId() != null) {
            Category parent = getCategoryById(category.getParent().getId());
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategoriesOrderBySortOrder();
    }

    @Transactional(readOnly = true)
    public List<Category> getChildCategories(Long parentId) {
        Category parent = getCategoryById(parentId);
        return categoryRepository.findByParentOrderBySortOrder(parent);
    }

    @Transactional(readOnly = true)
    public Category getCategoryWithChildren(Long id) {
        return categoryRepository.findByIdWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Category getCategoryWithProducts(Long id) {
        return categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setImageUrl(categoryDetails.getImageUrl());
        category.setSortOrder(categoryDetails.getSortOrder());

        // Update parent if provided
        if (categoryDetails.getParent() != null && categoryDetails.getParent().getId() != null) {
            // Prevent circular references
            if (categoryDetails.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            Category parent = getCategoryById(categoryDetails.getParent().getId());
            category.setParent(parent);
        } else if (categoryDetails.getParent() == null) {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    public Category activateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    public Category deactivateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setActive(false);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        // Check if category has children
        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with child categories");
        }

        // Check if category has products
        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public long getChildrenCount(Long categoryId) {
        Category category = getCategoryById(categoryId);
        return categoryRepository.countByParent(category);
    }

    @Transactional(readOnly = true)
    public boolean isCategoryNameAvailable(String name) {
        return !categoryRepository.existsByName(name);
    }

    @Transactional(readOnly = true)
    public boolean isCategoryNameAvailable(String name, Long excludeId) {
        Optional<Category> existing = categoryRepository.findByName(name);
        return existing.isEmpty() || existing.get().getId().equals(excludeId);
    }

    public Category moveCategory(Long categoryId, Long newParentId) {
        Category category = getCategoryById(categoryId);

        if (newParentId != null) {
            Category newParent = getCategoryById(newParentId);

            // Prevent circular references
            if (isDescendantOf(newParent, category)) {
                throw new IllegalArgumentException("Cannot move category to its own descendant");
            }

            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    private boolean isDescendantOf(Category potential, Category ancestor) {
        Category current = potential.getParent();
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    public void reorderCategories(List<Long> categoryIds) {
        for (int i = 0; i < categoryIds.size(); i++) {
            Category category = getCategoryById(categoryIds.get(i));
            category.setSortOrder(i);
            categoryRepository.save(category);
        }
    }
}