package org.example.cloudpos.payment.api;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.order.domain.Order;
import org.example.cloudpos.order.listener.OrderListener;
import org.example.cloudpos.order.listener.PaymentResultListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApiImpl implements OrderApi, PaymentOutApi{
    private final OrderListener orderListener;
    private final PaymentResultListener paymentResultListener;

    public Order getOrder(String orderId){
        return orderListener.getOrderById(orderId);
    }

    @Override
    public void onPaymentSuccess(String orderId) {
        paymentResultListener.onPaymentSuccess(orderId);
    }

    @Override
    public void onPaymentFailure(String orderId) {
        paymentResultListener.onPaymentFailure(orderId);
    }

    @Override
    public void onPaymentCanceled(String orderId) {
        paymentResultListener.onPaymentCanceled(orderId);
    }
}
