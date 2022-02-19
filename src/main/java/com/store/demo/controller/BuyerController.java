package com.store.demo.controller;

import com.store.demo.model.Checkout;
import com.store.demo.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BuyerController {
    @Autowired
    private BuyerService buyerService;

    @PostMapping("/checkout")
    public ResponseEntity<Boolean> addProducts(@RequestBody Checkout checkout) {
        return buyerService.addProducts(checkout);
    }

    @DeleteMapping("/checkout")
    public ResponseEntity<Boolean> deleteProduct(@RequestBody Checkout checkout) {
        return buyerService.deleteProduct(checkout);
    }

    @GetMapping("/checkout/{userId}")
    public ResponseEntity<Checkout> getTotalPrice(@PathVariable Long userId) {
        return buyerService.getCheckoutDetail(userId);
    }
}
