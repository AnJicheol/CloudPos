package org.example.cloudpos.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Order implements Serializable {
    private String orderId;
    private List<String> productList;

}
