package com.stepx.stepx.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Shoe.Brand;
import com.stepx.stepx.model.Shoe.Category;
import com.stepx.stepx.repository.ShoeRepository;

@Service
public class ShoeService {

    private final ShoeRepository shoeRepository;

    public ShoeService(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

    // to get first 9 shoes without filter aplicated
    public Page<Shoe> getNineShoes(int page) {
        int pageSize=9;
        return shoeRepository.findNineShoes(PageRequest.of(page, pageSize));
    }

    //obtain the first 9 of a brand
    public Page<Shoe>getShoesByBrand(int page,String brand){ 
        int pageSize=9;
        return shoeRepository.findFirst9ByBrand(brand,PageRequest.of(page, pageSize));
    }

    //obtain the next 3 shoes with brand filter aplicated
    public Page<Shoe> getShoesPaginatedByBrand(int currentPage, String selectedBrand) {
        int pageSize = 3;
        System.out.println(pageSize);
        Page<Shoe>paginatedShoe=shoeRepository.findByBrand(selectedBrand, PageRequest.of(currentPage, pageSize));
        System.out.println(paginatedShoe.getNumberOfElements());
        return shoeRepository.findByBrand(selectedBrand, PageRequest.of(currentPage, pageSize));
    }    

    //obtain the first 9 of category
    public Page<Shoe> getShoesByCategory(int page,String category){  
        int pageSize=9;
        return shoeRepository.findFirst9ByCategory(category,PageRequest.of(page,pageSize));
    }

    //obtain the next 3 shoes with category filter aplicated
    public Page<Shoe> getShoesPaginatedByCategory(int currentPage, String selectedCategory){
        int pageSize=3;
        return shoeRepository.findByCategory(selectedCategory,PageRequest.of(currentPage, pageSize));
    }


    public Optional<Shoe> getShoeById(Long id) {
        return shoeRepository.findById(id);
    }
    
    
    public Shoe saveShoe(Shoe shoe) {
        return shoeRepository.save(shoe);
    }

    
    public void deleteShoe(Long id) {
        shoeRepository.deleteById(id);
    }

    public Page<Shoe> getShoesPaginated(int currentPage) {
        int pagesize=3;
        return shoeRepository.findAll(PageRequest.of(currentPage, pagesize));
    }
    public BigDecimal getTotalEarnings() {
        return shoeRepository.sumOfAllPrices();
    }

    public long getTotalShoes() {
        // FindAll() is gonna return all the shoes saved before
        List<Shoe> shoes = shoeRepository.findAll();
        return shoes.size();
    }

   public Resource getShoeImage(Long id, int imageNumber) throws SQLException, IOException {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Shoe not found"));

        Blob imageBlob = switch (imageNumber) {
            case 1 -> shoe.getImage1();
            case 2 -> shoe.getImage2();
            case 3 -> shoe.getImage3();
            default -> throw new NoSuchElementException("Invalid image number");
        };

        if (imageBlob == null) {
            throw new NoSuchElementException("Image not found");
        }

        return new InputStreamResource(imageBlob.getBinaryStream());
    }

    public static String convertBlobToBase64(Blob blob) {
        if (blob == null) {
            return null; 
        }
        try {
            //Getting bytes from Blob and turn it in Base64
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Blob convertBase64ToBlob(String image64) throws SQLException{
        if (image64 == null) {
            return null;
        }
        try{
            byte[] decodedByte = Base64.getDecoder().decode(image64);
            Blob b = new SerialBlob(decodedByte);
            return b;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public String brandToString(Brand brand){
        return brand.name().toUpperCase();
    }

    public String categoryToString(Category category){
        return category.name().toUpperCase();
    }

    
}
