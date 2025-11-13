package org.example.cloudpos.cart.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 장바구니 엔티티.
 *
 * <p>
 * - 장바구니의 메타정보를 RDB에 영속화하기 위한 엔티티이다.<br>
 * - Redis에 저장된 장바구니 아이템 목록의 상태 스냅샷 역할을 수행한다.<br>
 * - 유저 식별자와 FSM(유한상태머신) 기반 상태를 함께 관리한다.
 * </p>
 *
 * <p><b>주요 컬럼</b></p>
 * <ul>
 *     <li>{@code id} — 내부 DB 식별자 (PK)</li>
 *     <li>{@code cartId} — 외부 연동용 식별자(UUID 등)</li>
 *     <li>{@code userId} — 장바구니 소유자 ID</li>
 *     <li>{@code state} — 장바구니 FSM 상태 (예: EMPTY, ACTIVE, CLOSED)</li>
 * </ul>
 *
 * <p>
 * 비즈니스 로직은 {@code CartService} 혹은 상태머신({@code CartStateMachine})을 통해 관리되며,
 * 본 엔티티는 데이터 보관 책임만 가진다.
 * </p>
 */

@Entity
@Table(name="carts")
@Getter
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="cart_id", nullable = false, unique = true, length=26)
    private String cartId;

    @Column(name="inventory_id", nullable = false)
    private Long inventoryId;

}