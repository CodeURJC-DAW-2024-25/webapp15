package com.stepx.stepx.service;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.repository.ShoeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final ShoeRepository shoeRepository;

    public DatabaseInitializer(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is already populated
        if (shoeRepository.count() == 0) {
            // Create sample shoes
            Shoe shoe1 = new Shoe(
                    "Running Shoe",
                    "Lightweight running shoe for daily use",
                    "This shoe is designed for runners who need a lightweight and comfortable shoe for daily training.",
                    120,
                    "Nike",
                    Arrays.asList("image1.jpg", "image2.jpg"),
                    Arrays.asList(5, 6, 7, 8, 9),
                    "Running",
                    "9"
            );

            Shoe shoe2 = new Shoe(
                    "Casual Sneaker",
                    "Stylish sneaker for everyday wear",
                    "A versatile sneaker that combines style and comfort, perfect for casual outings.",
                    90,
                    "Adidas",
                    Arrays.asList("image3.jpg", "image4.jpg"),
                    Arrays.asList(6, 7, 8, 9, 10),
                    "Casual",
                    "8"
            );

            // Save the shoes to the database
            shoeRepository.saveAll(Arrays.asList(shoe1, shoe2));
            System.out.println("Database initialized with sample shoes.");
        } else {
            System.out.println("Database already contains data. Skipping initialization.");
        }
    }
}