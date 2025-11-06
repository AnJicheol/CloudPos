package org.example.cloudpos.payment.controller;
/**
 * <h2>DiscountPolicyController</h2>
 *
 * 할인 정책 관련 REST API를 제공하는 컨트롤러입니다.
 * 주로 관리자(Admin)가 정책을 등록, 조회, 수정, 비활성화할 때 사용됩니다.
 *
 * 엔드포인트 예시:
 *  - POST   /payments/discount-policies
 *  - GET    /payments/discount-policies
 *  - GET    /payments/discount-policies/active
 *  - PUT    /payments/discount-policies/{id}
 *  - PATCH  /payments/discount-policies/{id}/deactivate
 *  - DELETE /payments/discount-policies/{id}
 */
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.payment.dto.DiscountPolicyRequest;
import org.example.cloudpos.payment.dto.DiscountPolicyResponse;
import org.example.cloudpos.payment.service.DiscountPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments/discount-policies")
@RequiredArgsConstructor
public class DiscountPolicyController {

    private final DiscountPolicyService discountPolicyService;

    // 할인 정책 등록
    @PostMapping
    public ResponseEntity<DiscountPolicyResponse> create(@RequestBody DiscountPolicyRequest request) {
        return ResponseEntity.ok(discountPolicyService.createPolicy(request));
    }

    // 전체 할인 정책 조회
    @GetMapping
    public ResponseEntity<List<DiscountPolicyResponse>> getAll() {
        return ResponseEntity.ok(discountPolicyService.getAllPolicies());
    }

    // 현재 활성화된 정책만 조회
    @GetMapping("/active")
    public ResponseEntity<List<DiscountPolicyResponse>> getActive() {
        return ResponseEntity.ok(discountPolicyService.getActivePolicies());
    }

    // 정책 수정
    @PutMapping("/{id}")
    public ResponseEntity<DiscountPolicyResponse> update(@PathVariable Long id,
                                                         @RequestBody DiscountPolicyRequest request) {
        return ResponseEntity.ok(discountPolicyService.updatePolicy(id, request));
    }

    // 정책 비활성화
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        discountPolicyService.deactivatePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // 정책 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discountPolicyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}