package org.example.cloudpos.discount.listener;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.discount.service.DiscountService;
import org.springframework.stereotype.Component;
import org.example.cloudpos.discount.domain.Discount;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscountListenerImpl implements DiscountListener{
    private final DiscountService discountService;


    @Override
    public Map<String, Integer> getDiscountMap(List<String> productIdList) {
        return discountService.getDiscountList(productIdList).stream()
                .collect(Collectors.toMap(
                        Discount::getProductId,
                        Discount::getAmount
                ));
    }

}
