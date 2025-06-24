package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

     public void saveStockList(List<ShoeSizeStockDTO> dtos,Shoe shoe) {
        List<ShoeSizeStock> entities = dtos.stream()
        .map(shoeSizeStockMapper::toDomain)
        .peek(stock -> {
            stock.setShoe(shoe);      
            shoe.addSizeStock(stock); 
        })
        .toList();
            shoeSizeStockRepository.saveAll(entities);
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
                 int rows = shoeSizeStockRepository.reduceStock(shoeId, size, quantity);

            if (rows == 0) {
                throw new IllegalStateException(
                   "Not enough stock for shoe " + shoeId + ", size " + size);
            }
            }
        }
    }

    public ShoeSizeStockDTO findById(Long id){
        Optional<ShoeSizeStock> shoeSizeStock = shoeSizeStockRepository.findById(id);
        if (shoeSizeStock.isPresent()) {
            return shoeSizeStockMapper.toDTO(shoeSizeStock.get());
        } else {
            return null;
        }
    }

    public List<ShoeSizeStockDTO> findAll() {
        List<ShoeSizeStock> shoeSizeStocks = shoeSizeStockRepository.findAll();
        return shoeSizeStockMapper.toDTOs(shoeSizeStocks);
    }

    public List<ShoeSizeStockDTO> findByShoeId(Long shoeId) {
        List<ShoeSizeStock> shoeSizeStocks = shoeSizeStockRepository.findByShoeId(shoeId);
        return shoeSizeStockMapper.toDTOs(shoeSizeStocks);
    }

    public ShoeSizeStockDTO save(ShoeSizeStockDTO shoeSizeStockDTO){
        Shoe shoe = shoeRepository.findById(shoeSizeStockDTO.shoeId()).orElse(null);
        ShoeSizeStock shoeSizeStock = new ShoeSizeStock();
        shoeSizeStock.setShoe(shoe);
        shoeSizeStock.setSize(shoeSizeStockDTO.size());
        shoeSizeStock.setStock(shoeSizeStockDTO.stock());
        if (shoe != null) {
            shoe.addSizeStock(shoeSizeStock);
        } else {
            shoeSizeStock.setShoe(null);
        }
        ShoeSizeStock saved=shoeSizeStockRepository.save(shoeSizeStock);
        return shoeSizeStockMapper.toDTO(saved);
    }

    public Optional<ShoeSizeStockDTO> updateSigleStock(String size,Long shoeId, ShoeSizeStockDTO dto){
        List<ShoeSizeStock> stockList=shoeSizeStockRepository.findByShoeIdsAndSizes(List.of(shoeId), List.of(size));
        if(!stockList.isEmpty()){
            ShoeSizeStock toUpdate=stockList.getFirst();
            toUpdate.setSize(dto.size());
            toUpdate.setStock(dto.stock());
            ShoeSizeStock updated= shoeSizeStockRepository.save(toUpdate);
            return Optional.of(shoeSizeStockMapper.toDTO(updated));
        }
        return Optional.empty();
    }

    public Optional<ShoeSizeStockDTO> deleteStock(String size,Long shoeId) {
        List<ShoeSizeStock> stockList=shoeSizeStockRepository.findByShoeIdsAndSizes(List.of(shoeId), List.of(size));
        if(!stockList.isEmpty()){
            shoeSizeStockRepository.deleteByShoeIdAndSize(shoeId,size);
            return Optional.of(shoeSizeStockMapper.toDTO(stockList.getFirst()));
        }
        return Optional.empty();
    }

    public List<ShoeSizeStockDTO> convertToShoeSizeStockDTO(List<ShoeSizeStock> sizeStocks) {
        List<ShoeSizeStockDTO> sizeStocksDTOs = new ArrayList<>();
        sizeStocksDTOs = shoeSizeStockMapper.toDTOs(sizeStocks);
        
        return sizeStocksDTOs;
    }

    public List<ShoeSizeStock> convertToShoeSizeStock(List<ShoeSizeStockDTO> sizeStocksDTOs) {
        return shoeSizeStockMapper.toDomains(sizeStocksDTOs);
    }

}
