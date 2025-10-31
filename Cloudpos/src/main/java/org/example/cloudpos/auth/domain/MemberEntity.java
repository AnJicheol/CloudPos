package org.example.cloudpos.auth.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;



/**
 * 멤버-인벤토리 매핑을 나타내는 엔티티.
 *
 * <p>
 * 한 사용자(userId)가 어떤 인벤토리(inventoryId)를 소유/참조하고 있는지를 저장하는 테이블에 매핑됩니다.
 * 실제 {@code User} 나 {@code Inventory} 엔티티와의 JPA 연관관계를 사용하지 않고
 * 식별자(Long)만 보관하는 형태로 설계되었습니다. 이는 서비스/도메인 계층에서 느슨하게
 * 의존성을 유지하려는 의도입니다.
 * <p>
 *
 * <strong>주의:</strong> 동일한 {@code userId} 와 {@code inventoryId} 조합이 중복 저장되지 않아야 한다면
 * DB 레벨에서 복합 유니크 제약을 추가하거나, 서비스 계층에서 중복 여부를 검사해야 합니다.
 */

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "member"
)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Builder
    public MemberEntity(@NonNull Long userId, @NonNull Long inventoryId) {
        this.userId = userId;
        this.inventoryId = inventoryId;
    }
}
