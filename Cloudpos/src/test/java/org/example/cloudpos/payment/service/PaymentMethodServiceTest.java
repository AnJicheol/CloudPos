package org.example.cloudpos.payment.service;

import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private PaymentMethod activeMethod;
    private PaymentMethod inactiveMethod;

    @BeforeEach
    void setUp() {
        activeMethod = PaymentMethod.builder()
                .id(1L)
                .code("CARD")
                .name("신용카드")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        inactiveMethod = PaymentMethod.builder()
                .id(2L)
                .code("CASH")
                .name("현금")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createMethod_success() {
        // given
        when(paymentMethodRepository.findByCode("CARD")).thenReturn(Optional.empty());
        when(paymentMethodRepository.save(any(PaymentMethod.class)))
                .thenAnswer(invocation -> {
                    PaymentMethod saved = invocation.getArgument(0);
                    ReflectionTestUtils.setField(saved, "id", 1L);
                    return saved;
                });

        // when
        PaymentMethod result = paymentMethodService.createMethod("card", "신용카드");

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCode()).isEqualTo("CARD");
        assertThat(result.getName()).isEqualTo("신용카드");
        assertThat(result.isActive()).isTrue();

        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void createMethod_fail_whenDuplicateCode() {
        // given
        when(paymentMethodRepository.findByCode("CARD"))
                .thenReturn(Optional.of(activeMethod));

        // when & then
        assertThatThrownBy(() -> paymentMethodService.createMethod("CARD", "신용카드"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 결제수단 코드");
    }

    @Test
    void activate_success() {
        // given
        when(paymentMethodRepository.findById(2L)).thenReturn(Optional.of(inactiveMethod));

        // when
        paymentMethodService.activate(2L);

        // then
        assertThat(inactiveMethod.isActive()).isTrue();
        verify(paymentMethodRepository, times(1)).findById(2L);
    }

    @Test
    void deactivate_success() {
        // given
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(activeMethod));

        // when
        paymentMethodService.deactivate(1L);

        // then
        assertThat(activeMethod.isActive()).isFalse();
        verify(paymentMethodRepository, times(1)).findById(1L);
    }

    @Test
    void getById_success() {
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(activeMethod));

        PaymentMethod result = paymentMethodService.getById(1L);

        assertThat(result.getCode()).isEqualTo("CARD");
    }

    @Test
    void getById_fail_whenNotFound() {
        when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentMethodService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제수단을 찾을 수 없습니다");
    }

    @Test
    void getAll_success() {
        when(paymentMethodRepository.findAll()).thenReturn(List.of(activeMethod, inactiveMethod));

        List<PaymentMethod> result = paymentMethodService.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void getActives_success() {
        when(paymentMethodRepository.findByIsActiveTrue()).thenReturn(List.of(activeMethod));

        List<PaymentMethod> result = paymentMethodService.getActives();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CARD");
    }

    @Test
    void delete_success() {
        // when
        paymentMethodService.delete(1L);

        // then
        verify(paymentMethodRepository, times(1)).deleteById(1L);
    }
}
