package org.example.cloudpos.payment.service;


import org.example.cloudpos.payment.domain.DiscountPolicy;
import org.example.cloudpos.payment.domain.DiscountType;
import org.example.cloudpos.payment.dto.DiscountPolicyRequest;
import org.example.cloudpos.payment.repository.DiscountPolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <h2>DiscountPolicyServiceTest</h2>
 *
 * 할인 정책 비즈니스 로직을 검증하는 단위 테스트 클래스입니다.
 * DB 접근은 모두 Mockito Mock으로 대체합니다.
 *
 * 테스트 목적:
 *  ✅ 할인 정책 생성 성공
 *  ✅ 활성 정책 조회
 *  ✅ 전체 정책 조회
 *  ✅ 존재하지 않는 정책 수정 시 예외 발생
 */

@ExtendWith(MockitoExtension.class)
class DiscountPolicyServiceTest {

    @Mock
    private DiscountPolicyRepository discountPolicyRepository;

    @InjectMocks
    private DiscountPolicyService discountPolicyService;

    @Test
    @DisplayName("✅ 할인 정책 생성 성공")
    void createPolicy_success() {
        // given
        DiscountPolicyRequest request = mock(DiscountPolicyRequest.class);
        when(request.getName()).thenReturn("신규회원 10% 할인");
        when(request.getDiscountType()).thenReturn(DiscountType.PERCENTAGE);
        when(request.getValue()).thenReturn(10);
        when(request.getIsActive()).thenReturn(true);
        when(request.getValidFrom()).thenReturn(LocalDateTime.now().minusDays(1));
        when(request.getValidTo()).thenReturn(LocalDateTime.now().plusDays(7));

        when(discountPolicyRepository.save(any(DiscountPolicy.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        var response = discountPolicyService.createPolicy(request);

        // then
        assertNotNull(response);
        assertEquals("신규회원 10% 할인", response.getName());
        verify(discountPolicyRepository, times(1)).save(any(DiscountPolicy.class));
    }

    @Test
    @DisplayName("✅ 활성화된 정책 조회 성공")
    void getActivePolicies_success() {
        // given
        DiscountPolicy activePolicy = DiscountPolicy.builder()
                .name("여름 세일 15%")
                .discountType(DiscountType.PERCENTAGE)
                .value(15)
                .isActive(true)
                .validFrom(LocalDateTime.now().minusDays(2))
                .validTo(LocalDateTime.now().plusDays(3))
                .build();

        when(discountPolicyRepository.findByIsActiveTrueAndValidFromBeforeAndValidToAfter(any(), any()))
                .thenReturn(List.of(activePolicy));

        // when
        var result = discountPolicyService.getActivePolicies();

        // then
        assertEquals(1, result.size());
        assertEquals("여름 세일 15%", result.get(0).getName());
        verify(discountPolicyRepository, times(1))
                .findByIsActiveTrueAndValidFromBeforeAndValidToAfter(any(), any());
    }

    @Test
    @DisplayName("✅ 전체 할인 정책 조회 성공")
    void getAllPolicies_success() {
        // given
        DiscountPolicy p1 = DiscountPolicy.builder()
                .name("세일 1")
                .discountType(DiscountType.FIXED_AMOUNT)
                .value(5000)
                .isActive(true)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(2))
                .build();

        DiscountPolicy p2 = DiscountPolicy.builder()
                .name("세일 2")
                .discountType(DiscountType.PERCENTAGE)
                .value(20)
                .isActive(false)
                .validFrom(LocalDateTime.now().minusDays(3))
                .validTo(LocalDateTime.now().plusDays(5))
                .build();

        when(discountPolicyRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // when
        var result = discountPolicyService.getAllPolicies();

        // then
        assertEquals(2, result.size());
        verify(discountPolicyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("❌ 존재하지 않는 ID로 수정 시 IllegalArgumentException 발생")
    void updatePolicy_fail_notFound() {
        // given
        when(discountPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());
        DiscountPolicyRequest request = mock(DiscountPolicyRequest.class);

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                discountPolicyService.updatePolicy(99L, request));
    }
}