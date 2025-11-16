package org.example.cloudpos.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <h2>TossPaymentResponse</h2>
 *
 * 토스페이먼츠 결제 승인 API 응답을 매핑하는 DTO입니다.
 *
 * <p>토스 서버로부터 받은 JSON 응답을 매핑하며,
 * 결제 승인 성공 시 결제 상태, 금액, 승인 시간 등의 정보를 담습니다.</p>
 *
 * <p>응답 예시:</p>
 * <pre>
 * {
 *   "version": "2022-11-16",
 *   "paymentKey": "pay_2025110312345678abcd",
 *   "orderId": "order_001",
 *   "orderName": "삼성 모니터 27인치",
 *   "status": "DONE",
 *   "method": "카드",
 *   "totalAmount": 30000,
 *   "approvedAt": "2025-11-03T12:00:00+09:00",
 *   "easyPay": {
 *     "provider": "토스페이",
 *     "amount": 30000
 *   },
 *   "receipt": {
 *     "url": "https://sandbox.tosspayments.com/receipt/pay_..."
 *   }
 * }
 * </pre>

 */

@Getter
@NoArgsConstructor
@ToString
public class TossPaymentResponse {

    private String version;       // 응답 버전
    private String paymentKey;    // 결제 키
    private String orderId;       // 주문 ID
    private String orderName;     // 주문명
    private String status;        // 결제 상태
    private String method;        // 결제수단
    private long totalAmount;     // 결제 금액
    private String approvedAt;    // 결제 승인 시각
    private EasyPay easyPay;      // 간편결제 정보 (토스페이, 네이버페이 등)
    private Receipt receipt;      // 영수증 URL 정보

    @Getter
    @NoArgsConstructor
    @ToString
    public static class EasyPay {
        private String provider;  // 예: 토스페이, 네이버페이 등
        private long amount;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Receipt {
        private String url;       // 영수증 조회 URL
    }
}
