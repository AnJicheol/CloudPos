package org.example.cloudpos.product.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 정보를 나타내는 도메인 엔티티 클래스입니다.
 *
 * <p>이 클래스는 {@code products} 테이블과 매핑되며,
 * 상품의 기본 정보(식별자, 이름, 가격, 상태, 대표이미지 등)를 관리합니다.
 * JPA 엔티티로서 데이터베이스와 직접 연동됩니다.</p>
 *
 * <h3>식별자 설계</h3>
 * <ul>
 *     <li>{@code id} — 데이터베이스 기본 키(PK). 자동 증가 값. 외부 노출용 아님</li>
 *     <li>{@code productId} — 비즈니스용 상품 코드(ULID 기반). 외부 API 및 다른 도메인과 연동 시 사용</li>
 * </ul>
 *
 * <p>{@code productId}는 고유(UNIQUE) 제약이 적용되며,
 * 값이 비어 있을 경우 서비스 계층에서 ULID 기반 문자열로 자동 생성됩니다.</p>
 *
 * <h3>유효성 검증</h3>
 * <ul>
 *   <li>{@link #name} — 공백 불가 ({@link NotBlank})</li>
 *   <li>{@link #price} — 0 이상 ({@link Min})</li>
 *   <li>{@link #status} — {@link ProductStatus} enum 저장 (기본값: ACTIVE)</li>
 * </ul>
 *
 * <h3>이미지 관리</h3>
 *  * <ul>
 *  *   <li>{@link #imageUrl} — 상품의 대표 이미지 URL. 선택 입력 필드</li>
 *  *   <li>서버는 URL 존재 여부를 검증하지 않으며, 문자열만 저장합니다.</li>
 *  *   <li>예: {@code https://cdn.example.com/images/americano.jpg}</li>
 *  * </ul>
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
     * <p>외부 시스템(주문, 재고 등)에서 참조할 때 사용되는 고유한 문자열 ID입니다.
     * ULID 기반으로 서비스 계층에서 자동 생성됩니다.</p>
     *
     * <p>DB 제약 조건: UNIQUE + NOT NULL</p>
     */
    @Column(name = "product_id", unique = true, nullable = false, length = 26)
    private String productId;

    /**
     * 대표 이미지 URL.
     *
     * <p>CDN, S3, 또는 외부 저장소에 업로드된 상품 이미지를 가리키며,
     * 존재하지 않아도 무방합니다.</p>
     *
     * <p>예: {@code https://cdn.example.com/images/americano.jpg}</p>
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
