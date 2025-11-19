package org.example.cloudpos.discount.listener;

import org.example.cloudpos.order.dto.CartDto;

import java.util.List;
import java.util.Map;

public interface DiscountListener {
    Map<String, Integer> getDiscountMap(List<String> productIdList);
}
