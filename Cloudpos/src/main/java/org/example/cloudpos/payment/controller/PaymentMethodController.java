package org.example.cloudpos.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.dto.PaymentMethodRequest;
import org.example.cloudpos.payment.dto.PaymentMethodResponse;
import org.example.cloudpos.payment.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제 수단 CRUD + 상태 변경 API.
 */
@Slf4j
@RestController
@RequestMapping("/payments/methods")
@RequiredArgsConstructor
@Tag(name = "Payment Method API", description = "결제 수단 등록/활성화/조회/삭제 API")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Operation(
            summary = "결제 수단 등록",
            description = "code/name 값을 받아 결제 수단을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 code 등)")
    })
    @PostMapping
    public ResponseEntity<PaymentMethodResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentMethodRequest.class))
            )
            @RequestBody PaymentMethodRequest request
    ){

        log.info("[POST] /payments/methods code={}, name={}", request.getCode(), request.getName());
        PaymentMethod method = paymentMethodService.createMethod(request.getCode(), request.getName());
        return ResponseEntity.ok(PaymentMethodResponse.from(method));
    }

    @Operation(summary = "결제 수단 활성화", description = "비활성 상태의 결제 수단을 다시 사용 가능하게 만듭니다.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(
            @Parameter(required = true)
            @PathVariable Long id
    ){

        paymentMethodService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 수단 비활성화", description = "해당 결제 수단을 더 이상 노출하지 않습니다.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @Parameter(required = true)
            @PathVariable Long id
    ){

        paymentMethodService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 수단 삭제", description = "행 자체를 삭제합니다. 이미 사용된 주문이 있다면 주의하세요.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(required = true)
            @PathVariable Long id
    ){

        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 결제 수단 조회", description = "활성/비활성 여부와 관계없이 전체 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class)))
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAll(){

        List<PaymentMethodResponse> responses = paymentMethodService.getAll()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "활성 결제 수단 조회", description = "현재 사용자에게 노출 가능한 결제 수단만 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentMethodResponse.class)))
    @GetMapping("/active")
    public ResponseEntity<List<PaymentMethodResponse>> getActives(){

        List<PaymentMethodResponse> responses = paymentMethodService.getActives()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
