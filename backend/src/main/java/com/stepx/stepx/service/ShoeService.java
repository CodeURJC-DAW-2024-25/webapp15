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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.mapper.ShoeMapper;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.mapper.*;
import com.stepx.stepx.model.Shoe.Brand;
import com.stepx.stepx.model.Shoe.Category;
import com.stepx.stepx.repository.ShoeRepository;


@Service
public class ShoeService {
   

    private final ShoeRepository shoeRepository;

    
    @Autowired
    private ShoeMapper shoeMapper;
    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    public ShoeService(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

    // to get first 9 shoes without filter aplicated
    public Page<ShoeDTO> getNineShoes(int page) {
        int pageSize=9;
        Page<Shoe> shoePage=shoeRepository.findNineShoes(PageRequest.of(page, pageSize));
        return shoePage.map(shoeMapper::toDTO);
    }

    //obtain the first 9 of a brand
    public Page<ShoeDTO>getShoesByBrand(int page,String brand){ 
        int pageSize=9;
        Page<Shoe> shoePage=shoeRepository.findFirst9ByBrand(brand,PageRequest.of(page, pageSize));
        return shoePage.map(shoeMapper::toDTO);
    }

    //obtain the next 3 shoes with brand filter aplicated
    public Page<ShoeDTO> getShoesPaginatedByBrand(int currentPage, String selectedBrand) {
        int pageSize = 3;
        Page<Shoe>paginatedShoe=shoeRepository.findByBrand(selectedBrand, PageRequest.of(currentPage, pageSize));
        return paginatedShoe.map(shoeMapper::toDTO);
    }    

    //obtain the first 9 of category
    public Page<ShoeDTO> getShoesByCategory(int page,String category){  
        int pageSize=9;
        Page<Shoe> shoePage=shoeRepository.findFirst9ByCategory(category,PageRequest.of(page,pageSize));
        return shoePage.map(shoeMapper::toDTO);
    }

    //obtain the next 3 shoes with category filter aplicated
    public Page<ShoeDTO> getShoesPaginatedByCategory(int currentPage, String selectedCategory){
        int pageSize=3;
        Page<Shoe> paginatedShoe=shoeRepository.findByCategory(selectedCategory,PageRequest.of(currentPage, pageSize));
        return paginatedShoe.map(shoeMapper::toDTO);
    }


    public Optional<ShoeDTO> getShoeById(Long id) {
        Shoe shoe= shoeRepository.findById(id).orElse(null);
        
        if(shoe==null){
            return Optional.empty();
        }
        ShoeDTO shoeDTO = shoeMapper.toDTO(shoe);
        if (shoeDTO == null) {
            return Optional.empty();
        }else{
            return Optional.of(shoeDTO);
        }
    }
    
    
    public Shoe saveShoe(Shoe shoe) {
        return shoeRepository.save(shoe);
    }

    @Transactional
    public void updateShoe(Long id, String name, String shortDescription, String longDescription,
            BigDecimal price, MultipartFile image1, MultipartFile image2,
            MultipartFile image3, String brand, String category)
            throws IOException, SQLException {

        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shoe not found"));

        shoe.setName(name);
        shoe.setDescription(shortDescription);
        shoe.setLongDescription(longDescription);
        shoe.setPrice(price);
        shoe.setBrand(Brand.valueOf(brand));
        shoe.setCategory(Category.valueOf(category));

        if (image1 != null && !image1.isEmpty()) {
            shoe.setImage1(new SerialBlob(image1.getBytes()));
        }
        if (image2 != null && !image2.isEmpty()) {
            shoe.setImage2(new SerialBlob(image2.getBytes()));
        }
        if (image3 != null && !image3.isEmpty()) {
            shoe.setImage3(new SerialBlob(image3.getBytes()));
        }

        shoeRepository.save(shoe);
    }

    
    public Long saveAndReturnId(ShoeDTO dto) {
        Shoe shoe = shoeMapper.toDomain(dto);
        Shoe saved = shoeRepository.save(shoe);
        return saved.getId();
    }

    @Transactional
    public Long createShoeWithImagesAndDefaultStock(
            String name,
            String shortDescription,
            String longDescription,
            BigDecimal price,
            String brand,
            String category,
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3) throws IOException, SQLException {

        Shoe shoe = new Shoe();
        shoe.setName(name);
        shoe.setDescription(shortDescription);
        shoe.setLongDescription(longDescription);
        shoe.setPrice(price);
        shoe.setBrand(Shoe.Brand.valueOf(brand));
        shoe.setCategory(Shoe.Category.valueOf(category));

        if (image1 != null && !image1.isEmpty()) {
            shoe.setImage1(new SerialBlob(image1.getBytes()));
        }
        if (image2 != null && !image2.isEmpty()) {
            shoe.setImage2(new SerialBlob(image2.getBytes()));
        }
        if (image3 != null && !image3.isEmpty()) {
            shoe.setImage3(new SerialBlob(image3.getBytes()));
        }

        Shoe saved = shoeRepository.save(shoe);

        List<String> sizes = List.of("S", "M", "L", "XL");
        List<ShoeSizeStockDTO> stockDTOs = sizes.stream()
                .map(size -> new ShoeSizeStockDTO(null, saved.getId(), size, 10))
                .toList();

        shoeSizeStockService.saveStockList(stockDTOs);

        return saved.getId(); // In case you need the id of the new created shoe
    }

    public void deleteShoe(Long id) {
        shoeRepository.deleteById(id);
    }

    public Page<ShoeDTO> getShoesPaginated(int currentPage) {
        int pagesize=3;
        Page<Shoe> shoes = shoeRepository.findAll(PageRequest.of(currentPage, pagesize));
        return shoes.map(shoeMapper::toDTO);
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

    public String convertBlobToBase64(Blob blob) {
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

    public Blob convertBase64ToBlob(String image64) throws SQLException{
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

    public Brand StringToBrand(String brandString){
      Brand brand = Brand.valueOf(brandString.trim().toUpperCase());
      return brand;
    }

    public Category StringToCategory(String categoryString){
        Category category = Category.valueOf(categoryString.trim().toUpperCase());
        return category;
    }

    public String categoryToString(Category category){
        return category.name().toUpperCase();
    }

    public BigDecimal getPricefromShoe(ShoeDTO shoeDtO){
        Shoe shoe = shoeMapper.toDomain(shoeDtO);
        return shoe.getPrice();
    }

    
}
