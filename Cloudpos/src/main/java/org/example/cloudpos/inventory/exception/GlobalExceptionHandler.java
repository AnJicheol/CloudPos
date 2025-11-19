package org.example.cloudpos.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.cloudpos.product.exception.ProductNotFoundException;
import org.example.cloudpos.inventory.exception.InventoryNotFoundException;
import org.example.cloudpos.inventory.exception.DuplicateStoreProductException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 애플리케이션 전역 예외 처리기(Global Exception Handler).
 *
 * <p>도메인 계층이나 서비스 계층에서 발생하는 예외를
 * 일관된 HTTP 응답 형태로 변환합니다.</p>
 *
 * <p>각 {@code @ExceptionHandler} 메서드는 특정 예외를 감지해
 * 적절한 HTTP 상태 코드(404, 409 등)와 메시지를 반환합니다.</p>
 *
 * <h2>처리 대상 예외</h2>
 * <ul>
 *   <li>{@link ProductNotFoundException} → 404 Not Found</li>
 *   <li>{@link InventoryNotFoundException} → 404 Not Found</li>
 *   <li>{@link DuplicateStoreProductException} → 409 Conflict</li>
 * </ul>
 *
 * <h2>응답 형태 예시</h2>
 * <pre>{@code
 * HTTP/1.1 404 Not Found
 * Content-Type: text/plain
 *
 * 존재하지 않는 상품입니다. productId=1
 * }</pre>
 *
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 상품이 존재하지 않을 때의 예외를 처리합니다.
     *
     * @param e {@link ProductNotFoundException} 예외
     * @return 404 Not Found 상태와 예외 메시지를 담은 응답
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * 매장이 존재하지 않을 때의 예외를 처리합니다.
     *
     * @param e {@link InventoryNotFoundException} 예외
     * @return 404 Not Found 상태와 예외 메시지를 담은 응답
     */
    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<String> handleInventoryNotFound(InventoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * 매장에 이미 등록된 상품을 다시 추가하려 할 때의 예외를 처리합니다.
     *
     * @param e {@link DuplicateStoreProductException} 예외
     * @return 409 Conflict 상태와 예외 메시지를 담은 응답
     */
    @ExceptionHandler(DuplicateStoreProductException.class)
    public ResponseEntity<String> handleDuplicateStoreProduct(DuplicateStoreProductException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    /**
     * {@link IllegalArgumentException} 발생 시 처리하는 예외 핸들러입니다.
     *
     * <p>잘못된 요청 파라미터나 비즈니스 로직 검증 실패 등으로 인해
     * {@code IllegalArgumentException}이 발생하면,
     * HTTP 400(Bad Request) 상태 코드와 함께 에러 정보를 JSON 형태로 반환합니다.</p>
     *
     * @param e   발생한 {@code IllegalArgumentException} 인스턴스
     * @param req 요청 객체 (에러가 발생한 요청 경로 확인용)
     * @return 에러 상세 정보(JSON): {@code timestamp}, {@code status}, {@code error},
     *         {@code message}, {@code path} 포함
     * @status 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", e.getMessage(),
                "path", req.getRequestURI()
        );
    }

}
