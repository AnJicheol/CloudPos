package org.example.cloudpos.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.example.cloudpos.cart")
public class CartExceptionHandler {
    // 공통 에러 응답 DTO
    public record ErrorResponse(
            String code,
            String message
    ) {}

    /**
     * 장바구니 TTL 만료 또는 존재하지 않는 cartId 접근
     * → 410 Gone
     */
    @ExceptionHandler(CartExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCartExpired(CartExpiredException ex) {
        ErrorResponse body = new ErrorResponse(
                "CART_EXPIRED",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.GONE).body(body); // 410
    }


    /**
     * 카트에 담으려는 상품이 없거나 ProductSummary 조회 실패
     * → 404 Not Found
     */
    @ExceptionHandler(CartProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(CartProductNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                "PRODUCT_NOT_FOUND_IN_CART",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // 404
    }


    /**
     * FSM(상태 전이) 예외, 잘못된 상태에서 장바구니 조작 시
     * → 409 Conflict
     */
    @ExceptionHandler(InvalidCartStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCartState(InvalidCartStateException ex) {
        ErrorResponse body = new ErrorResponse(
                "INVALID_CART_STATE",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // 409
    }

    /**
     * IllegalStateException: 수량 최소 1 미만 등
     * → 400 Bad Request
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse body = new ErrorResponse(
                "INVALID_OPERATION",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Cart 관련 예외 중 명시되지 않은 모든 오류 처리
     * → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {

        ErrorResponse body = new ErrorResponse(
                "INTERNAL_CART_ERROR",
                "서버 오류가 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
