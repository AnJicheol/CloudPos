package org.example.cloudpos.payment.service;

import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.domain.PaymentStatus;
import org.example.cloudpos.payment.dto.PaymentRequest;
import org.example.cloudpos.payment.dto.PaymentResponse;
import org.example.cloudpos.payment.repository.PaymentMethodRepository;
import org.example.cloudpos.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        // Order 생성
        order = new Order(LocalDateTime.now(), "cart_001", "order_001");
        order.applyTotalAmount(30000); // 총 금액 설정

        // PaymentMethod 생성 (리플렉션으로 필드 주입)
        paymentMethod = new PaymentMethod();
        ReflectionTestUtils.setField(paymentMethod, "id", 1L);
        ReflectionTestUtils.setField(paymentMethod, "code", "CARD");
        ReflectionTestUtils.setField(paymentMethod, "name", "신용카드");
        ReflectionTestUtils.setField(paymentMethod, "isActive", true);
    }

    @Test
    void createPayment_success() {
        // given
        PaymentRequest request = new PaymentRequest(order.getOrderId(), 1L);
        when(paymentRepository.findByOrder_OrderId(order.getOrderId())).thenReturn(Optional.empty());
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PaymentResponse response = paymentService.createPayment(order, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(response.getAmountFinal()).isEqualTo(30000);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.BEFORE_PAYMENT);
        assertThat(response.getMethodName()).isEqualTo("신용카드");

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentMethodRepository, times(1)).findById(1L);
    }

    @Test
    void createPayment_fail_whenDuplicate() {
        // given
        when(paymentRepository.findByOrder_OrderId(order.getOrderId()))
                .thenReturn(Optional.of(mock(Payment.class)));

        PaymentRequest request = new PaymentRequest(order.getOrderId(), 1L);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(order, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 결제가 생성된 주문입니다");
    }

    @Test
    void createPayment_fail_whenInvalidMethod() {
        // given
        when(paymentRepository.findByOrder_OrderId(order.getOrderId())).thenReturn(Optional.empty());
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentRequest request = new PaymentRequest(order.getOrderId(), 1L);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(order, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 결제 수단입니다");
    }

    @Test
    void createPayment_fail_whenAmountZero() {
        // given
        Order zeroOrder = new Order(LocalDateTime.now(), "cart_002", "order_002");
        // 총액을 안 넣으면 0
        PaymentRequest request = new PaymentRequest(zeroOrder.getOrderId(), 1L);
        when(paymentRepository.findByOrder_OrderId(zeroOrder.getOrderId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(zeroOrder, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제 금액이 0원 이하");
    }

    @Test
    void getPaymentByOrderId_success() {
        // given
        Payment payment = Payment.builder()
                .paymentId("pay_001")
                .order(order)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.BEFORE_PAYMENT)
                .amountFinal(30000)
                .build();

        when(paymentRepository.findByOrder_OrderId(order.getOrderId())).thenReturn(Optional.of(payment));

        // when
        PaymentResponse response = paymentService.getPaymentByOrderId(order.getOrderId());

        // then
        assertThat(response.getOrderId()).isEqualTo("order_001");
        assertThat(response.getMethodName()).isEqualTo("신용카드");
        assertThat(response.getAmountFinal()).isEqualTo(30000);
    }

    @Test
    void getPaymentByOrderId_fail_whenNotFound() {
        // given
        when(paymentRepository.findByOrder_OrderId(order.getOrderId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(order.getOrderId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제 정보를 찾을 수 없습니다");
    }
}
