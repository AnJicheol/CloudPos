package org.example.cloudpos.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.dto.OrderResponse;
import org.example.cloudpos.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "결제 시작",
            description = """
                장바구니 ID와 결제 수단을 받아 장바구니 상태를 '결제 진행'으로 변경하고,
                현재 장바구니 명세와 할인 정보를 기준으로 최종 결제 금액을 확정합니다.
                한 번 확정된 금액은 이후에 변경되지 않습니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 생성/결제 시작 성공",
            content = @Content(
                    schema = @Schema(implementation = OrderResponse.class)
            )
    )
    @PostMapping("/start-payment/{cartId}")
    public ResponseEntity<OrderResponse> startPayment(@PathVariable String cartId) {
        return ResponseEntity.ok(orderService.startPayment(cartId));
    }
}