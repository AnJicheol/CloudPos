package org.example.cloudpos.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.cart.dto.AddFirstRequest;
import org.example.cloudpos.cart.dto.CartItemResponse;
import org.example.cloudpos.cart.dto.CreateCartResponse;
import org.example.cloudpos.cart.dto.QuantityUpdateResponse;
import org.example.cloudpos.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 장바구니 CRUD + 수량 제어 API.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 생성",
            description = "새로운 cartId를 발급하고 Redis에 초기 상태(EMPTY)로 저장합니다."
    )
    @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = CreateCartResponse.class)))
    @PostMapping
    public ResponseEntity<CreateCartResponse> createCart(){

        return ResponseEntity.ok(cartService.createCart());
    }

    @Operation(
            summary = "첫 상품 추가",
            description = """
                    cartId 로 식별되는 장바구니에 처음 상품을 추가합니다.
                    이미 상품이 존재하면 수량을 1 증가시키며, AddFirstRequest 는 productId 만 포함합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가/증가 성공",
                    content = @Content(schema = @Schema(implementation = QuantityUpdateResponse.class))),
            @ApiResponse(responseCode = "409", description = "장바구니 상태 규칙 위반"),
            @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    })
    @PostMapping("/{cartId}/items")
    public ResponseEntity<QuantityUpdateResponse> addItem(
            @Parameter(required = true)
            @PathVariable String cartId,
            @RequestBody AddFirstRequest request
    ){

        cartService.addFirstTime(cartId, request.productId());
        int qty = cartService.getQuantity(cartId, request.productId());
        return ResponseEntity.ok(new QuantityUpdateResponse(qty));
    }

    @Operation(
            summary = "수량 변경",
            description = "delta 값(+/-)을 이용해 장바구니의 상품 수량을 증감합니다. 최소 수량은 1개입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(schema = @Schema(implementation = QuantityUpdateResponse.class))),
            @ApiResponse(responseCode = "409", description = "장바구니 상태 규칙 위반(예: CHECKOUT_PENDING)"),
            @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    })
    @PostMapping("/{cartId}/items/{productId}")
    public ResponseEntity<QuantityUpdateResponse> changeQuantity(
            @PathVariable String cartId,
            @PathVariable String productId,
            @Parameter(required = true)
            @RequestParam int delta
    ){

        int qty = cartService.changeQuantity(cartId, productId, delta);
        return ResponseEntity.ok(new QuantityUpdateResponse(qty));
    }

    @Operation(
            summary = "상품 삭제",
            description = "해당 cartId 에서 특정 productId 를 완전히 제거합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "409", description = "장바구니 상태 규칙 위반"),
            @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    })
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable String cartId, @PathVariable String productId){

        cartService.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "장바구니 전체 조회",
            description = "모든 상품과 수량을 반환합니다. ProductSummary + quantity 조합의 리스트입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    })
    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemResponse>> getCart(@PathVariable String cartId){

        return ResponseEntity.ok(cartService.getAll(cartId));
    }

    @Operation(
            summary = "장바구니 비우기",
            description = "Redis 에 저장된 cart:{id}:* 키를 모두 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비우기 성공"),
            @ApiResponse(responseCode = "410", description = "만료된 장바구니")
    })
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clear(@PathVariable String cartId){

        cartService.clear(cartId);
        return ResponseEntity.noContent().build();
    }
}
