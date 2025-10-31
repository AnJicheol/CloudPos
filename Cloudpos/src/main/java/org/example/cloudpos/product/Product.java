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
 * 상품의 이름, 가격, 상태 등의 정보를 관리합니다.
 * JPA 엔티티로서 데이터베이스와 직접 연동됩니다.</p>
 *
 * <p>유효성 검증(Bean Validation) 어노테이션을 통해
 * 상품명과 가격의 제약 조건을 명시적으로 표현합니다.</p>
 *
 * <ul>
 *   <li>{@link #name} — 공백 불가 (@NotBlank)</li>
 *   <li>{@link #price} — 0 이상 (@Min(0))</li>
 *   <li>{@link #status} — {@link ProductStatus} 열거형 사용</li>
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

    /**
     * 기본 키 (Primary Key).
     * <p>자동 증가 전략({@link GenerationType#IDENTITY})을 사용하며,
     * 데이터베이스에서 생성된 식별자 값을 사용합니다.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 상품명.
     * <p>공백이 아닌 값이어야 하며, {@code null} 또는 빈 문자열은 허용되지 않습니다.</p>
     *
     * @see NotBlank
     */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /**
     * 상품 가격 (원 단위).
     * <p>0 이상 정수만 허용되며, 음수는 유효하지 않습니다.</p>
     *
     * @see Min
     */
    @Min(0)
    @Column(nullable = false)
    private int price;

    /**
     * 상품의 현재 상태.
     * <p>{@link ProductStatus} 열거형으로 관리되며,
     * 문자열(EnumType.STRING)로 데이터베이스에 저장됩니다.</p>
     * <p>기본값은 {@link ProductStatus#ACTIVE}입니다.</p>
     *
     * @see ProductStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProductStatus status = ProductStatus.ACTIVE;
}
