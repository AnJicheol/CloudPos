package org.example.cloudpos.payment.api;

public interface PaymentOutApi {

    void onPaymentSuccess(String orderId);

    void onPaymentFailure(String orderId);

    void onPaymentCanceled(String orderId);
}
