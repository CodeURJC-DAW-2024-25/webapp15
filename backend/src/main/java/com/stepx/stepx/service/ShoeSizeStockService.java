package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stepx.stepx.dto.BasicShoeSizeStockDTO;
import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.mapper.ShoeSizeStockMapper;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.ShoeSizeStockRepository;

import jakarta.transaction.Transactional;

@Service
public class ShoeSizeStockService {

    @Autowired
    private ShoeSizeStockRepository shoeSizeStockRepository;

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private ShoeSizeStockMapper shoeSizeStockMapper;

    public List<ShoeSizeStock> getAllStock() {
        return shoeSizeStockRepository.findAll();
    }

    public Optional<ShoeSizeStock> getStockById(Long id) {
        return shoeSizeStockRepository.findById(id);
    }

    public ShoeSizeStock saveStock(ShoeSizeStock stock) {
        return shoeSizeStockRepository.save(stock);
    }

     public void saveStockList(List<ShoeSizeStockDTO> dtos) {
        List<ShoeSizeStock> entities = dtos.stream()
            .map(shoeSizeStockMapper::toDomain)
            .toList();
            shoeSizeStockRepository.saveAll(entities);
    }

    public void deleteStock(Long id) {
        shoeSizeStockRepository.deleteById(id);
    }

    public Optional<Integer> getStockByShoeAndSize(Long shoeId, String size) {
        return shoeSizeStockRepository.findByShoeAndSize(shoeId, size);
    }

    public Map<String, Integer> getAllStocksForShoes(List<Long> shoeIds, List<String> sizes) {
        List<ShoeSizeStock> stocks = shoeSizeStockRepository.findByShoeIdsAndSizes(shoeIds, sizes);
        return stocks.stream().collect(Collectors.toMap(
                stock -> stock.getShoe().getId() + "_" + stock.getSize(),
                ShoeSizeStock::getStock,
                (existing, replacement) -> existing));
    }

    @Transactional
    public void updateStock(Map<Long, Map<String, Integer>> stockUpdates) {
        for (Map.Entry<Long, Map<String, Integer>> entry : stockUpdates.entrySet()) {
            Long shoeId = entry.getKey();
            for (Map.Entry<String, Integer> sizeEntry : entry.getValue().entrySet()) {
                String size = sizeEntry.getKey();
                int quantity = sizeEntry.getValue();
                shoeSizeStockRepository.reduceStock(shoeId, size, quantity);
            }
        }
    }

    /*public List<BasicShoeSizeStockDTO> convertToShoeSizeStockDTO(List<ShoeSizeStock> sizeStocks) {
        List<BasicShoeSizeStockDTO> sizeStocksDTOs = new ArrayList<>();
    
        for (ShoeSizeStock sizeStock : sizeStocks) {
            BasicShoeSizeStockDTO sizeStockDTO = new BasicShoeSizeStockDTO(
                sizeStock.getId(), 
                sizeStock.getShoe().getId(), 
                sizeStock.getSize(), 
                sizeStock.getStock()
            );
            sizeStocksDTOs.add(sizeStockDTO);
        }
    
        return sizeStocksDTOs;
    }*/

    public List<ShoeSizeStock> convertToShoeSizeStock(List<ShoeSizeStockDTO> sizeStocksDTOs) {
        List<ShoeSizeStock> sizeStocks = new ArrayList<>();
        sizeStocks=shoeSizeStockMapper.toDomains(sizeStocksDTOs);
        /*for (BasicShoeSizeStockDTO sizeStockDTO : sizeStocksDTOs) {
            Shoe shoe = new Shoe(); // Debes recuperar la instancia de Shoe por su ID
            shoe.setId(sizeStockDTO.shoeId());
            ShoeSizeStock sizeStock = new ShoeSizeStock();
            Optional<Shoe> shoeOpt = shoeRepository.findById(sizeStockDTO.shoeId());
            if (shoeOpt.isPresent()) {
                sizeStock.setShoe(shoeOpt.get());
            } else {
                throw new IllegalArgumentException("Shoe with ID " + sizeStockDTO.shoeId() + " not found");
            }
            sizeStock.setSize(sizeStockDTO.size());
            sizeStock.setStock(sizeStockDTO.stock());
            sizeStocks.add(sizeStock);
        }*/

        return sizeStocks;
    }

}
