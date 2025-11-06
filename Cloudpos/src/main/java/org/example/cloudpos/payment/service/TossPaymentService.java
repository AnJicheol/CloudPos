package org.example.cloudpos.payment.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.TossPayment;
import org.example.cloudpos.payment.dto.TossPaymentRequest;
import org.example.cloudpos.payment.dto.TossPaymentResponse;
import org.example.cloudpos.payment.repository.PaymentRepository;
import org.example.cloudpos.payment.repository.TossPaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * <h2>TossPaymentService</h2>
 *
 * 토스페이먼츠 결제 승인 로직을 담당하는 서비스 클래스입니다.
 *
 * <p>프론트엔드에서 받은 paymentKey, orderId, amount 값을 이용해
 * Toss Payments 서버로 결제 승인 API를 호출하고, 승인 결과를 반환합니다.</p>
 *
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TossPaymentService {

    @Value("${toss.base-url}")
    private String tossBaseUrl;

    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate;
    private final TossPaymentRepository tossPaymentRepository;
    private final PaymentRepository paymentRepository;

    //토스 결제 승인 요청
    @Transactional
    public TossPaymentResponse confirmPayment(TossPaymentRequest request) {
        log.info("[TOSS 결제 승인 요청] paymentKey={}, orderId={}, amount={}",
                request.getPaymentKey(), request.getOrderId(), request.getAmount());

        try {
            // 인증 헤더 생성
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TossPaymentRequest> entity = new HttpEntity<>(request, headers);

            // Toss 서버 결제 승인 API 호출
            String confirmUrl = tossBaseUrl + "/confirm";

            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    confirmUrl, HttpMethod.POST, entity, TossPaymentResponse.class
            );

            TossPaymentResponse body = response.getBody();
            if (body == null) {
                throw new RuntimeException("Toss 결제 승인 응답이 비어있습니다.");
            }

            log.info("[TOSS 결제 승인 성공] paymentKey={}, status={}",
                    body.getPaymentKey(), body.getStatus());


            //  Payment 조회
            Payment payment = paymentRepository.findByOrderId(body.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

            //  TossPayment 엔티티 생성 및 저장
            TossPayment tossPayment = TossPayment.builder()
                    .paymentKey(body.getPaymentKey())
                    .payment(payment)
                    .totalAmount(body.getTotalAmount())
                    .method(body.getMethod())
                    .status(body.getStatus())
                    .requestedAt(LocalDateTime.now())
                    .approvedAt(LocalDateTime.parse(body.getApprovedAt()))
                    .isCancelable(true)
                    .build();

            tossPaymentRepository.save(tossPayment);
            log.info("[DB 저장 완료] paymentKey={}, totalAmount={}",
                    tossPayment.getPaymentKey(), tossPayment.getTotalAmount());

            return body;

        } catch (HttpClientErrorException e) {
            log.error("[TOSS 결제 승인 실패] {}", e.getResponseBodyAsString());
            throw new RuntimeException("Toss 결제 승인 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[서버 내부 오류] {}", e.getMessage(), e);
            throw new RuntimeException("서버 내부 오류 발생: " + e.getMessage());
        }
    }
}
