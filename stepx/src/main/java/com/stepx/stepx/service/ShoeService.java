package com.stepx.stepx.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Shoe.Brand;
import com.stepx.stepx.repository.ShoeRepository;

@Service
public class ShoeService {

    private final ShoeRepository shoeRepository;

    // Inyección de dependencias mediante constructor
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

    // Obtener un zapato por ID
    public Optional<Shoe> getShoeById(Long id) {
        return shoeRepository.findById(id);
    }
    
    // Guardar o actualizar un zapato
    public Shoe saveShoe(Shoe shoe) {
        return shoeRepository.save(shoe);
    }

    // Eliminar un zapato por ID
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
}
