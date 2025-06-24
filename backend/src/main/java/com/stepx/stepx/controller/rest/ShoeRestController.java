package com.stepx.stepx.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.mapper.ShoeMapper;
import com.stepx.stepx.service.ShoeService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1/Shoes")
public class ShoeRestController {

    @Autowired
    private ShoeService shoeService;

    @Autowired ShoeMapper shoeMapper;
     
    //get all shoes
    @GetMapping("/All")
    public ResponseEntity<?> getAllShoes() {
        List<ShoeDTO> shoes = shoeService.findAll();
        if (shoes.isEmpty()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "No shoes found"));
        }
        return ResponseEntity.ok(shoes);
    }

    //get paged shoes
    @GetMapping
    public ResponseEntity<?> getPagedShoes(@RequestParam int page, @RequestParam int size) {
        Page<ShoeDTO> shoes = shoeService.getPagedShoes(page,size);
        if(shoes.isEmpty()){
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "No shoes found"));
        }
        return ResponseEntity.ok(shoes);
    }

    //get one shoe
    @GetMapping("/{shoeId}")
    public ResponseEntity<?> getOneShoe(@PathVariable Long shoeId) {
        Optional<ShoeDTO> shoeDTO = shoeService.getShoeById(shoeId);
        if (!shoeDTO.isPresent()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "Shoe not found"));
        }
        
        return ResponseEntity.ok(shoeDTO.get());
    }

    //get image from shoe
    @GetMapping("/{shoeId}/image/{imageNumber}")
    public ResponseEntity<?> getImage(@PathVariable Long shoeId, @PathVariable int imageNumber) throws SQLException {
        Resource shoeImage=shoeService.getImage(shoeId, imageNumber);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
            .body(shoeImage);
    }
    
    //get by category
    @GetMapping("/category")
    public ResponseEntity<?> getByCategory(
        @RequestParam String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "9") int size) {

            Page<ShoeDTO>pageObj=shoeService.getShoesByCategory(page, category);
            return ResponseEntity.ok(pageObj);
    }

    //get by brand
    @GetMapping("/brand")
    public ResponseEntity<?> getByBrand(
        @RequestParam String brand,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "9") int size) {
            
            Page<ShoeDTO>pageObj=shoeService.getShoesByBrand(page, brand);
            return ResponseEntity.ok(pageObj);
    }
    

    //create a shoe
    @PostMapping()
    public ResponseEntity<?> createShoe(@RequestBody ShoeDTO shoeDTO) throws IOException, SQLException {
        ShoeDTO saved= shoeService.saveShoe(shoeDTO);
        URI location=ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    //create image for shoe
    @PostMapping("/{shoeId}/image/{imageNumber}")
    public ResponseEntity<?> loadImage(@PathVariable Long shoeId, @PathVariable int imageNumber,@RequestParam("file") MultipartFile file) throws IOException {
        
        URI location=ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        shoeService.storeImage(shoeId,imageNumber,file.getInputStream(),file.getSize());

        return ResponseEntity.created(location).build();
    }
    
    //update shoe
    @PutMapping("/{shoeId}")
    public ResponseEntity<?> updateShoe(@PathVariable Long shoeId, @RequestBody ShoeDTO shoeDTO) {
        
        Optional<ShoeDTO> shoeOptional=shoeService.updateAllShoe(shoeId,shoeDTO);
        if (!shoeOptional.isPresent()) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "Shoe not found"));
        }

        return ResponseEntity.ok(shoeOptional.get());
    }

    @PutMapping("/{shoeId}/image/{number}")
    public ResponseEntity<?> updateImage(@PathVariable Long shoeId, @PathVariable int number, @RequestParam("file") MultipartFile file) throws IOException {
        shoeService.storeImage(shoeId, number, file.getInputStream(), file.getSize());
        return ResponseEntity.ok().build();
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShoe(@PathVariable Long id) {
        shoeService.deleteShoe(id);
        return ResponseEntity.noContent().build();
    }

}
 
    

