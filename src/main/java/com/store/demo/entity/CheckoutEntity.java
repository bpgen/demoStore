package com.store.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "checkout")
@Getter
@Setter
public class CheckoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long checkoutId;
    private Long userId;
    private String productCode;
    private Long quantity;


}
