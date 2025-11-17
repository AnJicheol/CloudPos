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


import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.payment.api.OrderApi;
import org.example.cloudpos.payment.dto.PaymentRequest;
import org.example.cloudpos.payment.dto.PaymentResponse;
import org.example.cloudpos.payment.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <h2>PaymentController</h2>
 *
 * 내부 결제 정보 생성 및 조회를 담당하는 REST 컨트롤러입니다.
 * (토스 승인/취소는 TossPaymentController에서 처리)
 */

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "결제 생성 및 조회 API (Toss 승인/취소 제외)")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderApi orderApi;

    /**
     * 결제 생성 API
     */
    @Operation(
            summary = "결제 생성",
            description = "주문 ID를 기반으로 결제 정보를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 생성 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문 정보를 찾을 수 없음",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 생성 요청 정보 (orderId, 결제수단 등)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            )
            @RequestBody PaymentRequest request
    ) {
        log.info("[POST] /payments called with orderId={}", request.getOrderId());
        Order order = orderApi.getOrder(request.getOrderId());
        PaymentResponse response = paymentService.createPayment(order, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 조회 API
     */
    @Operation(
            summary = "결제 조회",
            description = "주문 ID를 통해 결제 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 주문의 결제를 찾을 수 없음",
                    content = @Content)
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "결제를 조회할 주문 ID", required = true)
            @PathVariable String orderId
    ) {
        log.info("[GET] /payments/{} called", orderId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
