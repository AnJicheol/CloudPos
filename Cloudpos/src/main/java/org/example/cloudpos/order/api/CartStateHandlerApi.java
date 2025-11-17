package org.example.cloudpos.order.api;

import org.example.cloudpos.cart.dto.CartItemResponse;
import org.example.cloudpos.order.dto.CartDto;

import java.util.List;


/**
 * 주문 ID를 이용해 장바구니 모듈에 상태 변경 신호를 전달하기 위한 API.
 *
 * <p>다른 모듈이 장바구니 도메인의 내부 구현을 몰라도,
 * 주문 ID만 가지고 "장바구니를 다시 열어라/닫아라/체크아웃을 시작해라"
 * 와 같은 요청 신호를 보낼 수 있도록 하는 계층이다.</p>
 *
 * <p>실제 장바구니 상태를 어떻게 관리할지는 장바구니 도메인의 책임이며,
 * 이 인터페이스는 그저 요청을 위임하는 역할만 담당한다.</p>
 */
public interface CartStateHandlerApi {


    /**
     * 해당 주문과 연결된 장바구니를 다시 열어달라는 신호를 전파한다.
     *
     * @param orderId 장바구니와 연결된 주문의 문자열 주문 ID
     */
    void stateOpen(String orderId);


    /**
     * 해당 주문과 연결된 장바구니를 닫아달라는 신호를 전파한다.
     *
     * @param orderId 장바구니와 연결된 주문의 문자열 주문 ID
     */
    void stateClose(String orderId);


    /**
     * 해당 주문과 연결된 장바구니에 대해 체크아웃을 시작해달라는
     * 신호를 전파하고, 그 결과로 결제에 사용할 장바구니 아이템 목록을 받는다.
     *
     * @param orderId 장바구니와 연결된 주문의 문자열 주문 ID
     * @return 체크아웃 과정에서 사용할 장바구니 상품 목록
     */
    List<CartDto> statePayment(String orderId);
}
