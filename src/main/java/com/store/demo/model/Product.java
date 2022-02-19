package com.store.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class Product {
    private String productCode;
    private String desc;
    private BigDecimal price;
    private String currency;
    private Long quantity;
    private String discountDeal;
    private String bundleDeal;
}
