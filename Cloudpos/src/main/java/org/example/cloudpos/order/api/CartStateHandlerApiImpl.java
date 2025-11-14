package org.example.cloudpos.order.api;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.application.CartCheckoutUseCase;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.order.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



/**
 * 주문 ID를 장바구니 ID로 매핑하고,
 * 장바구니에 상태 변경 요청을 위임하는 구현체.
 *
 * <p>Order 모듈 입장에서는 주문의 문자열 ID만 알고 있어도
 * 장바구니 모듈에 "열어라/닫아라/체크아웃 시작" 신호를 보낼 수 있도록
 * 중간에서 매핑과 위임을 담당한다.</p>
 *
 * <p>장바구니 상태 관리 로직은 {@link CartCheckoutUseCase} 가 속한
 * 장바구니 도메인의 책임이며, 이 클래스는 그 도메인으로의
 * 진입점을 감싸는 어댑터 역할만 수행한다.</p>
 */
@Component
@RequiredArgsConstructor
public class CartStateHandlerApiImpl implements CartStateHandlerApi {
    private final OrderRepository orderRepository;
    private final CartCheckoutUseCase cartCheckoutUseCase;


    @Override
    @Transactional(readOnly = true)
    public void stateOpen(String orderId) {
        cartCheckoutUseCase.cancelCheckout(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public void stateClose(String orderId) {
        cartCheckoutUseCase.paymentSuccess(orderRepository.findCartIdByOrderId(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemDto> statePayment(String orderId) {
        return cartCheckoutUseCase.beginCheckout(orderRepository.findCartIdByOrderId(orderId));
    }
}
