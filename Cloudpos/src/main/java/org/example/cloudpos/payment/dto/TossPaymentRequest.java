package org.example.cloudpos.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h2>TossPaymentRequest</h2>
 *
 * 프론트엔드에서 결제 완료 후 백엔드로 전달되는
 * 토스페이먼츠 결제 승인 요청 DTO 입니다.
 *
 * <p>토스 결제 위젯에서 결제가 완료되면, 프론트에서는
 * paymentKey, orderId, amount 값을 백엔드로 전달해야 합니다.</p>
 *
 * 예시 요청:
 * <pre>
 * {
 *   "paymentKey": "pay_20251031235959xYzAbC",
 *   "orderId": "12345",
 *   "amount": 30000
 * }
 * </pre>
 */

@Getter
@NoArgsConstructor
@ToString
public class TossPaymentRequest {

    //토스에서 발급한 결제 고유 키
    private String paymentKey;

    //내부 시스템에서 관리하는 주문 ID
    private String orderId;

    //결제 총금액
    private  long amount;
}
