package com.example.furniturestore;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.CategoryRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FurnitureStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurnitureStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (categoryRepository.count() == 0) {
                Category seating = categoryRepository
                        .save(new Category("Seating", "Chairs and sofas"));
                Category tables = categoryRepository
                        .save(new Category("Tables", "Various tables"));
                Category storage = categoryRepository
                        .save(new Category("Storage", "Shelves and cabinets"));

                if (productRepository.count() == 0) {
                    Product chair = new Product("Chair",
                            new BigDecimal("49.99"),
                            "Comfortable wooden chair",
                            "https://via.placeholder.com/150",
                            seating);
                    chair.setStockQuantity(10);
                    productRepository.save(chair);

                    Product table = new Product("Dining Table",
                            new BigDecimal("149.99"),
                            "Large table for family meals",
                            "https://via.placeholder.com/150",
                            tables);
                    table.setStockQuantity(5);
                    productRepository.save(table);

                    Product shelf = new Product("Bookshelf",
                            new BigDecimal("89.99"),
                            "Spacious wooden bookshelf",
                            "https://via.placeholder.com/150",
                            storage);
                    shelf.setStockQuantity(8);
                    productRepository.save(shelf);
                }
            }
            if (userRepository.count() == 0) {
                userRepository.save(new User("Admin", "admin@example.com",
                        passwordEncoder.encode("password"), "ADMIN"));
            }
        };
    }
}
