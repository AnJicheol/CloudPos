package org.example.cloudpos.cart.fsm;
/**

 장바구니 상태 전이를 유발하는 이벤트를 정의한 열거형입니다.

 <p><b>이벤트 설명</b></p>
 <ul>
 <li>{@code ADD_ITEM} — 상품이 장바구니에 추가될 때 발생합니다. {@code EMPTY → IN_PROGRESS} 전이를 유도합니다.</li>
 <li>{@code REMOVE_ITEM} — 상품이 장바구니에서 제거될 때 발생합니다. {@code IN_PROGRESS} 내에서 수량 변경 혹은 비움 상태로 후처리됩니다.</li>
 <li>{@code CHECKOUT} — 사용자가 결제 프로세스를 시작할 때 발생하며, {@code IN_PROGRESS → CHECKOUT_PENDING}으로 전이합니다.</li>
 <li>{@code PAYMENT_SUCCESS} — 결제가 성공적으로 완료되었을 때 발생하며, {@code CHECKOUT_PENDING → CLOSED}로 전이합니다.</li>
 <li>{@code CANCEL} — 결제 과정이 취소되었을 때 발생하며, {@code CHECKOUT_PENDING → IN_PROGRESS}로 전이합니다.</li>
 </ul>
 <p><b>비고</b><br>

 각 이벤트는 {@link org.example.cloudpos.cart.domain.CartState}의 상태 전이 표에 따라 동작하며,

 {@link org.example.cloudpos.cart.service.CartService}에서 FSM 전이 로직으로 처리됩니다.

 </p>

 */
public enum CartEvent {
    ADD_ITEM,
    REMOVE_ITEM,
    PAYMENT_SUCCESS,
    CANCEL,
    CHECKOUT

}