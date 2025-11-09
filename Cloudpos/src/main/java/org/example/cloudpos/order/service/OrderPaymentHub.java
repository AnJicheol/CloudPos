package org.example.cloudpos.order.service;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.reaction.PaymentSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderPaymentHub implements PaymentResultListener, PaymentStartListener {
    private final ApplicationEventPublisher publisher;


    @Override
    public void onPaymentSuccess(String orderId) {
        publisher.publishEvent(new PaymentSuccessEvent(orderId));
    }

    @Override
    public void onPaymentFailure(String orderId) {

    }

    @Override
    public void onPaymentCanceled(String orderId) {

    }

    @Override
    public void callPayment() {

    }
}
