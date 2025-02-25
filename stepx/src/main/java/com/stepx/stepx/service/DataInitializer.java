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
    private Blob image1;
    private Blob image2;
    private Blob image3;

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
        // Código reorganizado de manera aleatoria

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Beta_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Beta_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Beta_3.jpg");

        Shoe shoe31 = new Shoe();
        shoe31.setName("Adidas Campus 00s Beta");
        shoe31.setDescription("null");
        shoe31.setLongDescription("null");
        shoe31.setPrice(new BigDecimal("120.00"));
        shoe31.setBrand(Shoe.Brand.ADIDAS);
        shoe31.setCategory(Shoe.Category.URBAN);
        shoe31.setImage1(image1);
        shoe31.setImage2(image2);
        shoe31.setImage3(image3);
        shoeRepository.save(shoe31);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Anzarun-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Anzarun-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Anzarun-Black_3.jpg");

        Shoe shoe46 = new Shoe();
        shoe46.setName("Puma Anzarun Black");
        shoe46.setDescription("null");
        shoe46.setLongDescription("null");
        shoe46.setPrice(new BigDecimal("76.00"));
        shoe46.setBrand(Shoe.Brand.PUMA);
        shoe46.setCategory(Shoe.Category.URBAN);
        shoe46.setImage1(image1);
        shoe46.setImage2(image2);
        shoe46.setImage3(image3);
        shoeRepository.save(shoe46);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NK270_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NK270_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NK270_3.jpg");

        Shoe shoe1 = new Shoe();
        shoe1.setName("Nike 270");
        shoe1.setDescription("Nike Air Max 270 Running Shoes");
        shoe1.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe1.setPrice(new BigDecimal("150.00"));
        shoe1.setBrand(Shoe.Brand.NIKE);
        shoe1.setCategory(Shoe.Category.SPORT);
        shoe1.setImage1(image1);
        shoe1.setImage2(image2);
        shoe1.setImage3(image3);
        shoeRepository.save(shoe1);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB111_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB111_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB111_3.jpg");

        Shoe shoe16 = new Shoe();
        shoe16.setName("Nike AirForce");
        shoe16.setDescription("Nike AirForce 1");
        shoe16.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe16.setPrice(new BigDecimal("150.00"));
        shoe16.setBrand(Shoe.Brand.NIKE);
        shoe16.setCategory(Shoe.Category.URBAN);
        shoe16.setImage1(image1);
        shoe16.setImage2(image2);
        shoe16.setImage3(image3);
        shoeRepository.save(shoe16);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Black_3.jpg");

        Shoe shoe32 = new Shoe();
        shoe32.setName("Adidas Campus 00s Black");
        shoe32.setDescription("null");
        shoe32.setLongDescription("null");
        shoe32.setPrice(new BigDecimal("100.00"));
        shoe32.setBrand(Shoe.Brand.ADIDAS);
        shoe32.setCategory(Shoe.Category.URBAN);
        shoe32.setImage1(image1);
        shoe32.setImage2(image2);
        shoe32.setImage3(image3);
        shoeRepository.save(shoe32);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Arizona-Yellow-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Arizona-Yellow-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Arizona-Yellow-Black_3.jpg");

        Shoe shoe47 = new Shoe();
        shoe47.setName("Puma Arizona Yellow Black");
        shoe47.setDescription("null");
        shoe47.setLongDescription("null");
        shoe47.setPrice(new BigDecimal("90.00"));
        shoe47.setBrand(Shoe.Brand.PUMA);
        shoe47.setCategory(Shoe.Category.URBAN);
        shoe47.setImage1(image1);
        shoe47.setImage2(image2);
        shoe47.setImage3(image3);
        shoeRepository.save(shoe47);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKair_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKair_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKair_3.jpg");

        Shoe shoe2 = new Shoe();
        shoe2.setName("Air Max 270");
        shoe2.setDescription("Nike air ");
        shoe2.setPrice(new BigDecimal("150.00"));
        shoe2.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe2.setBrand(Shoe.Brand.NIKE);
        shoe2.setCategory(Shoe.Category.CASUAL);
        shoe2.setImage1(image1);
        shoe2.setImage2(image2);
        shoe2.setImage3(image3);
        shoeRepository.save(shoe2);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB327_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB327_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB327_3.jpg");

        Shoe shoe17 = new Shoe();
        shoe17.setName("Nike AirForce");
        shoe17.setDescription("Nike AirForce 1");
        shoe17.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe17.setPrice(new BigDecimal("150.00"));
        shoe17.setBrand(Shoe.Brand.NIKE);
        shoe17.setCategory(Shoe.Category.URBAN);
        shoe17.setImage1(image1);
        shoe17.setImage2(image2);
        shoe17.setImage3(image3);
        shoeRepository.save(shoe17);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Skyblue_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Skyblue_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Campus-00s-Skyblue_3.jpg");

        Shoe shoe33 = new Shoe();
        shoe33.setName("Adidas Campus 00s Skyblue");
        shoe33.setDescription("null");
        shoe33.setLongDescription("null");
        shoe33.setPrice(new BigDecimal("90.00"));
        shoe33.setBrand(Shoe.Brand.ADIDAS);
        shoe33.setCategory(Shoe.Category.URBAN);
        shoe33.setImage1(image1);
        shoe33.setImage2(image2);
        shoe33.setImage3(image3);
        shoeRepository.save(shoe33);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Disperse-Tech_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Disperse-Tech_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Disperse-Tech_3.jpg");

        Shoe shoe48 = new Shoe();
        shoe48.setName("Puma Disperse Tech");
        shoe48.setDescription("null");
        shoe48.setLongDescription("null");
        shoe48.setPrice(new BigDecimal("50.00"));
        shoe48.setBrand(Shoe.Brand.PUMA);
        shoe48.setCategory(Shoe.Category.URBAN);
        shoe48.setImage1(image1);
        shoe48.setImage2(image2);
        shoe48.setImage3(image3);
        shoeRepository.save(shoe48);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKairforce_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKairforce_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKairforce_3.jpg");

        Shoe shoe3 = new Shoe();
        shoe3.setName("Nike AirForce");
        shoe3.setDescription("Nike AirForce 1");
        shoe3.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe3.setPrice(new BigDecimal("150.00"));
        shoe3.setBrand(Shoe.Brand.NIKE);
        shoe3.setCategory(Shoe.Category.URBAN);
        shoe3.setImage1(image1);
        shoe3.setImage2(image2);
        shoe3.setImage3(image3);
        shoeRepository.save(shoe3);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB370_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB370_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB370_3.jpg");

        Shoe shoe18 = new Shoe();
        shoe18.setName("Nike AirForce");
        shoe18.setDescription("Nike AirForce 1");
        shoe18.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe18.setPrice(new BigDecimal("150.00"));
        shoe18.setBrand(Shoe.Brand.NIKE);
        shoe18.setCategory(Shoe.Category.URBAN);
        shoe18.setImage1(image1);
        shoe18.setImage2(image2);
        shoe18.setImage3(image3);
        shoeRepository.save(shoe18);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Campus-Originals-Grey_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Campus-Originals-Grey_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Campus-Originals-Grey_3.jpg");

        Shoe shoe34 = new Shoe();
        shoe34.setName("Adidas Campus Originals Grey");
        shoe34.setDescription("null");
        shoe34.setLongDescription("null");
        shoe34.setPrice(new BigDecimal("80.00"));
        shoe34.setBrand(Shoe.Brand.ADIDAS);
        shoe34.setCategory(Shoe.Category.URBAN);
        shoe34.setImage1(image1);
        shoe34.setImage2(image2);
        shoe34.setImage3(image3);
        shoeRepository.save(shoe34);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Flex-Focus_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Flex-Focus_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Flex-Focus_3.jpg");

        Shoe shoe49 = new Shoe();
        shoe49.setName("Puma Flex Focus");
        shoe49.setDescription("null");
        shoe49.setLongDescription("null");
        shoe49.setPrice(new BigDecimal("70.00"));
        shoe49.setBrand(Shoe.Brand.PUMA);
        shoe49.setCategory(Shoe.Category.URBAN);
        shoe49.setImage1(image1);
        shoe49.setImage2(image2);
        shoe49.setImage3(image3);
        shoeRepository.save(shoe49);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKairmax_black_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKairmax_black_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKairmax_black_3.jpg");

        Shoe shoe4 = new Shoe();
        shoe4.setName("Nike AirForce");
        shoe4.setDescription("Nike AirForce 1");
        shoe4.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe4.setPrice(new BigDecimal("150.00"));
        shoe4.setBrand(Shoe.Brand.NIKE);
        shoe4.setCategory(Shoe.Category.URBAN);
        shoe4.setImage1(image1);
        shoe4.setImage2(image2);
        shoe4.setImage3(image3);
        shoeRepository.save(shoe4);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB405_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB405_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB405_3.jpg");

        Shoe shoe19 = new Shoe();
        shoe19.setName("Nike AirForce");
        shoe19.setDescription("Nike AirForce 1");
        shoe19.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe19.setPrice(new BigDecimal("150.00"));
        shoe19.setBrand(Shoe.Brand.NIKE);
        shoe19.setCategory(Shoe.Category.URBAN);
        shoe19.setImage1(image1);
        shoe19.setImage2(image2);
        shoe19.setImage3(image3);
        shoeRepository.save(shoe19);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Dame-9_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Dame-9_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Dame-9_3.jpg");

        Shoe shoe35 = new Shoe();
        shoe35.setName("Adidas Dame 9");
        shoe35.setDescription("null");
        shoe35.setLongDescription("null");
        shoe35.setPrice(new BigDecimal("50.00"));
        shoe35.setBrand(Shoe.Brand.ADIDAS);
        shoe35.setCategory(Shoe.Category.URBAN);
        shoe35.setImage1(image1);
        shoe35.setImage2(image2);
        shoe35.setImage3(image3);
        shoeRepository.save(shoe35);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Future-Plat-TT_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Future-Plat-TT_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Future-Plat-TT_3.jpg");

        Shoe shoe50 = new Shoe();
        shoe50.setName("Puma Future Plat TT");
        shoe50.setDescription("null");
        shoe50.setLongDescription("null");
        shoe50.setPrice(new BigDecimal("130.00"));
        shoe50.setBrand(Shoe.Brand.PUMA);
        shoe50.setCategory(Shoe.Category.URBAN);
        shoe50.setImage1(image1);
        shoe50.setImage2(image2);
        shoe50.setImage3(image3);
        shoeRepository.save(shoe50);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKblack_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKblack_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKblack_3.jpg");

        Shoe shoe5 = new Shoe();
        shoe5.setName("Nike AirForce");
        shoe5.setDescription("Nike AirForce 1");
        shoe5.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe5.setPrice(new BigDecimal("150.00"));
        shoe5.setBrand(Shoe.Brand.NIKE);
        shoe5.setCategory(Shoe.Category.URBAN);
        shoe5.setImage1(image1);
        shoe5.setImage2(image2);
        shoe5.setImage3(image3);
        shoeRepository.save(shoe5);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB530_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB530_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB530_3.jpg");

        Shoe shoe20 = new Shoe();
        shoe20.setName("Nike AirForce");
        shoe20.setDescription("Nike AirForce 1");
        shoe20.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe20.setPrice(new BigDecimal("150.00"));
        shoe20.setBrand(Shoe.Brand.NIKE);
        shoe20.setCategory(Shoe.Category.URBAN);
        shoe20.setImage1(image1);
        shoe20.setImage2(image2);
        shoe20.setImage3(image3);
        shoeRepository.save(shoe20);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Green_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Green_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Green_3.jpg");

        Shoe shoe36 = new Shoe();
        shoe36.setName("Adidas Gazelle Originals Green");
        shoe36.setDescription("null");
        shoe36.setLongDescription("null");
        shoe36.setPrice(new BigDecimal("70.00"));
        shoe36.setBrand(Shoe.Brand.ADIDAS);
        shoe36.setCategory(Shoe.Category.CASUAL);
        shoe36.setImage1(image1);
        shoe36.setImage2(image2);
        shoe36.setImage3(image3);
        shoeRepository.save(shoe36);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Palermo-Brown_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Palermo-Brown_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Palermo-Brown_3.jpg");

        Shoe shoe51 = new Shoe();
        shoe51.setName("Puma Palermo Brown");
        shoe51.setDescription("null");
        shoe51.setLongDescription("null");
        shoe51.setPrice(new BigDecimal("70.00"));
        shoe51.setBrand(Shoe.Brand.PUMA);
        shoe51.setCategory(Shoe.Category.URBAN);
        shoe51.setImage1(image1);
        shoe51.setImage2(image2);
        shoe51.setImage3(image3);
        shoeRepository.save(shoe51);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKdn_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKdn_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKdn_3.jpg");

        Shoe shoe6 = new Shoe();
        shoe6.setName("Nike AirForce");
        shoe6.setDescription("Nike AirForce 1");
        shoe6.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe6.setPrice(new BigDecimal("150.00"));
        shoe6.setBrand(Shoe.Brand.NIKE);
        shoe6.setCategory(Shoe.Category.URBAN);
        shoe6.setImage1(image1);
        shoe6.setImage2(image2);
        shoe6.setImage3(image3);
        shoeRepository.save(shoe6);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB550_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB550_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB550_3.jpg");

        Shoe shoe21 = new Shoe();
        shoe21.setName("Nike AirForce");
        shoe21.setDescription("Nike AirForce 1");
        shoe21.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe21.setPrice(new BigDecimal("150.00"));
        shoe21.setBrand(Shoe.Brand.NIKE);
        shoe21.setCategory(Shoe.Category.URBAN);
        shoe21.setImage1(image1);
        shoe21.setImage2(image2);
        shoe21.setImage3(image3);
        shoeRepository.save(shoe21);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Real-Madrid_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Real-Madrid_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Gazelle-Originals-Real-Madrid_3.jpg");

        Shoe shoe37 = new Shoe();
        shoe37.setName("Adidas Gazelle Originals Real Madrid");
        shoe37.setDescription("null");
        shoe37.setLongDescription("null");
        shoe37.setPrice(new BigDecimal("60.00"));
        shoe37.setBrand(Shoe.Brand.ADIDAS);
        shoe37.setCategory(Shoe.Category.CASUAL);
        shoe37.setImage1(image1);
        shoe37.setImage2(image2);
        shoe37.setImage3(image3);
        shoeRepository.save(shoe37);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Palermo-Green-Pink_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Palermo-Green-Pink_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Palermo-Green-Pink_3.jpg");

        Shoe shoe52 = new Shoe();
        shoe52.setName("Puma Palermo Green Pink");
        shoe52.setDescription("null");
        shoe52.setLongDescription("null");
        shoe52.setPrice(new BigDecimal("110.00"));
        shoe52.setBrand(Shoe.Brand.PUMA);
        shoe52.setCategory(Shoe.Category.URBAN);
        shoe52.setImage1(image1);
        shoe52.setImage2(image2);
        shoe52.setImage3(image3);
        shoeRepository.save(shoe52);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKdunk_grey_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKdunk_grey_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKdunk_grey_3.jpg");

        Shoe shoe7 = new Shoe();
        shoe7.setName("Nike AirForce");
        shoe7.setDescription("Nike AirForce 1");
        shoe7.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe7.setPrice(new BigDecimal("150.00"));
        shoe7.setBrand(Shoe.Brand.NIKE);
        shoe7.setCategory(Shoe.Category.URBAN);
        shoe7.setImage1(image1);
        shoe7.setImage2(image2);
        shoe7.setImage3(image3);
        shoeRepository.save(shoe7);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB601_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB601_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB601_3.jpg");

        Shoe shoe22 = new Shoe();
        shoe22.setName("Nike AirForce");
        shoe22.setDescription("Nike AirForce 1");
        shoe22.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe22.setPrice(new BigDecimal("150.00"));
        shoe22.setBrand(Shoe.Brand.NIKE);
        shoe22.setCategory(Shoe.Category.URBAN);
        shoe22.setImage1(image1);
        shoe22.setImage2(image2);
        shoe22.setImage3(image3);
        shoeRepository.save(shoe22);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Handball-Originals-Spezial_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Handball-Originals-Spezial_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Handball-Originals-Spezial_3.jpg");

        Shoe shoe38 = new Shoe();
        shoe38.setName("Adidas Handball Originals Spezial");
        shoe38.setDescription("null");
        shoe38.setLongDescription("null");
        shoe38.setPrice(new BigDecimal("50.00"));
        shoe38.setBrand(Shoe.Brand.ADIDAS);
        shoe38.setCategory(Shoe.Category.CASUAL);
        shoe38.setImage1(image1);
        shoe38.setImage2(image2);
        shoe38.setImage3(image3);
        shoeRepository.save(shoe38);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Palermo-Premium-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Palermo-Premium-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Palermo-Premium-Black_3.jpg");

        Shoe shoe53 = new Shoe();
        shoe53.setName("Puma Palermo Premium Black");
        shoe53.setDescription("null");
        shoe53.setLongDescription("null");
        shoe53.setPrice(new BigDecimal("100.00"));
        shoe53.setBrand(Shoe.Brand.PUMA);
        shoe53.setCategory(Shoe.Category.URBAN);
        shoe53.setImage1(image1);
        shoe53.setImage2(image2);
        shoe53.setImage3(image3);
        shoeRepository.save(shoe53);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKmercu_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKmercu_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKmercu_3.jpg");

        Shoe shoe8 = new Shoe();
        shoe8.setName("Nike AirForce");
        shoe8.setDescription("Nike AirForce 1");
        shoe8.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe8.setPrice(new BigDecimal("150.00"));
        shoe8.setBrand(Shoe.Brand.NIKE);
        shoe8.setCategory(Shoe.Category.URBAN);
        shoe8.setImage1(image1);
        shoe8.setImage2(image2);
        shoe8.setImage3(image3);
        shoeRepository.save(shoe8);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB705_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB705_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB705_3.jpg");

        Shoe shoe23 = new Shoe();
        shoe23.setName("Nike AirForce");
        shoe23.setDescription("Nike AirForce 1");
        shoe23.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe23.setPrice(new BigDecimal("150.00"));
        shoe23.setBrand(Shoe.Brand.NIKE);
        shoe23.setCategory(Shoe.Category.URBAN);
        shoe23.setImage1(image1);
        shoe23.setImage2(image2);
        shoe23.setImage3(image3);
        shoeRepository.save(shoe23);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Predator-Elite_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Predator-Elite_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Predator-Elite_3.jpg");

        Shoe shoe39 = new Shoe();
        shoe39.setName("Adidas Predator Elite");
        shoe39.setDescription("null");
        shoe39.setLongDescription("null");
        shoe39.setPrice(new BigDecimal("75.00"));
        shoe39.setBrand(Shoe.Brand.ADIDAS);
        shoe39.setCategory(Shoe.Category.SPORT);
        shoe39.setImage1(image1);
        shoe39.setImage2(image2);
        shoe39.setImage3(image3);
        shoeRepository.save(shoe39);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Palermo-Skyblue_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Palermo-Skyblue_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Palermo-Skyblue_3.jpg");

        Shoe shoe54 = new Shoe();
        shoe54.setName("Puma Palermo Skyblue");
        shoe54.setDescription("null");
        shoe54.setLongDescription("null");
        shoe54.setPrice(new BigDecimal("87.00"));
        shoe54.setBrand(Shoe.Brand.PUMA);
        shoe54.setCategory(Shoe.Category.URBAN);
        shoe54.setImage1(image1);
        shoe54.setImage2(image2);
        shoe54.setImage3(image3);
        shoeRepository.save(shoe54);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKmid1_blue_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKmid1_blue_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKmid1_blue_3.jpg");

        Shoe shoe9 = new Shoe();
        shoe9.setName("Nike AirForce");
        shoe9.setDescription("Nike AirForce 1");
        shoe9.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe9.setPrice(new BigDecimal("150.00"));
        shoe9.setBrand(Shoe.Brand.NIKE);
        shoe9.setCategory(Shoe.Category.URBAN);
        shoe9.setImage1(image1);
        shoe9.setImage2(image2);
        shoe9.setImage3(image3);
        shoeRepository.save(shoe9);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB740_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB740_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB740_3.jpg");

        Shoe shoe24 = new Shoe();
        shoe24.setName("Nike AirForce");
        shoe24.setDescription("Nike AirForce 1");
        shoe24.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe24.setPrice(new BigDecimal("150.00"));
        shoe24.setBrand(Shoe.Brand.NIKE);
        shoe24.setCategory(Shoe.Category.URBAN);
        shoe24.setImage1(image1);
        shoe24.setImage2(image2);
        shoe24.setImage3(image3);
        shoeRepository.save(shoe24);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-Black_3.jpg");

        Shoe shoe40 = new Shoe();
        shoe40.setName("Adidas Samba Originals Black");
        shoe40.setDescription("null");
        shoe40.setLongDescription("null");
        shoe40.setPrice(new BigDecimal("90.00"));
        shoe40.setBrand(Shoe.Brand.ADIDAS);
        shoe40.setCategory(Shoe.Category.URBAN);
        shoe40.setImage1(image1);
        shoe40.setImage2(image2);
        shoe40.setImage3(image3);
        shoeRepository.save(shoe40);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Smash-Suede-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Smash-Suede-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Smash-Suede-Black_3.jpg");

        Shoe shoe55 = new Shoe();
        shoe55.setName("Puma Smash Suede Black");
        shoe55.setDescription("null");
        shoe55.setLongDescription("null");
        shoe55.setPrice(new BigDecimal("80.00"));
        shoe55.setBrand(Shoe.Brand.PUMA);
        shoe55.setCategory(Shoe.Category.URBAN);
        shoe55.setImage1(image1);
        shoe55.setImage2(image2);
        shoe55.setImage3(image3);
        shoeRepository.save(shoe55);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKmid1_green_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKmid1_green_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKmid1_green_3.jpg");

        Shoe shoe10 = new Shoe();
        shoe10.setName("Nike AirForce");
        shoe10.setDescription("Nike AirForce 1");
        shoe10.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe10.setPrice(new BigDecimal("150.00"));
        shoe10.setBrand(Shoe.Brand.NIKE);
        shoe10.setCategory(Shoe.Category.URBAN);
        shoe10.setImage1(image1);
        shoe10.setImage2(image2);
        shoe10.setImage3(image3);
        shoeRepository.save(shoe10);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NBWRPD_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NBWRPD_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NBWRPD_3.jpg");

        Shoe shoe25 = new Shoe();
        shoe25.setName("Nike AirForce");
        shoe25.setDescription("Nike AirForce 1");
        shoe25.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe25.setPrice(new BigDecimal("150.00"));
        shoe25.setBrand(Shoe.Brand.NIKE);
        shoe25.setCategory(Shoe.Category.URBAN);
        shoe25.setImage1(image1);
        shoe25.setImage2(image2);
        shoe25.setImage3(image3);
        shoeRepository.save(shoe25);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-Red_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-Red_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-Red_3.jpg");

        Shoe shoe41 = new Shoe();
        shoe41.setName("Adidas Samba Originals OG Red");
        shoe41.setDescription("null");
        shoe41.setLongDescription("null");
        shoe41.setPrice(new BigDecimal("80.00"));
        shoe41.setBrand(Shoe.Brand.ADIDAS);
        shoe41.setCategory(Shoe.Category.CASUAL);
        shoe41.setImage1(image1);
        shoe41.setImage2(image2);
        shoe41.setImage3(image3);
        shoeRepository.save(shoe41);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Black_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Black_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Black_3.jpg");

        Shoe shoe56 = new Shoe();
        shoe56.setName("Puma Speedcat OG Black");
        shoe56.setDescription("null");
        shoe56.setLongDescription("null");
        shoe56.setPrice(new BigDecimal("74.00"));
        shoe56.setBrand(Shoe.Brand.PUMA);
        shoe56.setCategory(Shoe.Category.URBAN);
        shoe56.setImage1(image1);
        shoe56.setImage2(image2);
        shoe56.setImage3(image3);
        shoeRepository.save(shoe56);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKmid1_grey_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKmid1_grey_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKmid1_grey_3.jpg");

        Shoe shoe11 = new Shoe();
        shoe11.setName("Nike AirForce");
        shoe11.setDescription("Nike AirForce 1");
        shoe11.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe11.setPrice(new BigDecimal("150.00"));
        shoe11.setBrand(Shoe.Brand.NIKE);
        shoe11.setCategory(Shoe.Category.URBAN);
        shoe11.setImage1(image1);
        shoe11.setImage2(image2);
        shoe11.setImage3(image3);
        shoeRepository.save(shoe11);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB1000_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB1000_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB1000_3.jpg");

        Shoe shoe26 = new Shoe();
        shoe26.setName("Nike AirForce");
        shoe26.setDescription("Nike AirForce 1");
        shoe26.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe26.setPrice(new BigDecimal("150.00"));
        shoe26.setBrand(Shoe.Brand.NIKE);
        shoe26.setCategory(Shoe.Category.URBAN);
        shoe26.setImage1(image1);
        shoe26.setImage2(image2);
        shoe26.setImage3(image3);
        shoeRepository.save(shoe26);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-White_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-White_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/Samba-Originals-OG-White_3.jpg");

        Shoe shoe42 = new Shoe();
        shoe42.setName("Adidas Samba Originals OG White");
        shoe42.setDescription("null");
        shoe42.setLongDescription("null");
        shoe42.setPrice(new BigDecimal("70.00"));
        shoe42.setBrand(Shoe.Brand.ADIDAS);
        shoe42.setCategory(Shoe.Category.URBAN);
        shoe42.setImage1(image1);
        shoe42.setImage2(image2);
        shoe42.setImage3(image3);
        shoeRepository.save(shoe42);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Green_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Green_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Green_3.jpg");

        Shoe shoe57 = new Shoe();
        shoe57.setName("Puma Speedcat OG Green");
        shoe57.setDescription("null");
        shoe57.setLongDescription("null");
        shoe57.setPrice(new BigDecimal("70.00"));
        shoe57.setBrand(Shoe.Brand.PUMA);
        shoe57.setCategory(Shoe.Category.URBAN);
        shoe57.setImage1(image1);
        shoe57.setImage2(image2);
        shoe57.setImage3(image3);
        shoeRepository.save(shoe57);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKshot_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKshot_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKshot_3.jpg");

        Shoe shoe12 = new Shoe();
        shoe12.setName("Nike AirForce");
        shoe12.setDescription("Nike AirForce 1");
        shoe12.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe12.setPrice(new BigDecimal("150.00"));
        shoe12.setBrand(Shoe.Brand.NIKE);
        shoe12.setCategory(Shoe.Category.URBAN);
        shoe12.setImage1(image1);
        shoe12.setImage2(image2);
        shoe12.setImage3(image3);
        shoeRepository.save(shoe12);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB2022R_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB2022R_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB2022R_3.jpg");

        Shoe shoe27 = new Shoe();
        shoe27.setName("Nike AirForce");
        shoe27.setDescription("Nike AirForce 1");
        shoe27.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe27.setPrice(new BigDecimal("150.00"));
        shoe27.setBrand(Shoe.Brand.NIKE);
        shoe27.setCategory(Shoe.Category.URBAN);
        shoe27.setImage1(image1);
        shoe27.setImage2(image2);
        shoe27.setImage3(image3);
        shoeRepository.save(shoe27);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/SL-Originals-72_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/SL-Originals-72_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/SL-Originals-72_3.jpg");

        Shoe shoe43 = new Shoe();
        shoe43.setName("Adidas SL Originals 72");
        shoe43.setDescription("null");
        shoe43.setLongDescription("null");
        shoe43.setPrice(new BigDecimal("60.00"));
        shoe43.setBrand(Shoe.Brand.ADIDAS);
        shoe43.setCategory(Shoe.Category.URBAN);
        shoe43.setImage1(image1);
        shoe43.setImage2(image2);
        shoe43.setImage3(image3);
        shoeRepository.save(shoe43);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Pink_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Pink_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Pink_3.jpg");

        Shoe shoe58 = new Shoe();
        shoe58.setName("Puma Speedcat OG Pink");
        shoe58.setDescription("null");
        shoe58.setLongDescription("null");
        shoe58.setPrice(new BigDecimal("60.00"));
        shoe58.setBrand(Shoe.Brand.PUMA);
        shoe58.setCategory(Shoe.Category.URBAN);
        shoe58.setImage1(image1);
        shoe58.setImage2(image2);
        shoe58.setImage3(image3);
        shoeRepository.save(shoe58);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKsl_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKsl_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKsl_3.jpg");

        Shoe shoe13 = new Shoe();
        shoe13.setName("Nike AirForce");
        shoe13.setDescription("Nike AirForce 1");
        shoe13.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe13.setPrice(new BigDecimal("150.00"));
        shoe13.setBrand(Shoe.Brand.NIKE);
        shoe13.setCategory(Shoe.Category.URBAN);
        shoe13.setImage1(image1);
        shoe13.setImage2(image2);
        shoe13.setImage3(image3);
        shoeRepository.save(shoe13);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB3030_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB3030_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB3030_3.jpg");

        Shoe shoe28 = new Shoe();
        shoe28.setName("Nike AirForce");
        shoe28.setDescription("Nike AirForce 1");
        shoe28.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe28.setPrice(new BigDecimal("150.00"));
        shoe28.setBrand(Shoe.Brand.NIKE);
        shoe28.setCategory(Shoe.Category.URBAN);
        shoe28.setImage1(image1);
        shoe28.setImage2(image2);
        shoe28.setImage3(image3);
        shoeRepository.save(shoe28);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-750_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-750_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-750_3.jpg");

        Shoe shoe44 = new Shoe();
        shoe44.setName("Adidas ZX Originals 750");
        shoe44.setDescription("null");
        shoe44.setLongDescription("null");
        shoe44.setPrice(new BigDecimal("50.00"));
        shoe44.setBrand(Shoe.Brand.ADIDAS);
        shoe44.setCategory(Shoe.Category.URBAN);
        shoe44.setImage1(image1);
        shoe44.setImage2(image2);
        shoe44.setImage3(image3);
        shoeRepository.save(shoe44);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Red_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Red_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Speedcat-OG-Red_3.jpg");

        Shoe shoe59 = new Shoe();
        shoe59.setName("Puma Speedcat OG Red");
        shoe59.setDescription("null");
        shoe59.setLongDescription("null");
        shoe59.setPrice(new BigDecimal("50.00"));
        shoe59.setBrand(Shoe.Brand.PUMA);
        shoe59.setCategory(Shoe.Category.URBAN);
        shoe59.setImage1(image1);
        shoe59.setImage2(image2);
        shoe59.setImage3(image3);
        shoeRepository.save(shoe59);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKup_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKup_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKup_3.jpg");

        Shoe shoe14 = new Shoe();
        shoe14.setName("Nike AirForce");
        shoe14.setDescription("Nike AirForce 1");
        shoe14.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe14.setPrice(new BigDecimal("150.00"));
        shoe14.setBrand(Shoe.Brand.NIKE);
        shoe14.setCategory(Shoe.Category.URBAN);
        shoe14.setImage1(image1);
        shoe14.setImage2(image2);
        shoe14.setImage3(image3);
        shoeRepository.save(shoe14);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NB9060_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NB9060_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NB9060_3.jpg");

        Shoe shoe29 = new Shoe();
        shoe29.setName("Nike AirForce");
        shoe29.setDescription("Nike AirForce 1");
        shoe29.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe29.setPrice(new BigDecimal("150.00"));
        shoe29.setBrand(Shoe.Brand.NIKE);
        shoe29.setCategory(Shoe.Category.URBAN);
        shoe29.setImage1(image1);
        shoe29.setImage2(image2);
        shoe29.setImage3(image3);
        shoeRepository.save(shoe29);

        // Adidas
        image1 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-Flux-2_1.jpg");
        image2 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-Flux-2_2.jpg");
        image3 = loadImage("images/PRODUCS/ADIDAS/ZX-Originals-Flux-2_3.jpg");

        Shoe shoe45 = new Shoe();
        shoe45.setName("Adidas ZX Originals Flux 2");
        shoe45.setDescription("null");
        shoe45.setLongDescription("null");
        shoe45.setPrice(new BigDecimal("80.00"));
        shoe45.setBrand(Shoe.Brand.ADIDAS);
        shoe45.setCategory(Shoe.Category.URBAN);
        shoe45.setImage1(image1);
        shoe45.setImage2(image2);
        shoe45.setImage3(image3);
        shoeRepository.save(shoe45);

        // Puma
        image1 = loadImage("images/PRODUCS/PUMA/Ultra-5-Match_1.jpg");
        image2 = loadImage("images/PRODUCS/PUMA/Ultra-5-Match_2.jpg");
        image3 = loadImage("images/PRODUCS/PUMA/Ultra-5-Match_3.jpg");

        Shoe shoe60 = new Shoe();
        shoe60.setName("Puma Ultra 5 Match");
        shoe60.setDescription("null");
        shoe60.setLongDescription("null");
        shoe60.setPrice(new BigDecimal("40.00"));
        shoe60.setBrand(Shoe.Brand.PUMA);
        shoe60.setCategory(Shoe.Category.URBAN);
        shoe60.setImage1(image1);
        shoe60.setImage2(image2);
        shoe60.setImage3(image3);
        shoeRepository.save(shoe60);

        // Nike
        image1 = loadImage("images/PRODUCS/NIKE/NKwhite_1.jpg");
        image2 = loadImage("images/PRODUCS/NIKE/NKwhite_2.jpg");
        image3 = loadImage("images/PRODUCS/NIKE/NKwhite_3.jpg");

        Shoe shoe15 = new Shoe();
        shoe15.setName("Nike AirForce");
        shoe15.setDescription("Nike AirForce 1");
        shoe15.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe15.setPrice(new BigDecimal("150.00"));
        shoe15.setBrand(Shoe.Brand.NIKE);
        shoe15.setCategory(Shoe.Category.URBAN);
        shoe15.setImage1(image1);
        shoe15.setImage2(image2);
        shoe15.setImage3(image3);
        shoeRepository.save(shoe15);

        // New Balance
        image1 = loadImage("images/PRODUCS/NEW_BALANCE/NBC10_1.jpg");
        image2 = loadImage("images/PRODUCS/NEW_BALANCE/NBC10_2.jpg");
        image3 = loadImage("images/PRODUCS/NEW_BALANCE/NBC10_3.jpg");

        Shoe shoe30 = new Shoe();
        shoe30.setName("Nike AirForce");
        shoe30.setDescription("Nike AirForce 1");
        shoe30.setLongDescription(
                "The Nike Air Max 270 delivers visible air under every step. Updated for modern comfort, it nods to the original 1991 Air Max 180 with its exaggerated tongue top and heritage tongue logo. ");
        shoe30.setPrice(new BigDecimal("150.00"));
        shoe30.setBrand(Shoe.Brand.NIKE);
        shoe30.setCategory(Shoe.Category.URBAN);
        shoe30.setImage1(image1);
        shoe30.setImage2(image2);
        shoe30.setImage3(image3);
        shoeRepository.save(shoe30);

        // create sisez dinamicamente para cada producto.
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