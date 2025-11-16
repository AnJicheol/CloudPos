package org.example.cloudpos.product.service;

import org.example.cloudpos.product.domain.ProductStatus;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductSummaryResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 상품 도메인의 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 *
 * <p>상품 생성, 조회, 수정, 삭제(소프트 삭제)와 같은 핵심 기능을 선언하며,
 * 실제 구현은 {@link ProductServiceImpl} 에서 수행됩니다.</p>
 *
 * <p>서비스 계층은 컨트롤러와 리포지토리 사이의 중간 계층으로서,
 * 트랜잭션 처리 및 도메인 규칙을 담당합니다.</p>
 *
 * @author Esther
 * @since 1.0
 */
public interface ProductService {

    /**
     * 신규 상품을 생성합니다.
     *
     * <p>요청에 {@code productId} 가 생략된 경우 서버에서 자동 생성되며,
     * 중복 여부는 저장 전 검증됩니다.</p>
     *
     * @param req 상품 생성 요청 DTO
     * @return 생성된 상품의 DB 기본 키(id)
     */
    String create(ProductCreateRequest req);

    /**
     * 상품을 ID로 조회합니다.
     *
     * @param productId 상품의 DB 기본 키
     * @return 상품 정보를 담은 응답 DTO
     * @throws org.example.cloudpos.product.exception.ProductNotFoundException 존재하지 않는 상품일 경우
     */
    ProductResponse get(String productId);

    /**
     * 상품을 소프트 삭제 처리합니다.
     *
     * <p>실제로 DB에서 삭제하지 않고 상태를
     * {@link ProductStatus#ARCHIVED} 로 변경합니다.</p>
     *
     * @param productId 상품의 DB 기본 키
     * @throws org.example.cloudpos.product.exception.ProductNotFoundException 삭제 대상이 존재하지 않을 경우
     */
    void archive(String productId);

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>페이지네이션(Pageable)을 이용해 상품을 일정 단위로 조회할 수 있으며,
     * 기본 정렬 및 페이지 크기는 컨트롤러 또는 호출 측에서 지정합니다.</p>
     *
     * <p>응답 객체에는 각 상품의 주요 정보(id, 이름, 가격, 상태 등)가 포함됩니다.</p>
     *
     * @param pageable 페이지 요청 정보 (페이지 번호, 크기, 정렬 조건 등)
     * @return 상품 목록의 페이지 객체
     */
    Page<ProductResponse> list(Pageable pageable);

    /**
     * 기존 상품 정보를 수정합니다.
     *
     * <p>요청 본문에 포함된 필드만 변경되며, 부분 수정(PATCH) 방식으로 동작합니다.</p>
     *
     * <p>상품이 존재하지 않을 경우 {@link org.example.cloudpos.product.exception.ProductNotFoundException}
     * 이 발생하며, 정상적으로 수정되면 별도의 반환값은 없습니다.</p>
     *
     * @param productId  수정 대상 상품의 DB 기본 키
     * @param req 수정할 상품 정보가 담긴 요청 DTO
     * @throws org.example.cloudpos.product.exception.ProductNotFoundException 수정 대상이 존재하지 않을 경우
     */
    void update(String productId, ProductUpdateRequest req);

    /**
     * 상품명을 기준으로 상품을 검색합니다.
     *
     * <p>대소문자를 구분하지 않으며, {@code ARCHIVED} 상태의 상품은 제외됩니다.</p>
     *
     * @param name 검색할 상품명 (부분 일치 가능)
     * @param pageable 페이지 요청 정보
     * @return 검색된 상품 목록 페이지
     */
    Page<ProductResponse> searchByName(String name, Pageable pageable);

    /**
     * 상품 식별자({@code productId})로 상품의 요약 정보를 조회합니다.
     *
     * @param productId 조회할 상품의 식별자
     * @return 상품의 요약 정보 DTO
     */
    ProductSummaryResponse findSummaryByProductId(String productId);

}
