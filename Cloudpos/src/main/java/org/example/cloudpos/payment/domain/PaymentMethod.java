package org.example.cloudpos.payment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_method")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;  // 결제 수단(CARD,CASH,KAKAO_PAY)등등

    @Column(nullable = false)
    private String name; // 사용자에게 보여주기용


    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;




    public static PaymentMethod create(String code, String name) {
        return PaymentMethod.builder()
                .code(normalizeCode(code))
                .name(name.trim())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    //비즈니스 메서드
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }



    private static String normalizeCode(String raw) {
        if (raw == null) throw new IllegalArgumentException("결제수단 코드는 필수입니다.");
        return raw.trim().toUpperCase().replace(' ', '_');
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
