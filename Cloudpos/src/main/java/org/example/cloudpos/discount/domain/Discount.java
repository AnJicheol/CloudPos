package org.example.cloudpos.discount.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * {@code Discount} 엔티티는 할인 정보를 데이터베이스에 저장하기 위한 도메인 클래스입니다.
 * <p>
 * 이 클래스는 할인 ID, 할인명, 할인 금액, 그리고 유효 기간(시작일 및 종료일)을 포함합니다.
 *
 * <ul>
 *   <li>할인 ID는 중복되지 않도록 고유하게 설정됩니다.</li>
 *   <li>할인 금액은 정수형으로, 단위는 원(₩)입니다.</li>
 *   <li>할인 시작일과 종료일은 {@link LocalDateTime}으로 관리되며, JSON 직렬화 시
 *       {@code yyyy-MM-dd HH:mm:ss} 형식으로 변환됩니다.</li>
 * </ul>
 *
 * <p>이 클래스는 JPA {@link Entity}로 매핑되며, Lombok을 이용하여
 * Getter, Setter, 기본 생성자를 자동 생성합니다.</p>
 *
 * @author
 * @since 2025-11
 */
@Entity
@Table(
        name = "discounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"discountId", "inventoryId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Discount {

    /**
     * 데이터베이스에서 자동 생성되는 기본 키 (PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 고유한 할인 식별자.
     * <p>예: "DISC001", "EMPLOYEE2025"</p>
     * <ul>
     *   <li>중복 불가</li>
     *   <li>Null 불가</li>
     * </ul>
     */
    @Column(nullable = false, name = "discount_id", length = 26)
    private String discountId;

    /**
     * 이게 없으면 전 세계 매장의 할인 정보를 불러오게 되어버림
     */
    @Column(nullable = false, name = "inventory_id")
    private String inventoryId;

    /**
     * 어느 제품에 할인이 적용되는지 알기 위해
     */
    @Column(nullable = false, name = "product_id")
    private String productId;



    /**
     * 할인 이름.
     * <p>예: "오픈기념", "직원할인" 등</p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * 할인 금액 (단위: 원).
     * <p>예: 5000 → 5000원 할인</p>
     */
    @Column(nullable = false)
    private Integer amount;

    /**
     * 할인 시작일시.
     * <p>할인 적용이 가능한 기간의 시작 시점입니다.</p>
     * <p>자동 생성 필드로, 생성 시점이 기록됩니다.</p>
     *
     * <p>JSON 직렬화 시 형식: {@code yyyy-MM-dd HH:mm:ss}</p>
     */
    @CreatedDate
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountStart;

    /**
     * 할인 종료일시.
     * <p>할인 적용이 가능한 기간의 종료 시점입니다.</p>
     *
     * <p>JSON 직렬화 시 형식: {@code yyyy-MM-dd HH:mm:ss}</p>
     */
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountEnd;

    public Discount(String discountId, String inventoryId, String productId, String name, Integer amount, LocalDateTime discountStart, LocalDateTime discountEnd) {
        this.discountId = discountId;
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.name = name;
        this.amount = amount;
        this.discountStart = discountStart;
        this.discountEnd = discountEnd;
    }
}
