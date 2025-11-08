package org.example.cloudpos.payment.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.order.service.PaymentResultListener;
import org.example.cloudpos.payment.domain.Payment;
import org.example.cloudpos.payment.domain.PaymentStatus;
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
 * 처리 및 주문서비스 통보르 담당하기도하는 클래스입니다
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
    private final PaymentResultListener paymentResultListener;

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

            log.info("[TOSS 결제 승인 성공] paymentKey={}, status={}, totalAmount={}",
                    body.getPaymentKey(), body.getStatus(), body.getTotalAmount());



            //  Payment 조회
            String orderId = body.getOrderId();
            log.info("[Order ID 조회] 외부 orderId={}", orderId);

            Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. orderId=" + orderId));

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
            payment.updateStatus(PaymentStatus.COMPLETED);
            log.info("[DB 저장 완료] paymentKey={}, totalAmount={}",
                    tossPayment.getPaymentKey(), tossPayment.getTotalAmount());

            paymentResultListener.onPaymentSuccess();

            return body;

        } catch (HttpClientErrorException e) {
            String msg = e.getResponseBodyAsString() != null ? e.getResponseBodyAsString() : e.getMessage();
            log.error("[TOSS 결제 승인 실패] {}", msg);
            paymentResultListener.onPaymentFailure(); // 실패 통보
            throw new RuntimeException("Toss 결제 승인 실패: " + msg);
        } catch (Exception e) {
            log.error("[서버 내부 오류] {}", e.getMessage(), e);
            paymentResultListener.onPaymentFailure(); // 실패 통보
            throw new RuntimeException("서버 내부 오류 발생: " + e.getMessage());
        }
    }

    //결제 취소처리
    @Transactional
    public  TossPaymentResponse cancelPayment(String paymentKey, String cancelReason){
        log.info("[TOSS 결제 취소 요청] paymentKey={}, reason={}", paymentKey, cancelReason);

        try{
            //인증 헤더 생성
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            //토스 결제 취소 API 호출
            String cancelUrl = tossBaseUrl + "/" + paymentKey + "/cancel";

            ResponseEntity<TossPaymentResponse> response = restTemplate.exchange(
                    cancelUrl, HttpMethod.POST, entity, TossPaymentResponse.class
            );

            TossPaymentResponse body = response.getBody();
            if (body == null) throw new RuntimeException("Toss 결제 취소 응답이 비어있습니다.");

            log.info("[TOSS 결제 취소 성공] paymentKey={}, status={}", paymentKey, body.getStatus());

            // DB 업데이트
            TossPayment tossPayment = tossPaymentRepository.findById(paymentKey)
                    .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다. paymentKey=" + paymentKey));

            tossPayment.updateStatus("CANCELED");
            tossPayment.updateCancelable(false);

            Payment payment = tossPayment.getPayment();
            payment.updateStatus(PaymentStatus.CANCELED);

            log.info("[DB 반영 완료] paymentKey={}, paymentStatus={}", paymentKey, payment.getPaymentStatus());

            // 주문 서비스에 결제 취소 통보
            paymentResultListener.onPaymentCanceled();

            return body;
        }catch (HttpClientErrorException e) {
            String msg = e.getResponseBodyAsString() != null ? e.getResponseBodyAsString() : e.getMessage();
            log.error("[TOSS 결제 취소 실패] {}", msg);
            throw new RuntimeException("Toss 결제 취소 실패: " + msg);
        } catch (Exception e) {
            log.error("[서버 내부 오류] {}", e.getMessage(), e);
            throw new RuntimeException("서버 내부 오류 발생: " + e.getMessage());
        }
    }



}
