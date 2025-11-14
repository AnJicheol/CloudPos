package org.example.cloudpos.order.advice;



import org.example.cloudpos.order.controller.OrderController;
import org.example.cloudpos.order.dto.OrderErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = OrderController.class)
public class OrderControllerAdvice {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<OrderErrorResponse> handleOrderDbError( DataAccessException ex){
        OrderErrorResponse body = new OrderErrorResponse(
                "ORDER_DB_ERROR",
                "주문 처리 중 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


}
