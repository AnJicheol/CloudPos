    package org.example.cloudpos.cart.exception;

    /**
     * <h2>InvalidCartStateException</h2>
     *
     * 장바구니(Cart)의 상태가 유효하지 않거나,
     * 요청된 작업을 수행할 수 없는 상태일 때 발생하는 예외입니다.
     *
     * <p>예: 결제 가능 상태가 아닌데 결제를 시도하거나,
     * 이미 닫힌 장바구니를 다시 수정하려는 경우 등</p>
     */
    public class InvalidCartStateException extends RuntimeException {
        public InvalidCartStateException(String message) {
            super(message);
        }
    }