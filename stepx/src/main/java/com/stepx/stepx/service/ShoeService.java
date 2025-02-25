package com.stepx.stepx.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.repository.ShoeRepository;

@Service
public class ShoeService {

    private final ShoeRepository shoeRepository;

    // Inyección de dependencias mediante constructor
    public ShoeService(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;
    }

        


    // Obtener todos los zapatos
    public List<Shoe> getAllShoes() {
        return shoeRepository.findAll();
    }

    // Obtener un zapato por ID
    public Shoe getShoeById(Long id) {
        return shoeRepository.findById(id).orElse(null);
    }

    // Guardar o actualizar un zapato
    public Shoe saveShoe(Shoe shoe) {
        return shoeRepository.save(shoe);
    }

    // Eliminar un zapato por ID
    public void deleteShoe(Long id) {
        shoeRepository.deleteById(id);
    }
}
