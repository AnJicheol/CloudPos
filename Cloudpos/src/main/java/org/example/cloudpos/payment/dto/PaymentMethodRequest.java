package org.example.cloudpos.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <h2>PaymentMethodRequest</h2>
 *
 * 결제 수단 등록/수정 요청 DTO
 * 예:
 * {
 *   "code": "KAKAO_PAY",
 *   "name": "카카오페이"
 * }
 */

@Getter
@NoArgsConstructor
@ToString
public class PaymentMethodRequest {

    private String code; // 예: "CARD", "KAKAO_PAY"
    private String name; // 예: "카드결제", "카카오페이"

}
