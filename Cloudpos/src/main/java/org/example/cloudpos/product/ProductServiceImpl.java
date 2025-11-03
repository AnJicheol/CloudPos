package org.example.cloudpos.product;

import lombok.RequiredArgsConstructor;
import org.example.cloudpos.product.dto.ProductCreateRequest;
import org.example.cloudpos.product.dto.ProductResponse;
import org.example.cloudpos.product.dto.ProductUpdateRequest;
import org.example.cloudpos.product.exception.DuplicateProductIdException;
import org.example.cloudpos.product.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

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
 *     <li>{@code productId} — 비즈니스용 상품 코드 (자동 또는 사용자 입력)</li>
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
     * <p>{@code productId} 가 요청에서 생략된 경우 서버에서 자동 생성되며,
     * 존재하는 경우에는 {@code 중복 여부} 를 확인합니다.</p>
     *
     * @param req 상품 생성 요청 DTO
     * @return 생성된 상품의 DB 기본 키(id)
     * @throws DuplicateProductIdException productId가 이미 존재할 경우
     */
    @Override
    public Long create(ProductCreateRequest req) {
        String pid = (req.productId() == null || req.productId().isBlank())
                ? generateProductId()
                : req.productId();

        if (repo.existsByProductId(pid)) {
            throw new DuplicateProductIdException(pid);
        }

        Product p = new Product();
        p.setProductId(pid);
        p.setName(req.name());
        p.setPrice(req.price());
        p.setStatus(req.status() != null ? req.status() : ProductStatus.ACTIVE);

        return repo.save(p).getId();
    }

    /**
     * 상품을 ID로 조회합니다.
     *
     * @param id 상품의 DB 기본 키
     * @return 상품 정보를 담은 응답 DTO
     * @throws ProductNotFoundException 조회 대상이 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    @Override
    public ProductResponse get(Long id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return new ProductResponse(p.getId(), p.getProductId(), p.getName(), p.getPrice(), p.getStatus());
    }

    /**
     * 상품을 소프트 삭제(ARCHIVED 상태 전환)합니다.
     *
     * <p>실제 DB에서 삭제하지 않고 상태만 변경하며,
     * 트랜잭션 종료 시 JPA Dirty Checking에 의해 자동으로 UPDATE 됩니다.</p>
     *
     * @param id 상품의 DB 기본 키
     * @throws ProductNotFoundException 삭제 대상이 존재하지 않을 경우
     */
    @Override
    public void archive(Long id) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        p.setStatus(ProductStatus.ARCHIVED);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> list(Pageable pageable) {
        return repo.findByStatusNot(ProductStatus.ARCHIVED, pageable)
                .map(p -> new ProductResponse(p.getId(), p.getProductId(), p.getName(), p.getPrice(), p.getStatus()));
    }

    @Override
    public void update(Long id, ProductUpdateRequest req) {
        Product p = repo.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        if (req.name() != null) {
            p.setName(req.name());
        }
        if (req.price() != null) {
            p.setPrice(req.price());
        }
        if (req.status() != null) {
            p.setStatus(req.status());
        }
    }

    /**
     * 비즈니스용 상품 식별자(Product ID)를 자동 생성합니다.
     *
     * <p>예시 형식: {@code P-2025-8F91ACD2}</p>
     * <ul>
     *     <li>{@code P-} — 상품 도메인 prefix</li>
     *     <li>{@code YYYY} — 생성 연도</li>
     *     <li>{@code 랜덤 8자리 UUID} — 중복 방지</li>
     * </ul>
     *
     * @return 생성된 상품 코드 문자열
     */
    private String generateProductId() {
        return "P-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
