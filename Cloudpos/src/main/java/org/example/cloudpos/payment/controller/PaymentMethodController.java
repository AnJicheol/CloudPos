package org.example.cloudpos.payment.controller;


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
 *
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
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // 결제수단 등록
    @PostMapping
    public ResponseEntity<PaymentMethodResponse> create(@RequestBody PaymentMethodRequest request) {
        log.info("[POST] /payments/methods 호출됨 code={}, name={}", request.getCode(), request.getName());
        PaymentMethod method = paymentMethodService.createMethod(request.getCode(), request.getName());
        return ResponseEntity.ok(PaymentMethodResponse.from(method));
    }

    // 결제수단 활성화
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        paymentMethodService.activate(id);
        return ResponseEntity.noContent().build();
    }

    // 결제수단 비활성화
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        paymentMethodService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // 결제수단 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //전체 결제수단 조회
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAll() {
        List<PaymentMethodResponse> responses = paymentMethodService.getAll()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 활성화된 결제수단 조회
    @GetMapping("/active")
    public ResponseEntity<List<PaymentMethodResponse>> getActives() {
        List<PaymentMethodResponse> responses = paymentMethodService.getActives()
                .stream()
                .map(PaymentMethodResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }



}
