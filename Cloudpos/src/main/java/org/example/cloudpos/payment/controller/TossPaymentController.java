package org.example.cloudpos.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.payment.dto.TossPaymentRequest;
import org.example.cloudpos.payment.dto.TossPaymentResponse;
import org.example.cloudpos.payment.service.TossPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>TossPaymentController</h2>
 *
 * 토스페이먼츠 결제 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>프론트엔드에서 결제 완료 후 전달된 paymentKey, orderId, amount 정보를
 * TossPaymentService로 전달해 결제 승인을 수행합니다.</p>
 *
 * <p>엔드포인트 예시:</p>
 * POST /payments/toss/confirm
 *
 * 요청 예시:
 * <pre>
 * {
 *   "paymentKey": "pay_2025110312345678abcd",
 *   "orderId": "order_001",
 *   "amount": 30000
 * }
 * </pre>
 *
 * 응답 예시:
 * <pre>
 * {
 *   "paymentKey": "pay_2025110312345678abcd",
 *   "orderId": "order_001",
 *   "status": "DONE",
 *   "method": "카드",
 *   "totalAmount": 30000,
 *   "approvedAt": "2025-11-03T12:34:56+09:00"
 * }
 * </pre>
 */

@Slf4j
@RestController
@RequestMapping("/payments/toss")
@RequiredArgsConstructor
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    // 토스 결제 승인 API
    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponse> confirmPayment(@RequestBody TossPaymentRequest request) {
        log.info("[POST] /api/payments/toss/confirm 호출됨");
        TossPaymentResponse response = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }
}
