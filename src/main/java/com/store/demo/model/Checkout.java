package com.store.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
public class Checkout {
    private Long userId;
    private Map<String, Long> productCodeAndQuantity;
    private BigDecimal totalPrice;
    private String bundleDeal;

}
