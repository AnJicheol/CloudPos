package org.example.cloudpos.order.service;


import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.order.api.CartStateHandlerApi;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.domain.OrderItem;
import org.example.cloudpos.order.domain.PaymentMethod;
import org.example.cloudpos.order.dto.OrderResponse;
import org.example.cloudpos.order.repository.OrderItemRepository;
import org.example.cloudpos.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



/**
 * 결제 시작 시 주문 생성 및 주문 상품 저장을 처리하는 구현체.
 *
 * <p>
 * CartStateHandlerApi 를 통해 결제 가능한 장바구니를 조회하고,
 * 해당 정보를 기반으로 Order / OrderItem 을 생성 및 저장한 뒤
 * 외부에서 사용할 주문 식별자를 반환한다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final CartStateHandlerApi cartStateHandlerApi;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;


    /**
     * 장바구니를 기반으로 주문을 생성하고 결제를 시작한다.
     *
     * <p>장바구니의 상품 목록을 조회하여 주문 및 주문 상품을 생성하고,
     * 총 결제 금액을 계산한 뒤 저장한다. 생성된 주문의 문자열 주문 ID
     * (예: ULID)를 응답으로 반환한다.</p>
     *
     * @param cartId 결제를 시작할 장바구니 ID
     * @return 생성된 주문의 문자열 주문 ID를 담은 {@link OrderResponse}
     */
    @Transactional
    public OrderResponse startPayment(String cartId, PaymentMethod paymentMethod) {

        Order order = new Order(
                UlidCreator.getUlid().toString(),
                cartId,
                LocalDateTime.now(),
                paymentMethod
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

        return new OrderResponse(order.getOrderId());
    }
}
