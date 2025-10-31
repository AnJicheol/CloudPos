package org.example.cloudpos.product;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link Product} 엔티티에 대한 데이터 접근 계층(Repository) 인터페이스입니다.
 *
 * <p>Spring Data JPA가 자동으로 구현 클래스를 생성하며,
 * {@link JpaRepository}를 상속받아 기본적인 CRUD 기능을 제공합니다.</p>
 *
 * <h3>기본 제공 메서드</h3>
 * <ul>
 *   <li>{@code save(entity)} — 엔티티 저장 또는 업데이트</li>
 *   <li>{@code findById(id)} — 기본 키(PK)로 조회</li>
 *   <li>{@code findAll()} — 모든 엔티티 조회</li>
 *   <li>{@code deleteById(id)} — ID로 삭제</li>
 *   <li>{@code count()} — 전체 개수 조회</li>
 *   <li>{@code existsById(id)} — 특정 ID 존재 여부 확인</li>
 * </ul>
 *
 * <p>필요하다면 쿼리 메서드(예: {@code findByName(String name)})나
 * {@code @Query} 어노테이션을 이용해 사용자 정의 메서드를 추가할 수 있습니다.</p>
 *
 * @see Product
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @author Esther
 * @since 1.0
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
