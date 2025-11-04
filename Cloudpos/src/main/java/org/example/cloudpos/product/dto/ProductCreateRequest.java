package org.example.cloudpos.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.example.cloudpos.product.domain.ProductStatus;

/**
 * 상품 생성 요청을 표현하는 DTO입니다.
 *
 * <p>클라이언트가 상품 생성 API 호출 시 전달하는 데이터를 담고 있으며,
 * 필수/선택 필드와 유효성 검증 규칙을 포함합니다.</p>
 *
 * <ul>
 *   <li>{@code name} — 필수 입력. 공백 불가</li>
 *   <li>{@code price} — 필수 입력. 0 이상 정수</li>
 *   <li>{@code status} — 선택 입력. 미지정 시 기본값 ACTIVE</li>
 *   <li>{@code imageUrl} — 선택 입력. 상품의 대표 이미지 URL (예: {@code https://cdn.example.com/img/coffee.jpg}).</li>
 * </ul>
 *
 * @param name 상품명 (필수, 공백 불가)
 * @param price 가격(원 단위, 0 이상)
 * @param status 상품 상태 (옵션, null이면 ACTIVE)
 * @param imageUrl 대표 이미지 URL (선택)
 * @author Esther
 * @since 1.0
 */
public record ProductCreateRequest(
        @NotBlank String name,
        @Min(0) int price,
        ProductStatus status,
        String imageUrl
) {}
