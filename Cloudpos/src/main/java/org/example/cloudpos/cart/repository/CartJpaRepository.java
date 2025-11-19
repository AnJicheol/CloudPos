package org.example.cloudpos.cart.repository;

import org.example.cloudpos.cart.domain.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 장바구니 엔티티용 JPA 리포지토리.
 *
 * <p>
 * - {@link CartEntity} 를 RDB에 영속화하고 조회하는 역할을 담당한다.<br>
 * - 기본적인 CRUD 메서드는 {@link JpaRepository} 가 제공하며,<br>
 *   장바구니 외부 식별자({@code cartId}) 기반 조회 메서드를 추가로 정의한다.
 * </p>
 *
 * <p><b>주요 책임</b></p>
 * <ul>
 *     <li>장바구니 생성, 수정, 삭제 (기본 JPA 기능)</li>
 *     <li>{@code cartId} 기준 단건 조회</li>
 * </ul>
 */
public interface CartJpaRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByCartId(String cartId);
}