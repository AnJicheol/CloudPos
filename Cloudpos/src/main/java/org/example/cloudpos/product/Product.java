package org.example.cloudpos.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 정보를 나타내는 도메인 엔티티 클래스입니다.
 *
 * <p>이 클래스는 {@code products} 테이블과 매핑되며,
 * 상품의 기본 정보(식별자, 이름, 가격, 상태 등)를 관리합니다.
 * JPA 엔티티로서 데이터베이스와 직접 연동됩니다.</p>
 *
 * <h3>식별자 설계</h3>
 * <ul>
 *     <li>{@code id} — 데이터베이스 기본 키(PK). 자동 증가 값. 외부 노출용 아님</li>
 *     <li>{@code productId} — 비즈니스용 상품 코드. 외부 API/다른 도메인과 연동 시 사용</li>
 * </ul>
 *
 * <p>{@code productId}는 유니크 제약이 적용되어 있으며,
 * 값이 없을 경우 서비스 계층에서 자동 생성됩니다.</p>
 *
 * <h3>유효성 검증</h3>
 * <ul>
 *   <li>{@link #name} — 공백 불가 ({@link NotBlank})</li>
 *   <li>{@link #price} — 0 이상 ({@link Min})</li>
 *   <li>{@link #status} — {@link ProductStatus} enum 저장 (기본값: ACTIVE)</li>
 * </ul>
 *
 * @author Esther
 * @since 1.0
 */
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    /** 내부용 PK (자동 증가) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 상품명 (필수, 공백 불가) */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /** 가격(원 단위, 0 이상 정수) */
    @Min(0)
    @Column(nullable = false)
    private int price;

    /** 상품 상태 (기본값 ACTIVE) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductStatus status = ProductStatus.ACTIVE;

    /**
     * 비즈니스용 상품 코드.
     *
     * <p>외부 시스템/다른 도메인(API, 주문, 재고 등)에서 참조할 때 사용됩니다.
     * 사용자 입력 또는 서버 자동 생성 방식 모두 지원합니다.</p>
     *
     * <p>DB 제약: UNIQUE + NOT NULL</p>
     */
    @Column(name = "product_id", unique = true, nullable = false, length = 50)
    private String productId;
}
