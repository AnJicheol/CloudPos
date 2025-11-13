package org.example.cloudpos.cart.exception;


public class CartProductNotFoundException extends RuntimeException {
    public CartProductNotFoundException(String productId) {
        super("Product not found for id=" + productId);
    }
}