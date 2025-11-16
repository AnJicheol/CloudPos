package org.example.cloudpos.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Toss Payments API", description = "토스페이먼츠 결제 승인 및 취소 API")
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    /**
     * 결제 승인 API
     */
    @Operation(
            summary = "토스 결제 승인",
            description = """
                    프론트엔드에서 받은 `paymentKey`, `orderId`, `amount` 정보를 기반으로
                    Toss 서버에 결제 승인을 요청합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 승인 성공",
                    content = @Content(schema = @Schema(implementation = TossPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "주문 또는 결제 정보 없음",
                    content = @Content)
    })
    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponse> confirmPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Toss 결제 승인 요청 정보 (paymentKey, orderId, amount)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TossPaymentRequest.class))
            )
            @RequestBody TossPaymentRequest request
    ) {
        log.info("[POST] /payments/toss/confirm 호출됨 orderId={}, paymentKey={}",
                request.getOrderId(), request.getPaymentKey());

        TossPaymentResponse response = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 취소 API
     */
    @Operation(
            summary = "토스 결제 취소",
            description = """
                    결제 고유키(paymentKey)와 주문 ID를 기반으로 결제를 취소합니다.
                    `cancelReason` 파라미터는 선택적으로 제공 가능합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 취소 성공",
                    content = @Content(schema = @Schema(implementation = TossPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "결제 정보 없음",
                    content = @Content)
    })
    @PostMapping("/cancel/{paymentKey}")
    public ResponseEntity<TossPaymentResponse> cancelPayment(
            @Parameter(description = "취소할 결제의 고유 paymentKey", required = true)
            @PathVariable String paymentKey,

            @Parameter(description = "취소할 주문의 ID", required = true)
            @RequestParam String orderId,

            @Parameter(description = "취소 사유 (기본값: 사용자 요청에 의한 취소)", required = false)
            @RequestParam(defaultValue = "사용자 요청에 의한 취소") String cancelReason
    ) {
        log.info("[POST] /payments/toss/cancel 호출됨 orderId={}, paymentKey={}, reason={}",
                orderId, paymentKey, cancelReason);

        TossPaymentResponse response = tossPaymentService.cancelPayment(paymentKey, cancelReason, orderId);
        return ResponseEntity.ok(response);
    }
}