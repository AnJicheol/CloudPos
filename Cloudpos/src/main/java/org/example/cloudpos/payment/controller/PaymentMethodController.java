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
import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.dto.PaymentMethodRequest;
import org.example.cloudpos.payment.dto.PaymentMethodResponse;
import org.example.cloudpos.payment.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h2>PaymentMethodController</h2>
 *
 * 결제 수단 관리용 REST API 컨트롤러.
 * (관리자 또는 초기 세팅 시 사용)
 * 주요 기능:
 *  - 결제 수단 등록
 *  - 결제 수단 활성화 / 비활성화
 *  - 결제 수단 삭제
 *  - 전체 / 활성 결제 수단 조회
 */

@Slf4j
@RestController
@RequestMapping("/payments/methods")
@RequiredArgsConstructor
@Tag(name = "Payment Method API", description = "결제 수단 등록/활성화/조회/삭제 등 관리용 API")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Operation(summary = "결제 수단 등록", description = "새로운 결제 수단을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public ResponseEntity<PaymentMethodResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 결제 수단 정보 (code, name)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentMethodRequest.class))
            )
            @RequestBody PaymentMethodRequest request
    ) {
        log.info("[POST] /payments/methods 호출됨 code={}, name={}", request.getCode(), request.getName());
        PaymentMethod method = paymentMethodService.createMethod(request.getCode(), request.getName());
        return ResponseEntity.ok(PaymentMethodResponse.from(method));
    }

    @Operation(summary = "결제 수단 활성화", description = "비활성화된 결제 수단을 다시 활성화합니다.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @Parameter(description = "활성화할 결제 수단 ID", required = true)
            @PathVariable Long id
    ) {
        paymentMethodService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 수단 비활성화", description = "해당 결제 수단을 비활성화 상태로 변경합니다.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "비활성화할 결제 수단 ID", required = true)
            @PathVariable Long id
    ) {
        paymentMethodService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 수단 삭제", description = "등록된 결제 수단을 완전히 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 결제 수단 ID", required = true)
            @PathVariable Long id
    ) {
        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 결제 수단 조회", description = "등록된 모든 결제 수단 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class)))
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAll() {
        List<PaymentMethodResponse> responses = paymentMethodService.getAll()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "활성화된 결제 수단 조회", description = "현재 사용 가능한 결제 수단만 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class)))
    @GetMapping("/active")
    public ResponseEntity<List<PaymentMethodResponse>> getActives() {
        List<PaymentMethodResponse> responses = paymentMethodService.getActives()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}