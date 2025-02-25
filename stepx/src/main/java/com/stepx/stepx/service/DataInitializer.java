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

        // create samples NIKE
        Blob image1 = loadImage("images/PRODUCS/NIKE/NK270_1.jpg");
        Blob image2 = loadImage("images/PRODUCS/NIKE/NK270_2.jpg");
        Blob image3 = loadImage("images/PRODUCS/NIKE/NK270_3.jpg");

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

        // second
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

        // thirt
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

        // four
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

        // five
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

        // six
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

        // seven
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

        // 8

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

        // 9

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

        // 10

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

        // 11

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

        // 12
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

        // 13
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

        // 14
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

        // 15
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

        // NEW-BALANCE

        // 16
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

        // 17
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

        // 18
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

        // 19
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

        // 20
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

        // 21
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

        // 22
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

        // 23
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

        // 24
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

        // 25
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

        // 26
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

        // 27
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

        // 28
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

        // 29
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

        // 30
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

        // Adidas

        Shoe shoe31 = new Shoe();
        shoe31.setName("Adidas");

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