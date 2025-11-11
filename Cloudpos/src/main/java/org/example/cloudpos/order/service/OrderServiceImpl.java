package org.example.cloudpos.order.service;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.order.api.CartStateHandlerApi;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.domain.OrderItem;
import org.example.cloudpos.order.repository.OrderItemRepository;
import org.example.cloudpos.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final CartStateHandlerApi cartStateHandlerApi;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Order startPayment(String cartId) {

        Order order = new Order(
                LocalDateTime.now(),
                cartId,
                UlidCreator.getUlid().toString()
        );

        List<OrderItem> orderItems = new ArrayList<>();
        int total = 0;

        for (CartItemDto ci : cartStateHandlerApi.statePayment(cartId)) {

            total += ci.getProduct().price() * ci.getQuantity();

            orderItems.add(new OrderItem(
                    order,
                    ci.getProduct().productId(),
                    ci.getQuantity(),
                    ci.getProduct().price()
            ));
        }

        order.applyTotalAmount(total);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        return order;
    }

    @Transactional()
    public Order getOrderById(String orderId){
        return orderRepository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException());
    }

}

