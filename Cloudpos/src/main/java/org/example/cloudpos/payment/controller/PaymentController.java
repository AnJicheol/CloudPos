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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PaymentController exposes CRUD-style APIs for {@link PaymentService}.
 * Toss 확정/취소는 {@link org.example.cloudpos.payment.controller.TossPaymentController}에서 처리합니다.
 */
@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "결제 생성/조회 API (Toss 승인·취소 제외)")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderApi orderApi;

    @Operation(
            summary = "결제 정보 생성",
            description = "orderId 와 paymentMethodId 로 Payment 엔티티를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 생성 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 결제, 주문 금액 오류 등)"),
            @ApiResponse(responseCode = "404", description = "주문 ID 로 Order 를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            )
            @RequestBody PaymentRequest request
    ){

        log.info("[POST] /payments called with orderId={}", request.getOrderId());
        Order order = orderApi.getOrder(request.getOrderId());
        PaymentResponse response = paymentService.createPayment(order, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "주문 ID 로 결제 조회",
        description = "orderId 에 해당하는 가장 최근 Payment 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 orderId 로 생성된 결제가 없음")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(required = true)
            @PathVariable String orderId
    ){

        log.info("[GET] /payments/{} called", orderId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
