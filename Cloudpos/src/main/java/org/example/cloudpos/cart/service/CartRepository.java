package org.example.cloudpos.cart.service;

public interface CartRepository {

    boolean createCart(Long cartId);

    boolean deleteCart(Long cartId);

    boolean addItem(Long cartId, Long productId);
}
