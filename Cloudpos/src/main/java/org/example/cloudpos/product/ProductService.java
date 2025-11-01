package org.example.cloudpos.product;

import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;

/**
 * 상품 도메인의 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 *
 * <p>상품 생성, 조회, 삭제(소프트 삭제)와 같은 핵심 기능을 선언하며,
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
    Long create(ProductCreateRequest req);

    /**
     * 상품을 ID로 조회합니다.
     *
     * @param id 상품의 DB 기본 키
     * @return 상품 정보를 담은 응답 DTO
     * @throws org.example.cloudpos.product.exception.ProductNotFoundException
     *         존재하지 않는 상품일 경우
     */
    ProductResponse get(Long id);

    /**
     * 상품을 소프트 삭제 처리합니다.
     *
     * <p>실제로 DB에서 삭제하지 않고 상태를
     * {@link ProductStatus#ARCHIVED} 로 변경합니다.</p>
     *
     * @param id 상품의 DB 기본 키
     * @throws org.example.cloudpos.product.exception.ProductNotFoundException
     *         삭제 대상이 존재하지 않을 경우
     */
    void archive(Long id);
}
