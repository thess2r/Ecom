package com.ecommerce.product.controllers;

import com.ecommerce.product.models.Product;
import com.ecommerce.product.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getProductByIdReturnsActiveProduct() throws Exception {
        Product product = saveProduct(true);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Test product"))
                .andExpect(jsonPath("$.price").value(1250.50))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getProductByIdReturnsNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductByIdReturnsNotFoundWhenProductIsInactive() throws Exception {
        Product product = saveProduct(false);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }

    private Product saveProduct(boolean active) {
        Product product = new Product();
        product.setName("Test product");
        product.setDescription("Product used in integration tests");
        product.setPrice(new BigDecimal("1250.50"));
        product.setStockQuantity(10);
        product.setCategory("test");
        product.setActive(active);
        return productRepository.saveAndFlush(product);
    }
}
