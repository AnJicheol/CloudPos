package org.example.cloudpos.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 상품 엔티티에 대한 데이터 접근을 담당하는 JPA 리포지토리 인터페이스입니다.
 *
 * <p>기본적으로 {@link JpaRepository} 를 상속하여
 * CRUD, 페이징, 정렬 등의 표준 메서드를 제공합니다.</p>
 *
 * <h3>기본 제공 메서드 예시</h3>
 * <ul>
 *     <li>{@code save(entity)} — 저장/수정</li>
 *     <li>{@code findById(id)} — 단건 조회</li>
 *     <li>{@code findAll()} — 전체 조회</li>
 *     <li>{@code deleteById(id)} — 삭제</li>
 *     <li>{@code existsById(id)} — 존재 여부 검사</li>
 * </ul>
 *
 * <h3>커스텀 조회 메서드</h3>
 * <ul>
 *     <li>{@link #existsByProductId(String)} — 비즈니스용 productId 중복 여부 확인</li>
 *     <li>{@link #findByProductId(String)} — productId 기준 단건 조회</li>
 *     <li>{@link #findByStatusNot(ProductStatus, Pageable)} — 특정 상태를 제외한 상품 목록 조회 (페이징)</li>
 * </ul>
 *
 * @author Esther
 * @since 1.0
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 주어진 productId가 이미 존재하는지 여부를 확인합니다.
     *
     * <p>상품 생성 시 중복 검증에 사용됩니다.</p>
     *
     * @param productId 비즈니스 상품 코드
     * @return 존재하면 {@code true}, 아니면 {@code false}
     */
    boolean existsByProductId(String productId);

    /**
     * productId를 기준으로 상품을 조회합니다.
     *
     * @param productId 비즈니스 상품 코드
     * @return 조회 결과. 존재하지 않을 경우 {@link Optional#empty()}
     */
    Optional<Product> findByProductId(String productId);

    /**
     * 지정된 상태를 제외한 상품 목록을 페이지 단위로 조회합니다.
     *
     * <p>예를 들어, {@code ProductStatus.ARCHIVED} 를 제외하여
     * 활성 상품만 조회할 때 사용됩니다.</p>
     *
     * @param status 제외할 상품 상태
     * @param pageable 페이지 요청 정보 (페이지 번호, 크기, 정렬 조건 등)
     * @return 조건에 해당하는 상품 목록 페이지 객체
     */
    Page<Product> findByStatusNot(ProductStatus status, Pageable pageable);
}
