package org.example.cloudpos.cart.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.CartItemResponse;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.cloudpos.cart.dto.CreateCartResponse;
import org.example.cloudpos.cart.dto.AddFirstRequest;
import org.example.cloudpos.cart.dto.QuantityUpdateResponse;

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
 * 장바구니 로직은 {@link CartService}에서 처리하며,
 * 식별자는 {@link org.example.cloudpos.cart.domain.UlidGenerator}로 생성됩니다.
 * </p>

 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 생성",
            description = "새로운 cartId를 생성하고 Redis에 초기 상태(EMPTY)로 저장합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "생성 성공",
            content = @Content(schema = @Schema(implementation = CreateCartResponse.class))
    )
    @PostMapping
    public ResponseEntity<CreateCartResponse> createCart() {
        return ResponseEntity.ok(cartService.createCart());
    }

    @Operation(
            summary = "첫 상품 추가",
            description = "장바구니에 첫 상품을 담습니다. 존재하지 않으면 수량 1로 추가합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "추가 성공",
            content = @Content(schema = @Schema(implementation = QuantityUpdateResponse.class))
    )
    @ApiResponse(responseCode = "409", description = "상태 규칙 위반")
    @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    @PostMapping("/{cartId}/items:first")
    public ResponseEntity<QuantityUpdateResponse> addFirst(@PathVariable String cartId, @RequestBody AddFirstRequest request) {
        cartService.addFirstTime(cartId, request.productId());
        int qty = cartService.getQuantity(cartId, request.productId());
        return ResponseEntity.ok(new QuantityUpdateResponse(qty));
    }

    @Operation(
            summary = "상품 수량 변경",
            description = "장바구니에서 지정한 상품의 수량을 delta 만큼 증감합니다. " +
                    "프론트에서 버튼 연타를 모아서 delta 값으로 한 번에 요청할 때 사용합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "수량 변경 성공",
            content = @Content(schema = @Schema(implementation = QuantityUpdateResponse.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "상태 규칙 위반 (수정 불가능한 상태이거나 최소 수량 1개 규칙 위반)"
    )
    @ApiResponse(
            responseCode = "410",
            description = "만료된 장바구니"
    )
    @PostMapping("/{cartId}/items/{productId}")
    public ResponseEntity<QuantityUpdateResponse> changeQuantity(@PathVariable String cartId, @PathVariable String productId, @RequestParam int delta) {
        int qty = cartService.changeQuantity(cartId, productId,delta);
        return ResponseEntity.ok(new QuantityUpdateResponse(qty));
    }

    @Operation(
            summary = "상품 제거",
            description = "장바구니에서 지정한 상품을 완전히 제거합니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "제거 성공"
    )
    @ApiResponse(responseCode = "409", description = "상태 규칙 위반")
    @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable String cartId, @PathVariable String productId) {
        cartService.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "장바구니 조회",
            description = "담긴 상품과 수량을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CartItemResponse.class))
    )
    @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemResponse>> getCart(@PathVariable String cartId) {
        return ResponseEntity.ok(cartService.getAll(cartId));
    }

    @Operation(
            summary = "장바구니 비우기",
            description = "모든 상품 및 상태 키를 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "비우기 성공")
    @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clear(@PathVariable String cartId) {
        cartService.clear(cartId);
        return ResponseEntity.noContent().build();
    }
}
