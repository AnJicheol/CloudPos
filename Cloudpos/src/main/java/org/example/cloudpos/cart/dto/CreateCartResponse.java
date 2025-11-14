package org.example.cloudpos.cart.dto;

import org.example.cloudpos.cart.api.ProductSummaryHandlerApi;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.exception.CartExpiredException;
import org.example.cloudpos.cart.fsm.CartEvent;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;

public record CreateCartResponse(String cartId) {}