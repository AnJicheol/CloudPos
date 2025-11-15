package org.example.cloudpos.cart.fsm;

import org.example.cloudpos.cart.domain.CartState;
import java.util.EnumMap;
import java.util.Optional;

public final class CartStateMachine {

    private static final EnumMap<CartState, EnumMap<CartEvent, CartState>> TRANSITIONS = new EnumMap<>(CartState.class);

    static {
        // EMPTY
        EnumMap<CartEvent, CartState> empty = new EnumMap<>(CartEvent.class);
        empty.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.EMPTY, empty);

        // IN_PROGRESS
        EnumMap<CartEvent, CartState> inProgress = new EnumMap<>(CartEvent.class);
        inProgress.put(CartEvent.ADD_ITEM, CartState.IN_PROGRESS);
        inProgress.put(CartEvent.REMOVE_ITEM, CartState.IN_PROGRESS);
        inProgress.put(CartEvent.CHECKOUT, CartState.CHECKOUT_PENDING);
        TRANSITIONS.put(CartState.IN_PROGRESS, inProgress);

        // CHECKOUT_PENDING
        EnumMap<CartEvent, CartState> checkout = new EnumMap<>(CartEvent.class);
        checkout.put(CartEvent.PAYMENT_SUCCESS, CartState.CLOSED);
        checkout.put(CartEvent.CANCEL, CartState.IN_PROGRESS);
        TRANSITIONS.put(CartState.CHECKOUT_PENDING, checkout);

        // CLOSED (종단)
        TRANSITIONS.put(CartState.CLOSED, new EnumMap<>(CartEvent.class));
    }

    private CartStateMachine() {}

    public static Optional<CartState> next(CartState current, CartEvent event) {
        EnumMap<CartEvent, CartState> map = TRANSITIONS.get(current);
        if (map == null) return Optional.empty();
        return Optional.ofNullable(map.get(event));
    }
}
