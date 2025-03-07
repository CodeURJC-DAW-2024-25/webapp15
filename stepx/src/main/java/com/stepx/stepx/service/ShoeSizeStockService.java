package com.stepx.stepx.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stepx.stepx.model.ShoeSizeStock;

import com.stepx.stepx.repository.ShoeSizeStockRepository;


@Service
public class ShoeSizeStockService {

    @Autowired
    private ShoeSizeStockRepository shoeSizeStockRepository;

    public List<ShoeSizeStock> getAllStock() {
        return shoeSizeStockRepository.findAll();
    }

    public Optional<ShoeSizeStock> getStockById(Long id) {
        return shoeSizeStockRepository.findById(id);
    }

    public ShoeSizeStock saveStock(ShoeSizeStock stock) {
        return shoeSizeStockRepository.save(stock);
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
            (existing, replacement) -> existing
        ));
    }
    

}
