package com.stepx.stepx.controller.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stepx.stepx.dto.ShoeSizeStockDTO;
import com.stepx.stepx.service.ShoeSizeStockService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/ShoeSizeStocks")
public class ShoeSizetockRestController {
    private final ShoeSizeStockService shoeSizeStockService;
    
    public ShoeSizetockRestController(ShoeSizeStockService shoeSizeStockService) {
        this.shoeSizeStockService = shoeSizeStockService;
    }

    //get shoesstock by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ShoeSizeStockDTO shoeSizeStock = shoeSizeStockService.findById(id);
        if (shoeSizeStock==null) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "ShoeSizeStock with ID " + id + " not found"));
        }
        return ResponseEntity.ok(shoeSizeStock);
    }

    //get all shoesstock
    @GetMapping
    public ResponseEntity<?> getAll(){
        List<ShoeSizeStockDTO> shoeSizeStocks = shoeSizeStockService.findAll();
        if (shoeSizeStocks.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("error", "No ShoeSizeStocks found"));
        }
        return ResponseEntity.ok(shoeSizeStocks);
    }

    //get stocks by shoe id
    @GetMapping("/Shoe/{id}")
    public ResponseEntity<?> getByShoeId(@PathVariable Long id) {
        List<ShoeSizeStockDTO> shoeSizeStocks = shoeSizeStockService.findByShoeId(id);
        if (shoeSizeStocks.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("error", "No ShoeSizeStocks found for shoe with ID " + id));
        }
        return ResponseEntity.ok(shoeSizeStocks);
    }   
    
    //add a new stock (for this we need a shoe but in this case we will do it whitout it)
    @PostMapping
    public ResponseEntity<?> createStock(@RequestBody ShoeSizeStockDTO shoeSizeStockDTO) {
        ShoeSizeStockDTO shoeSizeStock = shoeSizeStockService.save(shoeSizeStockDTO);
        return ResponseEntity.created(null).body(shoeSizeStock);
    }

    //update a stock of a shoe
    @PutMapping("/{size}/Shoe/{shoeId}")
    public ResponseEntity<?> updateStockofShoe(@PathVariable String size, @PathVariable Long shoeId, @RequestBody ShoeSizeStockDTO dto) {
        Optional<ShoeSizeStockDTO> shoeSizeStockDTO= shoeSizeStockService.updateSigleStock(size,shoeId,dto);
        if (shoeSizeStockDTO.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("error", "No ShoeSizeStock found"));
        }
        return ResponseEntity.ok(shoeSizeStockDTO.get());
    }

    //delete stock of shoe
    @DeleteMapping("/{size}/Shoe/{shoeid}")
    public ResponseEntity<?> deleteStockofShoe(@PathVariable String size,@PathVariable Long shoeid){
        Optional <ShoeSizeStockDTO> shoeSizeSOptional=shoeSizeStockService.deleteStock(size,shoeid);
        if (shoeSizeSOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("error", "No ShoeSizeStock found"));
        }
        return shoeSizeSOptional.map(ResponseEntity::ok)
            .orElseGet(()-> ResponseEntity.notFound().build());
    }
}
