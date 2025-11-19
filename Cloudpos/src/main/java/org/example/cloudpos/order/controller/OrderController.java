package org.example.cloudpos.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.dto.OrderResponse;
import org.example.cloudpos.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 주문 생성(결제 시작) API.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @Operation(
            summary = "주문 생성 및 결제 시작",
            description = "경로의 cartId를 기준으로 Order를 생성하고 CHECKOUT을 시작합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "주문 생성 성공",
            content = @Content(schema = @Schema(implementation = OrderResponse.class))
    )
    @PostMapping("/start-payment/{cartId}")
    public ResponseEntity<OrderResponse> startPayment(@PathVariable String cartId) {
        OrderResponse res = orderService.startPayment(cartId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
