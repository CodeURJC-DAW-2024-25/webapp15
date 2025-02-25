package com.stepx.stepx.service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.ShoeSizeStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ShoeRepository shoeRepository;
    private final ShoeSizeStockRepository shoeSizeStockRepository;

    public DataInitializer(ShoeRepository shoeRepository, ShoeSizeStockRepository shoeSizeStockRepository) {
        this.shoeRepository = shoeRepository;
        this.shoeSizeStockRepository = shoeSizeStockRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists to avoid duplicates
        if (shoeRepository.count() == 0 && shoeSizeStockRepository.count() == 0) {
            initializeShoesAndStocks();
        }
    }

    private void initializeShoesAndStocks() {
        // Create some sample shoes

        //create samples of image
        Blob image1 = loadImage("/static/images/shopimages/Nike-Air-grey.jpg");
        Blob image2 = loadImage("/static/images/shopimages/Nike-Air-blue.jpg");
        Blob image3 = loadImage("/static/images/shopimages/Nike-Air-red.jpg");

        Shoe shoe1 = new Shoe();
        shoe1.setName("Air Max 270");
        shoe1.setDescription("Nike Air Max 270 Running Shoes");
        shoe1.setPrice(new BigDecimal("150.00"));
        shoe1.setBrand(Shoe.Brand.NIKE);
        shoe1.setCategory(Shoe.Category.SPORT);
        shoe1.setImage1(image1);
        shoe1.setImage2(image2);
        shoe1.setImage3(image3);


        Shoe shoe2 = new Shoe();
        shoe2.setName("Stan Smith");
        shoe2.setDescription("Adidas Stan Smith Classic Sneakers");
        shoe2.setPrice(new BigDecimal("80.00"));
        shoe2.setBrand(Shoe.Brand.ADIDAS);
        shoe2.setCategory(Shoe.Category.CASUAL);
        shoe2.setImage1(image1);
        shoe2.setImage2(image2);
        shoe2.setImage3(image3);

        // Save shoes to the repository
        shoeRepository.save(shoe1);
        shoeRepository.save(shoe2);

        // Create size stocks for the first shoe
        ShoeSizeStock stock1 = new ShoeSizeStock();
        stock1.setShoe(shoe1);
        stock1.setSize("42");
        stock1.setStock(10);
        

        ShoeSizeStock stock2 = new ShoeSizeStock();
        stock2.setShoe(shoe1);
        stock2.setSize("43");
        stock2.setStock(5);

        // Create size stocks for the second shoe
        ShoeSizeStock stock3 = new ShoeSizeStock();
        stock3.setShoe(shoe2);
        stock3.setSize("40");
        stock3.setStock(15);

        ShoeSizeStock stock4 = new ShoeSizeStock();
        stock4.setShoe(shoe2);
        stock4.setSize("41");
        stock4.setStock(8);

        // Save size stocks to the repository
        shoeSizeStockRepository.save(stock1);
        shoeSizeStockRepository.save(stock2);
        shoeSizeStockRepository.save(stock3);
        shoeSizeStockRepository.save(stock4);

        System.out.println("Sample data initialized successfully!");
    }

    public Blob loadImage(String imagePath) {
        try {
            Resource resource = new ClassPathResource(imagePath);
            if (!resource.exists()) {
                System.out.println("Error: No se encontró la imagen en la ruta especificada.");
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] imageBytes = inputStream.readAllBytes();
                return new SerialBlob(imageBytes);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}