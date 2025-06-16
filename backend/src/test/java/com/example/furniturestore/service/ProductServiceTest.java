package com.example.furniturestore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private com.example.furniturestore.search.ProductSearchService searchService;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_savesAndReturnsProduct() {
        Category category = new Category("Chairs", "desc");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Product saved = new Product("Chair", new BigDecimal("20.00"), category);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.create("Chair", new BigDecimal("20.00"), "desc", null, 1L, 5);

        assertEquals("Chair", result.getName());
        assertEquals(category, result.getCategory());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void findById_returnsProduct() {
        Product product = new Product("Table", BigDecimal.TEN);
        product.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(2L);

        assertTrue(result.isPresent());
        assertEquals("Table", result.get().getName());
    }
}
