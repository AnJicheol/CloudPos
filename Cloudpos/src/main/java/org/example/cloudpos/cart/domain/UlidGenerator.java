package org.example.cloudpos.cart.domain;

import com.github.f4b6a3.ulid.UlidCreator;
/**

 * ULID(Universally Unique Lexicographically Sortable Identifier)를 생성하는 유틸리티 클래스입니다.
 *
 * <p><b>개요</b><br>
 * ULID는 시간 순으로 정렬 가능하며, UUID보다 짧고 가독성이 높은 식별자 형식입니다.
 * 본 클래스는 {@link com.github.f4b6a3.ulid.UlidCreator}를 이용해 ULID 문자열을 생성합니다.
 * </p>
 *
 * <p><b>특징</b></p>
 * <ul>
 * <li>정적 메서드 {@code generate()}를 통해 ULID를 문자열로 반환</li>
 * <li>시간순 정렬이 가능하여 정렬/조회 효율성이 높음</li>
 * <li>무상태(stateless) 유틸리티로, 인스턴스화가 불가능하도록 설계됨</li>
 * </ul>
 *
 * <p><b>비고</b><br>
 * 본 클래스는 장바구니, 주문 등 도메인에서 고유 식별자를 생성할 때 사용됩니다.
 * </p>

 */

public final class UlidGenerator {
    private UlidGenerator() {
    }

    public static String generate() {
        return UlidCreator.getUlid().toString();
    }
}
