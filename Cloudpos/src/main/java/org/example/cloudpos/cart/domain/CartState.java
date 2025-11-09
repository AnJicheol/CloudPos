package org.example.cloudpos.cart.domain;
/**

 * 장바구니의 상태를 정의하는 유한 상태(enum)입니다.
 *
 * <p><b>상태 설명</b></p>
 * <ul>
 * <li>{@code EMPTY} — 장바구니가 비어 있으며, 아직 상품이 추가되지 않은 초기 상태</li>
 * <li>{@code IN_PROGRESS} — 하나 이상의 상품이 담겨 있으며, 결제가 시작되지 않은 상태</li>
 * <li>{@code CHECKOUT_PENDING} — 결제 프로세스가 시작되어 결제 대기 중인 상태</li>
 * <li>{@code CLOSED} — 결제가 성공적으로 완료되었거나 만료로 인해 종료된 종단 상태</li>
 * </ul>
 *
 * <p><b>비고</b><br>
 * 상태 전이는 {@link org.example.cloudpos.cart.fsm.CartEvent}에 의해 결정되며,
 * {@link org.example.cloudpos.cart.service.CartService} 내의 FSM(유한 상태 머신) 로직에서 처리됩니다.
 * </p>

 */

public enum CartState {
    EMPTY,
    IN_PROGRESS,
    CHECKOUT_PENDING,
    CLOSED
}