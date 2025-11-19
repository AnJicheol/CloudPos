package org.example.cloudpos.order.api;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.discount.listener.DiscountListener;
import org.example.cloudpos.order.dto.CartDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiscountApiImpl implements DiscountApi{
    private final DiscountListener discountListener;

    public Map<String, Integer> getDiscountMap(List<CartDto> cartDtoList){
        return discountListener.getDiscountMap(cartDtoList.stream()
                .map(CartDto::productId)
                .toList());
    }
}
