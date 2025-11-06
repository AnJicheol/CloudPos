package org.example.cloudpos.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;



/**
 * <h2>PaymentRequest</h2>
 *
 * 결제 요청 시 클라이언트(또는 주문 모듈)에서 전달되는 DTO입니다.
 *
 * orderId만 전달받고, 금액/상품정보 등은 주문 서비스에서 조회합니다.
 */

@Getter
@NoArgsConstructor
public class PaymentRequest {

    private Long orderId; //결제한 주문 ID
    private Long paymentMethodId; //결제 수단

    public PaymentRequest(Long orderId, Long paymentMethodId) {
        this.orderId = orderId;
        this.paymentMethodId = paymentMethodId;
    }


}
