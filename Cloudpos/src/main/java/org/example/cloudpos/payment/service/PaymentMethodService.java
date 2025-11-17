package org.example.cloudpos.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cloudpos.payment.domain.PaymentMethod;
import org.example.cloudpos.payment.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h2>PaymentMethodService</h2>
 *
 * 결제 수단(PaymentMethod) 관리 로직을 담당하는 서비스 계층입니다.
 *
 * 주요 기능:
 *  - 결제 수단 등록, 수정, 삭제
 *  - 결제 수단 활성화 / 비활성화 관리
 *  - 전체 또는 활성 상태의 결제 수단 조회
 *
 * 예: CARD, CASH, KAKAO_PAY, TOSS_PAY 등
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    // 결제수단 등록
    @Transactional
    public PaymentMethod createMethod(String code, String name) {
        paymentMethodRepository.findByCode(code.toUpperCase())
                .ifPresent(pm -> { throw new IllegalArgumentException("이미 존재하는 결제수단 코드입니다: " + code); });

        PaymentMethod method = PaymentMethod.create(code, name);
        paymentMethodRepository.save(method);

        log.info("[결제수단 등록] id={}, code={}, name={}", method.getId(), method.getCode(), method.getName());
        return method;
    }



    // 활성화
    @Transactional
    public void activate(Long id) {
        PaymentMethod method = getById(id);
        method.activate();
        log.info("[결제수단 활성화] id={}, code={}", id, method.getCode());
    }

    // 비활성화
    @Transactional
    public void deactivate(Long id) {
        PaymentMethod method = getById(id);
        method.deactivate();
        log.info("[결제수단 비활성화] id={}, code={}", id, method.getCode());
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        paymentMethodRepository.deleteById(id);
        log.info("[결제수단 삭제] id={}", id);
    }

    // 전체 조회
    public List<PaymentMethod> getAll() {
        return paymentMethodRepository.findAll();
    }

    // 활성화된 수단만 조회
    public List<PaymentMethod> getActives() {
        return paymentMethodRepository.findByActiveTrue();
    }

    // 단건 조회
    public PaymentMethod getById(Long id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제수단을 찾을 수 없습니다. id=" + id));
    }
}
