package org.example.cloudpos.cart.service;

import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.dto.CreateCartResponse;

import java.util.List;

/**
 * 장바구니 도메인의 핵심 기능을 정의하는 서비스 인터페이스.
 *
 * 구현체는 Redis, RDB 등 인프라에 의존하지만
 * 컨트롤러·다른 도메인은 이 인터페이스만 의존하도록 한다.
 */
public interface CartService {

    CreateCartResponse createCart();

    CartState getState(String cartId);

    void addFirstTime(String cartId, String productId);

    int changeQuantity(String cartId, String productId, int delta);

    void removeItem(String cartId, String productId);

    void clear(String cartId);

    List<CartItemDto> getAll(String cartId);

    int getQuantity(String cartId, String productId);
}
