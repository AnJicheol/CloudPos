package org.example.cloudpos.order.service;

import org.example.cloudpos.order.domain.PaymentMethod;
import org.example.cloudpos.order.dto.OrderResponse;


/**
 * 주문 생성 및 결제 시작 흐름을 담당하는 서비스.
 *
 * <p>장바구니 상태를 결제 진행 상태로 전환하고,
 * 해당 장바구니를 기반으로 주문을 생성한 뒤 주문 식별자를 반환한다.</p>
 */
public interface OrderService {


    /**
     * 장바구니 ID로 결제를 시작하고 주문을 생성한다.
     *
     * @param cartId 결제를 시작할 장바구니 ID
     * @return 생성된 주문의 문자열 주문 ID(예: ULID)를 담은 응답 DTO
     */
    OrderResponse startPayment(String cartId, PaymentMethod paymentMethod);
}
