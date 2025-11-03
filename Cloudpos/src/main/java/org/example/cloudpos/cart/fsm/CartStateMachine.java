package org.example.cloudpos.cart.fsm;

import org.example.cloudpos.cart.domain.CartState;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class CartStateMachine {

    private static final Map<CartState, Map<CartEvent, CartState>> T = new EnumMap<>(CartState.class);

    static{
        T.put(CartState.EMPTY, Map.of(
                CartEvent.ADD_ITEM, CartState.IN_PROGRESS,
                CartEvent.EXPIRE, CartState.CLOSED
        ));

        T.put(CartState.IN_PROGRESS, Map.of(
                CartEvent.ADD_ITEM, CartState.IN_PROGRESS,
                CartEvent.REMOVE_ITEM, CartState.IN_PROGRESS,
                CartEvent.CHECKOUT, CartState.CHECKOUT_PENDING,
                CartEvent.EXPIRE, CartState.CLOSED
        ));

        T.put(CartState.CHECKOUT_PENDING, Map.of(
                CartEvent.PAYMENT_SUCCESS, CartState.CLOSED,
                CartEvent.EXPIRE,          CartState.CLOSED));

        T.put(CartState.CLOSED, Map.of());
    }

    private CartStateMachine() {
        throw new AssertionError("Utility class");
    }

    public static Optional<CartState> next(CartState current, CartEvent event){
        return Optional.ofNullable(
                T.getOrDefault(current, Map.of()).get(event)
        );
    }
}
