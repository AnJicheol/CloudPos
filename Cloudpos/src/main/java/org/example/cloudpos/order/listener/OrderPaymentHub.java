package org.example.cloudpos.order.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.api.CartStateHandlerApi;
import org.example.cloudpos.order.task.PaymentSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;



/**
 * 결제 모듈에서 전달된 결제 결과를 다른 모듈로 전파하는 허브.
 *
 * <p>자신이 직접 상태를 변경하지 않고, 주문 ID를 이용해
 * 장바구니 모듈 등에 "열림/닫힘" 신호를 전달하고
 * 필요 시 도메인 이벤트를 발행한다.</p>
 *
 * <p>실제 장바구니 상태 변경에 대한 책임은 각 도메인 모듈에 있으며,
 * 이 클래스는 결제 결과를 브로드캐스트하는 용도의 내부 계층이다.</p>
 */
@Service
@RequiredArgsConstructor
public class OrderPaymentHub implements PaymentResultListener {
    private final ApplicationEventPublisher publisher;
    private final CartStateHandlerApi cartStateHandlerApi;


    /**
     * 결제가 성공했음을 다른 모듈에 알리는 신호를 보낸다.
     *
     * @param orderId 결제가 성공한 주문의 문자열 주문 ID
     */
    @Override
    public void onPaymentSuccess(String orderId) {
        cartStateHandlerApi.stateClose(orderId);
        publisher.publishEvent(new PaymentSuccessEvent(orderId));
    }

    /**
     * 결제가 실패했음을 다른 모듈에 알리는 신호를 보낸다.
     *
     * @param orderId 결제가 실패한 주문의 문자열 주문 ID
     */
    @Override
    public void onPaymentFailure(String orderId) {
        cartStateHandlerApi.stateOpen(orderId);
    }

    /**
     * 결제가 취소되었음을 다른 모듈에 알리는 신호를 보낸다.
     *
     * @param orderId 결제가 취소된 주문의 문자열 주문 ID
     */
    @Override
    public void onPaymentCanceled(String orderId) {
        cartStateHandlerApi.stateOpen(orderId);
    }
}
