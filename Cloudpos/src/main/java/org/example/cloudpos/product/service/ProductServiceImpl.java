package org.example.cloudpos.product.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.cloudpos.product.domain.ProductStatus;
import org.example.cloudpos.product.domain.Product;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductSummaryResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.example.cloudpos.product.exception.ProductNotFoundException;
import org.example.cloudpos.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link ProductService} 구현체로,
 * 상품 생성, 조회, 삭제(소프트 삭제) 등의 비즈니스 로직을 담당합니다.
 *
 * <p>해당 서비스는 트랜잭션 범위 안에서 실행되며
 * JPA를 이용해 상품 엔티티를 저장/조회합니다.</p>
 *
 * <p>상품의 식별자는 두 가지를 사용합니다:</p>
 * <ul>
 *     <li>{@code id} — DB 기본 키 (자동 증가)</li>
 *     <li>{@code productId} — 비즈니스용 상품 코드 (ULID 기반 자동 생성)</li>
 * </ul>
 *
 * <h3>이미지 관리</h3>
 * <ul>
 *     <li>{@code imageUrl} — 대표 이미지 URL로, 실제 이미지 존재 여부는 검증하지 않습니다.</li>
 *     <li>URL 문자열만 저장하며, 외부 CDN 또는 S3 경로를 지정할 수 있습니다.</li>
 * </ul>
 *
 * @author Esther
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    /**
     * 신규 상품을 생성합니다.
     *
     * <p>상품 식별자인 {@code productId}는 ULID 방식으로 자동 생성되며,
     * 요청 본문에는 포함되지 않습니다.</p>
     *
     * <p>{@code imageUrl}은 선택적 필드로, 입력되지 않으면 null로 저장됩니다.</p>
     *
     * @param req 상품 생성 요청 DTO
     * @return 생성된 상품의 DB 기본 키(id)
     */
    @Override
    public String create(ProductCreateRequest req) {
        Product p = new Product();
        p.setProductId(UlidCreator.getUlid().toString());
        p.setName(req.name());
        p.setPrice(req.price());
        p.setStatus(req.status() != null ? req.status() : ProductStatus.ACTIVE);
        p.setImageUrl(req.imageUrl());

        return repo.save(p).getProductId();
    }

    /**
     * 상품을 ID로 조회합니다.
     *
     * @param productId 상품의 DB 기본 키
     * @return 상품 정보를 담은 응답 DTO
     * @throws ProductNotFoundException 조회 대상이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    @Override
    public ProductResponse get(String productId) {
        Product p = repo.findByProductId(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return new ProductResponse(p.getId(), p.getProductId(), p.getName(), p.getPrice(), p.getStatus(),p.getImageUrl());
    }

    /**
     * 상품을 소프트 삭제(ARCHIVED 상태 전환)합니다.
     *
     * <p>실제 DB에서 삭제하지 않고 상태만 변경하며,
     * 트랜잭션 종료 시 JPA Dirty Checking에 의해 자동으로 UPDATE 됩니다.</p>
     *
     * @param productId 상품의 DB 기본 키
     * @throws ProductNotFoundException 삭제 대상이 존재하지 않을 경우
     */
    @Override
    public void archive(String productId) {
        Product p = repo.findByProductId(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        p.setStatus(ProductStatus.ARCHIVED);
    }

    /**
     * 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>아카이브(ARCHIVED) 상태가 아닌 상품만 조회되며,
     * 요청된 {@link Pageable} 정보를 이용해 페이지네이션과 정렬을 수행합니다.</p>
     *
     * <p>조회 결과는 {@link ProductResponse} DTO로 변환되어 반환되며,
     * 각 항목은 상품의 주요 속성(id, 상품코드, 이름, 가격, 상태, 대표이미지)을 포함합니다.</p>
     *
     * @param pageable 페이지 요청 정보 (페이지 번호, 크기, 정렬 조건 등)
     * @return 상품 목록 페이지 객체
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> list(Pageable pageable) {
        return repo.findByStatusNot(ProductStatus.ARCHIVED, pageable)
                .map(p -> new ProductResponse(p.getId(), p.getProductId(), p.getName(), p.getPrice(), p.getStatus(),p.getImageUrl()));
    }

    /**
     * 기존 상품 정보를 수정합니다.
     *
     * <p>요청 객체에 포함된 필드만 업데이트되며,
     * null 값으로 전달된 필드는 변경되지 않습니다.</p>
     *
     * <p>상품이 존재하지 않을 경우 {@link ProductNotFoundException}이 발생하며,
     * 수정된 엔티티는 트랜잭션 종료 시점에 JPA Dirty Checking을 통해 자동 반영됩니다.</p>
     *
     * @param productId 수정 대상 상품의 DB 기본 키
     * @param req 수정할 상품 정보가 담긴 요청 DTO
     * @throws ProductNotFoundException 수정 대상 상품이 존재하지 않을 경우
     */
    @Override
    public void update(String productId, ProductUpdateRequest req) {
        Product p = repo.findByProductId(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        if (req.name() != null) {
            p.setName(req.name());
        }
        if (req.price() != null) {
            p.setPrice(req.price());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }
        if (req.imageUrl() != null) {
            p.setImageUrl(req.imageUrl());
        }
    }

    /**
     * 상품명을 기준으로 상품을 검색합니다.
     *
     * <p>입력된 {@code name}이 {@code null} 또는 공백일 경우 빈 문자열로 처리하며,
     * {@link ProductStatus#ARCHIVED} 상태(아카이브된 상품)는 검색 결과에서 제외합니다.</p>
     *
     * <p>검색 결과는 {@link ProductResponse}로 매핑되어
     * 페이지 단위로 반환됩니다.</p>
     *
     * @param name 검색할 상품명 (부분 일치, 대소문자 무시)
     * @param pageable 페이지 요청 정보
     * @return 검색된 상품 목록 페이지
     * @since 1.0
     */
    @Override
    public Page<ProductResponse> searchByName(String name, Pageable pageable) {
        String keyword = name == null ? "" : name.trim();


        return repo.findByNameContainingIgnoreCaseAndStatusNot(
                        keyword, ProductStatus.ARCHIVED, pageable)
                .map(p ->
                        new ProductResponse(
                                p.getId(),
                                p.getProductId(),
                                p.getName(),
                                p.getPrice(),
                                p.getStatus(),
                                p.getImageUrl()
                        )
                );

    }

    /**
     * 상품 식별자({@code productId})로 상품 요약 정보를 조회합니다.
     *
     * <p>해당 상품이 존재하지 않을 경우 {@link ProductNotFoundException}을 발생시킵니다.</p>
     *
     * <p>조회된 상품은 {@link ProductSummaryResponse} 형태로 변환되어
     * 상품명과 가격 등의 요약 정보만 반환됩니다.</p>
     *
     * @param productId 조회할 상품의 식별자
     * @return 상품의 요약 정보 DTO
     * @throws ProductNotFoundException 지정된 {@code productId}에 해당하는 상품이 존재하지 않는 경우
     * @since 1.0
     */
    @Override
    public ProductSummaryResponse findSummaryByProductId(String productId) {
        Product p = repo.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return new ProductSummaryResponse(
                p.getProductId(),
                p.getName(),
                p.getPrice()
        );
    }


}
