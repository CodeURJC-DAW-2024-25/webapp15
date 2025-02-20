package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.stepx.stepx.model.Product;

@Service
public class CartService {

    private final ProductsService productsService;

    private ConcurrentMap<Long, Product> cart = new ConcurrentHashMap<>();// allows to use the map in a concurrent way

    public CartService(ProductsService productsService) {

        this.productsService = productsService;

    }

    // Agregar un producto al carrito
    public String addProductToCart(Long productId, Product product) {
        if (product == null) {
            return "Error: El producto no existe.";
        }
        cart.put(productId, product);
        // poner el carrito en el
        return "Producto agregado al carrito.";
    }

    // Eliminar un producto del carrito
    public String removeProductFromCart(Long productId) {
        if (!cart.containsKey(productId)) {
            return "Error: El producto no está en el carrito.";
        }
        cart.remove(productId);
        return "Producto eliminado del carrito.";
    }

    // Obtener el contenido del carrito
    public Map<Long, Product> getCartContents() {
        return cart;
    }

    // Vaciar el carrito
    public void clearCart() {
        cart.clear();
    }

    
}
