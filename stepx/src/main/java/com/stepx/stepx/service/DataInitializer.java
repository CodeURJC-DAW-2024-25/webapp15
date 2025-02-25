package com.stepx.stepx.service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.ShoeSizeStockRepository;

import java.util.List;
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

        //create samples NIKE
        Blob image1 = loadImage("images/PRODUCS/NIKE/NK270_1.jpg");
        Blob image2 = loadImage("images/PRODUCS/NIKE/NK270_2.jpg");
        Blob image3 = loadImage("images/PRODUCS/NIKE/NK270_3.jpg");

        Shoe shoe1 = new Shoe();
        shoe1.setName("Nike 270");
        shoe1.setDescription("Nike Air Max 270 Running Shoes");
        shoe1.setPrice(new BigDecimal("150.00"));
        shoe1.setBrand(Shoe.Brand.NIKE);
        shoe1.setCategory(Shoe.Category.SPORT);
        shoe1.setImage1(image1);
        shoe1.setImage2(image2);
        shoe1.setImage3(image3);
        shoeRepository.save(shoe1);

        //second
        image1 = loadImage("images/PRODUCS/NIKE/NKair_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKair_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKair_3.jpg");

        Shoe shoe2 = new Shoe();
        shoe2.setName("Air Max 270");
        shoe2.setDescription("Nike air ");
        shoe2.setPrice(new BigDecimal("150.00"));
        shoe2.setBrand(Shoe.Brand.NIKE);
        shoe2.setCategory(Shoe.Category.CASUAL);
        shoe2.setImage1(image1);
        shoe2.setImage2(image2);
        shoe2.setImage3(image3);
        shoeRepository.save(shoe2);

        //thirt
        image1 = loadImage("images/PRODUCS/NIKE/NKairforce_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKairforce_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKairforce_3.jpg");

        Shoe shoe3 = new Shoe();
        shoe3.setName("Nike AirForce");
        shoe3.setDescription("Nike AirForce 1");
        shoe3.setPrice(new BigDecimal("150.00"));
        shoe3.setBrand(Shoe.Brand.NIKE);
        shoe3.setCategory(Shoe.Category.URBAN);
        shoe3.setImage1(image1);
        shoe3.setImage2(image2);
        shoe3.setImage3(image3);
        shoeRepository.save(shoe3);

        //four
        image1 = loadImage("images/PRODUCS/NIKE/NKairforce_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKairforce_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKairforce_3.jpg");

        Shoe shoe4 = new Shoe();
        shoe4.setName("Nike AirForce");
        shoe4.setDescription("Nike AirForce 1");
        shoe4.setPrice(new BigDecimal("150.00"));
        shoe4.setBrand(Shoe.Brand.NIKE);
        shoe4.setCategory(Shoe.Category.URBAN);
        shoe4.setImage1(image1);
        shoe4.setImage2(image2);
        shoe4.setImage3(image3);
        shoeRepository.save(shoe4);



        //create sisez dinamicamente para cada producto. 
        List<Shoe> savedShoes = shoeRepository.findAll();
        for (Shoe shoe : savedShoes) {
            ShoeSizeStock stock1 = new ShoeSizeStock();
            stock1.setShoe(shoe);
            stock1.setSize("S");
            stock1.setStock(10);
            shoeSizeStockRepository.save(stock1);
            ShoeSizeStock stock2 = new ShoeSizeStock();
            stock2.setShoe(shoe);
            stock2.setSize("M");
            stock2.setStock(10);
            shoeSizeStockRepository.save(stock2);
            ShoeSizeStock stock3 = new ShoeSizeStock();
            stock3.setShoe(shoe);
            stock3.setSize("L");
            stock3.setStock(10);
            shoeSizeStockRepository.save(stock3);
            ShoeSizeStock stock4 = new ShoeSizeStock();
            stock4.setShoe(shoe);
            stock4.setSize("XL");
            stock4.setStock(10);
            shoeSizeStockRepository.save(stock4);
        }


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