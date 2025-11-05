package org.example.cloudpos.cart.controller;


import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.domain.UlidGenerator;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    // ----- DTO (간단 버전) -----
    public record CreateCartResponse(String cartId, String state) {}
    public record AddFirstRequest(String productId) {}
    public record AddFirstResponse(String cartId, String state) {}
    public record GenericStateResponse(String state) {}

    //장바구니 생성
    @PostMapping
    public ResponseEntity<CreateCartResponse> createCart(){
        String cartId= UlidGenerator.generate();
        cartService.createCart(cartId);
        CartState state=cartService.getState(cartId);
        return ResponseEntity.ok(new CreateCartResponse(cartId, state.name()));
    }
    //상품 담기
    @PostMapping("/{cartId}/items:first")
    public ResponseEntity<AddFirstResponse> addFirst(@PathVariable String cartId, @RequestBody AddFirstRequest request){
        cartService.addFirstTime(cartId, request.productId);
        return ResponseEntity.ok(new AddFirstResponse(cartId, cartService.getState(cartId).name()));
    }

    //상품 수량 +1
    @PostMapping("/{cartId}/items/{productId}/inc")
    public ResponseEntity<GenericStateResponse> increment(@PathVariable String cartId, @PathVariable String productId){
        cartService.addOne(cartId, productId);
        return ResponseEntity.ok(new GenericStateResponse(cartService.getState(cartId).name()));
    }

    //상품 수량 -1
    @PostMapping("/{cartId}/items/{productId}/dec")
    public ResponseEntity<GenericStateResponse> decrement(@PathVariable String cartId, @PathVariable String productId){
        boolean ok= cartService.removeOne(cartId, productId);
        return ResponseEntity.ok(new GenericStateResponse(cartService.getState(cartId).name()));
    }

    //상품 제거
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<GenericStateResponse> removeItem(@PathVariable String cartId,@PathVariable String productId) {
        cartService.removeItem(cartId, productId);
        return ResponseEntity.ok(new GenericStateResponse(cartService.getState(cartId).name()));
    }

    //전체조회
    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemDto>> getCart(@PathVariable String cartId) {
        return ResponseEntity.ok(cartService.getAll(cartId));
    }


    //장바구니 삭제
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clear(@PathVariable String cartId) {
        cartService.clear(cartId);
        return ResponseEntity.noContent().build();
    }


    //상태조회
    @GetMapping("/{cartId}/state")
    public ResponseEntity<GenericStateResponse> getState(@PathVariable String cartId) {
        return ResponseEntity.ok(new GenericStateResponse(cartService.getState(cartId).name()));
    }

}
