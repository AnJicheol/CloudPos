package org.example.cloudpos.order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ApplicationEventPublisher publisher;


    @Override
    public void onPaymentSuccess(String orderId) {

    }

    @Override
    public void onPaymentFailure(String orderId) {

    }

    @Override
    public void onPaymentCanceled(String orderId) {

    }
}
