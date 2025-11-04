package org.example.cloudpos.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.product.service.ProductService;
import org.example.cloudpos.product.domain.ProductStatus;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 상품 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 *
 * <p>다른 도메인(재고/주문 등)에서 상품 생성 및 조회 기능을 사용할 수 있도록
 * 외부 공개용 API를 제공합니다.</p>
 *
 * <p>API 명세는 Swagger UI를 통해 확인할 수 있습니다.</p>
 *
 * <pre>
 * Base URL: /api/products
 * </pre>
 *
 * @author Esther
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    /**
     * 신규 상품을 등록합니다.
     *
     * <p>요청 본문에 상품명, 가격, 상태 등이 포함되며
     * {@code productId}가 비어 있을 경우 서버에서 자동 생성됩니다.</p>
     *
     * <p>상품이 정상적으로 생성되면 {@code 201 Created} 와 함께
     * 생성된 상품 정보를 응답합니다.</p>
     *
     * @param req 상품 생성 요청 DTO (유효성 검증 포함)
     * @return 생성된 상품 정보와 Location 헤더를 포함한 응답
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest req) {
        Long id = service.create(req);
        ProductResponse body = service.get(id);
        return ResponseEntity.created(URI.create("/api/products/" + id)).body(body);
    }

    /**
     * 상품을 ID로 조회합니다.
     *
     * <p>다른 도메인 연동 및 디버깅용 API이며,
     * 존재하지 않는 ID를 조회할 경우 {@code 404 Not Found} 예외가 발생합니다.</p>
     *
     * @param id 상품 기본키 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>상품명, 가격, 상태 등의 기본 정보를 포함하며
     * 페이지네이션(Pageable) 파라미터를 이용해 페이지 번호와 크기를 지정할 수 있습니다.</p>
     *
     * <p>예: <code>/api/products?page=0&size=10&sort=name,asc</code></p>
     *
     * @param pageable 페이지 요청 정보 (페이지 번호, 크기, 정렬 기준 등)
     * @return 상품 목록의 페이지 객체
     */
    @GetMapping
    public Page<ProductResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    /**
     * 기존 상품 정보를 수정합니다.
     *
     * <p>요청 본문에는 수정할 필드(예: 이름, 가격, 상태 등)만 포함할 수 있으며,
     * 부분 업데이트(PATCH) 방식으로 동작합니다.</p>
     *
     * <p>상품이 존재하지 않으면 {@code 404 Not Found} 예외가 발생하며,
     * 요청이 성공하면 본문 없는 {@code 204 No Content}를 반환합니다.</p>
     *
     * @param id 수정할 상품의 기본키 ID
     * @param req 상품 수정 요청 DTO
     * @return 본문 없는 {@code 204 No Content} 응답
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ProductUpdateRequest req) {
        service.update(id, req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품을 삭제(아카이브 처리)합니다.
     *
     * <p>실제로 DB에서 삭제하지 않고 상태 값을
     * {@link ProductStatus#ARCHIVED} 로 변경하는 소프트 삭제 방식입니다.</p>
     *
     * <p>요청이 성공하면 본문 없는 {@code 204 No Content}를 반환합니다.</p>
     *
     * @param id 상품 기본키 ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        service.archive(id);
        return ResponseEntity.noContent().build();
    }
}
