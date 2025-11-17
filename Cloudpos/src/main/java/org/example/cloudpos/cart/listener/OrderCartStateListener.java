package org.example.cloudpos.cart.listener;

import org.example.cloudpos.cart.dto.CartItemResponse;

import java.util.List;

/**
 * <h2>OrderCartStateListener</h2>
 *
 * 장바구니(Cart)의 상태 변화 이벤트를 감지하고
 * 주문(Order) 도메인과의 연동을 처리하는 리스너 인터페이스입니다.
 *
 * <p>장바구니가 열리거나 닫히는 시점, 결제가 발생하는 시점 등에서
 * 필요한 후속 동작(예: 주문 생성, 재고 차감, 로그 기록 등)을
 * 트리거하는 역할을 합니다.</p>
 */
public interface OrderCartStateListener {

    void onOpen(String cartId);

    void onClose(String cartId);

    List<CartItemResponse> onPayment(String CartId);

}
