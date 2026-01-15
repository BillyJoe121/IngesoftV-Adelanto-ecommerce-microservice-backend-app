package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private Category parentCategory;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setCategoryId(1);
        parentCategory.setCategoryTitle("Electronics");
        parentCategory.setImageUrl("http://example.com/electronics.jpg");

        testCategory = new Category();
        testCategory.setCategoryId(2);
        testCategory.setCategoryTitle("Computers");
        testCategory.setImageUrl("http://example.com/computers.jpg");
        testCategory.setParentCategory(parentCategory);
    }

    @Test
    @DisplayName("Should return all categories")
    void findAll_ShouldReturnAllCategories() {
        Category category2 = new Category();
        category2.setCategoryId(3);
        category2.setCategoryTitle("Phones");
        category2.setImageUrl("http://example.com/phones.jpg");
        category2.setParentCategory(parentCategory);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory, category2));

        List<CategoryDto> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return category by ID")
    void findById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(2)).thenReturn(Optional.of(testCategory));

        CategoryDto result = categoryService.findById(2);

        assertNotNull(result);
        assertEquals("Computers", result.getCategoryTitle());
        verify(categoryRepository, times(1)).findById(2);
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    void findById_WhenCategoryNotExists_ShouldThrowException() {
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.findById(999);
        });

        verify(categoryRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save category successfully")
    void save_ShouldSaveAndReturnCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryTitle("Computers")
                .imageUrl("http://example.com/computers.jpg")
                .build();

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertEquals("Computers", result.getCategoryTitle());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should update category successfully")
    void update_ShouldUpdateAndReturnCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(2)
                .categoryTitle("Updated Computers")
                .imageUrl("http://example.com/updated-computers.jpg")
                .build();

        CategoryDto result = categoryService.update(categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should update category by ID successfully")
    void updateById_ShouldUpdateAndReturnCategory() {
        when(categoryRepository.findById(2)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryTitle("Updated Computers")
                .imageUrl("http://example.com/updated-computers.jpg")
                .build();

        CategoryDto result = categoryService.update(2, categoryDto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(2);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category by ID")
    void deleteById_ShouldDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(2);

        categoryService.deleteById(2);

        verify(categoryRepository, times(1)).deleteById(2);
    }

    @Test
    @DisplayName("Should return empty list when no categories exist")
    void findAll_WhenNoCategories_ShouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        List<CategoryDto> result = categoryService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle root category without parent")
    void save_RootCategory_ShouldSaveCategory() {
        Category rootCategory = new Category();
        rootCategory.setCategoryId(1);
        rootCategory.setCategoryTitle("Root Category");
        rootCategory.setImageUrl("http://example.com/root.jpg");
        rootCategory.setParentCategory(null);

        when(categoryRepository.save(any(Category.class))).thenReturn(rootCategory);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryTitle("Root Category")
                .imageUrl("http://example.com/root.jpg")
                .build();

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertEquals("Root Category", result.getCategoryTitle());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should handle category with null image URL")
    void save_WithNullImageUrl_ShouldSaveCategory() {
        Category categoryNoImage = new Category();
        categoryNoImage.setCategoryId(4);
        categoryNoImage.setCategoryTitle("No Image Category");
        categoryNoImage.setImageUrl(null);

        when(categoryRepository.save(any(Category.class))).thenReturn(categoryNoImage);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryTitle("No Image Category")
                .build();

        CategoryDto result = categoryService.save(categoryDto);

        assertNotNull(result);
        assertNull(result.getImageUrl());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}
