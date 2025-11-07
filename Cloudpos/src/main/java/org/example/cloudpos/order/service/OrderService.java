package org.example.cloudpos.order.service;




public interface OrderService {

    /**
     * 결제가 정상적으로 완료되었을 때 호출됩니다.
     */
    void onPaymentSuccess(String orderId);

    /**
     * 결제가 실패했을 때 호출됩니다.
     */
    void onPaymentFailure(String orderId);

    /**
     * 결제가 취소되었을 때 호출됩니다.
     */
    void onPaymentCanceled(String orderId);


}
