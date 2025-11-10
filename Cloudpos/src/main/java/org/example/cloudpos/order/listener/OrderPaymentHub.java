package org.example.cloudpos.order.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.api.CartStateHandlerApi;
import org.example.cloudpos.order.task.PaymentSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderPaymentHub implements PaymentResultListener {
    private final ApplicationEventPublisher publisher;
    private final CartStateHandlerApi cartStateHandlerApi;

    @Override
    public void onPaymentSuccess(String orderId) {
        cartStateHandlerApi.stateClose(orderId);
        publisher.publishEvent(new PaymentSuccessEvent(orderId));
    }

    @Override
    public void onPaymentFailure(String orderId) {
        cartStateHandlerApi.stateOpen(orderId);
    }

    @Override
    public void onPaymentCanceled(String orderId) {
        cartStateHandlerApi.stateOpen(orderId);
    }
}
