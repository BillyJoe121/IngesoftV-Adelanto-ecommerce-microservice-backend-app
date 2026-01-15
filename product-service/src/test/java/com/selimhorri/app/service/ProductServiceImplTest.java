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
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryTitle("Electronics");
        testCategory.setImageUrl("http://example.com/electronics.jpg");

        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductTitle("Laptop");
        testProduct.setImageUrl("http://example.com/laptop.jpg");
        testProduct.setSku("LAP-001");
        testProduct.setPriceUnit(999.99);
        testProduct.setQuantity(50);
        testProduct.setCategory(testCategory);
    }

    @Test
    @DisplayName("Should return all products")
    void findAll_ShouldReturnAllProducts() {
        Product product2 = new Product();
        product2.setProductId(2);
        product2.setProductTitle("Smartphone");
        product2.setSku("PHN-001");
        product2.setPriceUnit(599.99);
        product2.setQuantity(100);
        product2.setCategory(testCategory);

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        List<ProductDto> result = productService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        ProductDto result = productService.findById(1);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductTitle());
        assertEquals("LAP-001", result.getSku());
        assertEquals(999.99, result.getPriceUnit());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void findById_WhenProductNotExists_ShouldThrowException() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.findById(999);
        });

        verify(productRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should save product successfully")
    void save_ShouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        ProductDto productDto = ProductDto.builder()
                .productTitle("Laptop")
                .sku("LAP-001")
                .priceUnit(999.99)
                .quantity(50)
                .categoryDto(categoryDto)
                .build();

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertEquals("Laptop", result.getProductTitle());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void update_ShouldUpdateAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        ProductDto productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Updated Laptop")
                .sku("LAP-001")
                .priceUnit(1099.99)
                .quantity(45)
                .categoryDto(categoryDto)
                .build();

        ProductDto result = productService.update(productDto);

        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product by ID successfully")
    void updateById_ShouldUpdateAndReturnProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDto productDto = ProductDto.builder()
                .productTitle("Updated Laptop")
                .sku("LAP-002")
                .priceUnit(1199.99)
                .quantity(40)
                .build();

        ProductDto result = productService.update(1, productDto);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product by ID")
    void deleteById_ShouldDeleteProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));

        productService.deleteById(1);

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void findAll_WhenNoProducts_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<ProductDto> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle product with zero quantity")
    void save_WithZeroQuantity_ShouldSaveProduct() {
        Product outOfStockProduct = new Product();
        outOfStockProduct.setProductId(3);
        outOfStockProduct.setProductTitle("Out of Stock Item");
        outOfStockProduct.setSku("OOS-001");
        outOfStockProduct.setPriceUnit(49.99);
        outOfStockProduct.setQuantity(0);
        outOfStockProduct.setCategory(testCategory);

        when(productRepository.save(any(Product.class))).thenReturn(outOfStockProduct);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        ProductDto productDto = ProductDto.builder()
                .productTitle("Out of Stock Item")
                .sku("OOS-001")
                .priceUnit(49.99)
                .quantity(0)
                .categoryDto(categoryDto)
                .build();

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle product with high price")
    void save_WithHighPrice_ShouldSaveProduct() {
        Product expensiveProduct = new Product();
        expensiveProduct.setProductId(4);
        expensiveProduct.setProductTitle("Luxury Item");
        expensiveProduct.setSku("LUX-001");
        expensiveProduct.setPriceUnit(99999.99);
        expensiveProduct.setQuantity(5);
        expensiveProduct.setCategory(testCategory);

        when(productRepository.save(any(Product.class))).thenReturn(expensiveProduct);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();

        ProductDto productDto = ProductDto.builder()
                .productTitle("Luxury Item")
                .sku("LUX-001")
                .priceUnit(99999.99)
                .quantity(5)
                .categoryDto(categoryDto)
                .build();

        ProductDto result = productService.save(productDto);

        assertNotNull(result);
        assertEquals(99999.99, result.getPriceUnit());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
