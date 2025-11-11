package org.example.cloudpos.cart.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.domain.CartState;
import org.example.cloudpos.cart.dto.CartItemDto;
import org.example.cloudpos.cart.domain.UlidGenerator;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**

 * 장바구니 관련 REST API를 제공하는 컨트롤러입니다.
 *
 * <p><b>기능 개요</b></p>
 * <ul>
 * <li>장바구니 생성 및 상태 조회</li>
 * <li>상품 추가, 수량 증가/감소, 상품 제거</li>
 * <li>장바구니 전체 조회 및 비우기</li>
 * </ul>
 *
 * <p><b>주요 엔드포인트</b></p>
 * <ul>
 * <li>POST /api/carts — 새 장바구니 생성</li>
 * <li>POST /api/carts/{cartId}/items:first — 첫 상품 추가</li>
 * <li>POST /api/carts/{cartId}/items/{productId}/inc — 상품 수량 +1</li>
 * <li>POST /api/carts/{cartId}/items/{productId}/dec — 상품 수량 -1</li>
 * <li>DELETE /api/carts/{cartId}/items/{productId} — 상품 제거</li>
 * <li>GET /api/carts/{cartId} — 장바구니 전체 조회</li>
 * <li>DELETE /api/carts/{cartId} — 장바구니 비우기</li>
 * <li>GET /api/carts/{cartId}/state — 현재 상태 조회</li>
 * </ul>
 *
 * <p><b>비고</b><br>
 * 장바구니 로직은 {@link org.example.cloudpos.cart.service.CartService}에서 처리하며,
 * 식별자는 {@link org.example.cloudpos.cart.domain.UlidGenerator}로 생성됩니다.
 * </p>

 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public record CreateCartResponse(String cartId, String state) {}
    public record AddFirstRequest(String productId) {}
    public record GenericStateResponse(String state) {}
    public record QuantityUpdateResponse(int quantity, String state) {}



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
    public ResponseEntity<QuantityUpdateResponse> addFirst(@PathVariable String cartId, @RequestBody AddFirstRequest request){
        cartService.addFirstTime(cartId, request.productId);
        int qty = cartService.getQuantity(cartId, request.productId);

        return ResponseEntity.ok(new QuantityUpdateResponse(qty, cartService.getState(cartId).name()));
    }

    //상품 수량 +1
    @PostMapping("/{cartId}/items/{productId}/inc")
    public ResponseEntity<QuantityUpdateResponse> increment(@PathVariable String cartId, @PathVariable String productId){
        cartService.addOne(cartId, productId);
        int qty = cartService.getQuantity(cartId, productId);

        return ResponseEntity.ok(new QuantityUpdateResponse(qty,cartService.getState(cartId).name()));
    }

    //상품 수량 -1
    @PostMapping("/{cartId}/items/{productId}/dec")
    public ResponseEntity<QuantityUpdateResponse> decrement(@PathVariable String cartId, @PathVariable String productId){
        boolean ok= cartService.removeOne(cartId, productId);
        if (!ok) {
            throw new IllegalStateException("상품이 이미 최소수량입니다.");
        }
        int qty = cartService.getQuantity(cartId, productId);

        return ResponseEntity.ok(new QuantityUpdateResponse(qty,cartService.getState(cartId).name()));
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


}
