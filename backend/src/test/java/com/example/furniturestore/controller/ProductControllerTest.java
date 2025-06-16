package com.example.furniturestore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        category = categoryRepository.save(new Category("Chairs", "desc"));
        productRepository.save(new Product("Chair", new BigDecimal("20.00"), "Nice chair", null, category));
        productRepository.save(new Product("Table", new BigDecimal("30.00"), "Nice table", null, category));
    }

    @Test
    void getAll_returnsProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Chair"))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void filterByName_returnsSingleProduct() throws Exception {
        mockMvc.perform(get("/api/products").param("name", "Table"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Table"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_createsProduct() throws Exception {
        String json = String.format("{\"name\":\"Table\",\"price\":100,\"description\":\"desc\",\"categoryId\":%d}", category.getId());
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Table"));
    }
}
