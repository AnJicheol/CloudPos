package org.example.cloudpos.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.payment.domain.DiscountPolicy;
import org.example.cloudpos.payment.dto.DiscountPolicyRequest;
import org.example.cloudpos.payment.dto.DiscountPolicyResponse;
import org.example.cloudpos.payment.repository.DiscountPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>DiscountPolicyService</h2>
 *
 * 할인 정책 비즈니스 로직을 담당하는 서비스 계층 클래스입니다.
 * - 할인 정책 생성, 수정, 삭제, 비활성화
 * - 현재 시점에 유효한 정책 조회
 *
 * Repository를 통해 DB에 접근하며,
 * Controller에서 호출되어 응답 DTO를 반환합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscountPolicyService {

    private final DiscountPolicyRepository discountPolicyRepository;

    // 할인 정책 등록
    public DiscountPolicyResponse createPolicy(DiscountPolicyRequest request) {
        DiscountPolicy policy = DiscountPolicy.builder()
                .name(request.getName())
                .discountType(request.getDiscountType())
                .value(request.getValue())
                .isActive(request.getIsActive())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .build();

        discountPolicyRepository.save(policy);
        log.info("[할인 정책 생성] name={}, type={}, value={}", request.getName(), request.getDiscountType(), request.getValue());

        return DiscountPolicyResponse.from(policy);
    }

    // 전체 할인 정책 조회
    public List<DiscountPolicyResponse> getAllPolicies() {
        return discountPolicyRepository.findAll()
                .stream()
                .map(DiscountPolicyResponse::from)
                .collect(Collectors.toList());
    }

    // 활성화된 정책만 조회
    public List<DiscountPolicyResponse> getActivePolicies() {
        LocalDateTime now = LocalDateTime.now();
        return discountPolicyRepository.findByIsActiveTrueAndValidFromBeforeAndValidToAfter(now, now)
                .stream()
                .map(DiscountPolicyResponse::from)
                .collect(Collectors.toList());
    }

    // 정책 수정
    @Transactional
    public DiscountPolicyResponse updatePolicy(Long id, DiscountPolicyRequest request) {
        DiscountPolicy policy = discountPolicyRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("할인 정책을 찾을수 없습니다"));

        policy.update(
                request.getName(),
                request.getDiscountType(),
                request.getValue(),
                request.getIsActive(),
                request.getValidFrom(),
                request.getValidTo()
        );

        log.info("[할인 정책 수정] id={}, name={}", id, request.getName());

        return DiscountPolicyResponse.from(policy);
    }

    // 정책 비활성화
    @Transactional
    public void deactivatePolicy(Long id) {
        DiscountPolicy policy = discountPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("할인 정책을 찾을 수 없습니다. id=" + id));

        policy.deactivate();
        log.info("[할인 정책 비활성화] id={}, name={}", id, policy.getName());
    }

    // 정책 삭제
    @Transactional
    public void deletePolicy(Long id) {
        discountPolicyRepository.deleteById(id);
        log.info("[할인 정책 삭제] id={}", id);
    }


}
