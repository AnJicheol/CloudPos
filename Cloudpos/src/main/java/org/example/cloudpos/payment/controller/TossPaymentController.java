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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Toss 결제 승인/취소 엔드포인트를 담당합니다.
 */
@Slf4j
@RestController
@RequestMapping("/payments/toss")
@RequiredArgsConstructor
@Tag(name = "Toss Payments API", description = "프런트/외부 채널이 호출하는 Toss 결제 승인·취소 API")
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @Operation(
            summary = "Toss 결제 승인",
            description = "paymentKey/orderId/amount 를 Toss API 로 전달해 결제를 확정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공",
                    content = @Content(schema = @Schema(implementation = TossPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "orderId 로 Payment 를 찾을 수 없음")
    })
    @PostMapping("/confirm")
    public ResponseEntity<TossPaymentResponse> confirmPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TossPaymentRequest.class))
            )
            @RequestBody TossPaymentRequest request
    ){

        log.info("[POST] /payments/toss/confirm orderId={}, paymentKey={}",
                request.getOrderId(), request.getPaymentKey());
        TossPaymentResponse response = tossPaymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Toss 결제 취소",
            description = """
                    paymentKey 를 기준으로 Toss 결제를 취소하고, 내부 Payment/TossPayment 상태를 업데이트합니다.
                    cancelReason 은 기본값("사용자 요청에 따른 취소")을 제공하며 필요 시 변경할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(schema = @Schema(implementation = TossPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (취소 불가 상태 등)"),
            @ApiResponse(responseCode = "404", description = "paymentKey 또는 orderId 로 결제를 찾을 수 없음")
    })
    @PostMapping("/cancel/{paymentKey}")
    public ResponseEntity<TossPaymentResponse> cancelPayment(
            @Parameter(required = true)
            @PathVariable String paymentKey,
            @Parameter(required = true)
            @RequestParam String orderId,
            @Parameter
            @RequestParam(defaultValue = "사용자 요청에 따른 취소") String cancelReason
    ){

        log.info("[POST] /payments/toss/cancel orderId={}, paymentKey={}, reason={}",
                orderId, paymentKey, cancelReason);
        TossPaymentResponse response = tossPaymentService.cancelPayment(paymentKey, cancelReason, orderId);
        return ResponseEntity.ok(response);
    }
}
