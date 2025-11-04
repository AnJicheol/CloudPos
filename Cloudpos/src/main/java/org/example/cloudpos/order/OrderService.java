package org.example.cloudpos.order;

public interface OrderService {
    // 전체 장바구니 만들기/저장
    void create(String orderId);

    // 전체 삭제
    void delete(String orderId);

    /** 상품 추가 (있으면 수량만 늘리게 할 수도 있고, 없으면 추가) */
    void addItem(String orderId, String productId);

    /** 상품 id로 수량 업데이트 */
    void updateItemQuantity(String cartId, String productId, int quantity);
}
