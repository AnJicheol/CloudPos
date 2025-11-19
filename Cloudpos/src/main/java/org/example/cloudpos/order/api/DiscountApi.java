package org.example.cloudpos.order.api;

import org.example.cloudpos.order.dto.CartDto;

import java.util.List;
import java.util.Map;

public interface DiscountApi {
    Map<String, Integer> getDiscountMap(List<CartDto> cartDtoList);
}
