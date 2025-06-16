package com.example.furniturestore;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.ProductRepository;

@SpringBootApplication
public class FurnitureStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurnitureStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Product("Chair", new BigDecimal("49.99")));
                repository.save(new Product("Table", new BigDecimal("149.99")));
                repository.save(new Product("Sofa", new BigDecimal("499.99")));
            }
        };
    }
}
