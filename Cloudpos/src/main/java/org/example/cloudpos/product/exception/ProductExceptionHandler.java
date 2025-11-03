package org.example.cloudpos.product.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 상품 도메인에서 발생하는 예외를 전역적으로 처리하는 핸들러입니다.
 *
 * <p>해당 클래스는 {@code org.example.cloudpos.product} 패키지 내부에서 발생한 예외만
 * 처리하도록 제한되며, 다른 도메인의 예외에는 영향을 주지 않습니다.</p>
 *
 * <p>예외 처리 시 HTTP 상태 코드와 JSON 형식의 응답을 반환하며,
 * 클라이언트가 에러 원인을 명확히 파악할 수 있도록 메시지를 제공합니다.</p>
 *
 *
 * @author Esther
 * @since 1.0
 */
@RestControllerAdvice(basePackages = "org.example.cloudpos.product")
public class ProductExceptionHandler {

    /**
     * 상품이 존재하지 않을 경우 발생하는 예외를 처리합니다.
     *
     * <p>처리 결과로 {@code 404 Not Found} 상태 코드를 반환합니다.</p>
     *
     * @param e {@link ProductNotFoundException} 예외 객체
     * @return JSON Body + 404 응답
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of(
                "message", e.getMessage(),
                "domain", "product"
        ));
    }
}
