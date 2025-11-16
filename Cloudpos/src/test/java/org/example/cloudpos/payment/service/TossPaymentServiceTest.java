package org.example.cloudpos.payment.service;

import org.example.cloudpos.order.listener.PaymentResultListener;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.TossPayment;
import org.example.cloudpos.payment.dto.TossPaymentRequest;
import org.example.cloudpos.payment.dto.TossPaymentResponse;
import org.example.cloudpos.payment.repository.PaymentRepository;
import org.example.cloudpos.payment.repository.TossPaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <h2>TossPaymentServiceTest</h2>
 *
 * JUnit5 + Mockito 기반의 단위 테스트 클래스입니다.
 * TossPaymentService의 결제 승인 로직을 독립적으로 검증합니다.
 *
 * 테스트 목적:
 *  ✅ Toss 결제 승인 성공 시 DB 저장 확인
 *  ✅ Toss 결제 승인 실패 시 예외 처리 확인
 */

@ExtendWith(MockitoExtension.class)
class TossPaymentServiceTest {

    @Mock
    private RestTemplate restTemplate;  // 외부 API 호출 Mock

    @Mock
    private PaymentResultListener paymentResultListener;

    @Mock
    private TossPaymentRepository tossPaymentRepository; // DB Mock

    @Mock
    private PaymentRepository paymentRepository; // DB Mock

    @InjectMocks
    private TossPaymentService tossPaymentService; // 테스트 대상 서비스

    @Test
    @DisplayName("✅ Toss 결제 승인 성공 시 TossPayment 저장 및 응답 반환")
    void confirmPayment_success() {
        // given (테스트 데이터 준비)
        TossPaymentRequest request = new TossPaymentRequest();
        request = mock(TossPaymentRequest.class);
        when(request.getPaymentKey()).thenReturn("pay_1234");
        when(request.getOrderId()).thenReturn("order_001");
        when(request.getAmount()).thenReturn(30000L);

        TossPaymentResponse responseBody = new TossPaymentResponse();
        responseBody = mock(TossPaymentResponse.class);
        when(responseBody.getPaymentKey()).thenReturn("pay_1234");
        when(responseBody.getOrderId()).thenReturn("order_001");
        when(responseBody.getStatus()).thenReturn("DONE");
        when(responseBody.getMethod()).thenReturn("CARD");
        when(responseBody.getTotalAmount()).thenReturn(30000L);
        when(responseBody.getApprovedAt()).thenReturn(LocalDateTime.now().toString());

        // Toss 서버 응답 Mock
        ResponseEntity<TossPaymentResponse> responseEntity =
                new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(TossPaymentResponse.class)))
                .thenReturn(responseEntity);

        Payment mockPayment = mock(Payment.class);
        when(paymentRepository.findByOrder_OrderId("order_001"))
                .thenReturn(Optional.of(mockPayment));
        when(tossPaymentRepository.save(any(TossPayment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when (테스트 대상 메서드 실행)
        TossPaymentResponse result = tossPaymentService.confirmPayment(request);

        // then (검증)
        assertNotNull(result);
        assertEquals("DONE", result.getStatus());
        verify(tossPaymentRepository, times(1)).save(any(TossPayment.class));
        verify(paymentRepository, times(1)).findByOrder_OrderId("order_001");
    }

    @Test
    @DisplayName("❌ Toss 결제 승인 실패 시 RuntimeException 발생")
    void confirmPayment_fail_httpError() {
        // given
        TossPaymentRequest request = mock(TossPaymentRequest.class);
        when(request.getPaymentKey()).thenReturn("pay_fail");
        when(request.getOrderId()).thenReturn("order_999");
        when(request.getAmount()).thenReturn(10000L);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(TossPaymentResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // when & then
        assertThrows(RuntimeException.class, () -> tossPaymentService.confirmPayment(request));
        verify(tossPaymentRepository, never()).save(any());
    }
}