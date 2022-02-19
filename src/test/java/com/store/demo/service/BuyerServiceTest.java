package com.store.demo.service;

import com.store.demo.entity.CheckoutEntity;
import com.store.demo.entity.ProductEntity;
import com.store.demo.model.Checkout;
import com.store.demo.repository.CheckoutRepository;
import com.store.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {
    @InjectMocks
    private BuyerService buyerService;

    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private ProductRepository productRepository;
    @Captor
    private ArgumentCaptor<CheckoutEntity> checkoutEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;

    @Test
    public void addProducts_in_empty_bucket() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductCode("A23");
        productEntity.setQuantity(6l);
        productEntity.setPrice(new BigDecimal("2.50"));
        when(productRepository.findByProductCode(any())).thenReturn(productEntity);
        ResponseEntity<Boolean> response = buyerService.addProducts(getCheckoutDetails(119l, "A23", 5l));
        assertTrue(response.getBody() == Boolean.TRUE);
    }

    @Test
    public void addProducts_when_product_is_already_present() {
        //product stock by owner
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductCode("A23");
        productEntity.setQuantity(6l);
        productEntity.setPrice(new BigDecimal("2.50"));

        //existing product A23 in bucket
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setQuantity(1l);

        when(productRepository.findByProductCode(any())).thenReturn(productEntity);
        when(checkoutRepository.findByUserIdAndProductCode(any(), any())).thenReturn(checkoutEntity);
        ResponseEntity<Boolean> response = buyerService.addProducts(getCheckoutDetails(119l, "A23", 5l));
        verify(checkoutRepository).saveAndFlush(checkoutEntityArgumentCaptor.capture());
        verify(productRepository).saveAndFlush(productEntityArgumentCaptor.capture());

        assertTrue(response.getBody() == Boolean.TRUE);
        assertTrue(checkoutEntityArgumentCaptor.getValue().getQuantity() == 6l); // 1 existing + 5 more added, total=6l
        assertTrue(productEntityArgumentCaptor.getValue().getQuantity() == 1l); // 5 product added into bucket from stock, hence stock would be 6-5=1
    }

    @Test
    public void addProducts_when_product_is_not_available() {
        when(productRepository.findByProductCode(any())).thenReturn(null);
        ResponseEntity<Boolean> response = buyerService.addProducts(getCheckoutDetails(119l, "A23", 5l));
        assertTrue(response.getBody() == Boolean.FALSE);
    }

    @Test
    public void addProducts_when_Stock_is_less() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductCode("A23");
        productEntity.setQuantity(4l);
        productEntity.setPrice(new BigDecimal("2.50"));
        when(productRepository.findByProductCode(any())).thenReturn(productEntity);
        ResponseEntity<Boolean> response = buyerService.addProducts(getCheckoutDetails(119l, "A23", 5l));
        assertTrue(response.getBody() == Boolean.FALSE);
    }

    @Test
    public void deleteProduct_when_typical() {
        //product stock by owner
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductCode("A23");
        productEntity.setQuantity(6l);
        productEntity.setPrice(new BigDecimal("2.50"));

        //existing product A23 in bucket
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setQuantity(5l);
        when(productRepository.findByProductCode(any())).thenReturn(productEntity);
        when(checkoutRepository.findByUserIdAndProductCode(any(), any())).thenReturn(checkoutEntity);
        ResponseEntity<Boolean> response = buyerService.deleteProduct(getCheckoutDetails(119l, "A23", 5l));
        assertTrue(response.getBody() == Boolean.TRUE);
    }

    @Test
    public void deleteProduct_when_bucket_has_than_deletion_request() {
        //existing product A23 in bucket
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setQuantity(4l);
        when(checkoutRepository.findByUserIdAndProductCode(any(), any())).thenReturn(checkoutEntity);
        ResponseEntity<Boolean> response = buyerService.deleteProduct(getCheckoutDetails(119l, "A23", 5l));
        assertTrue(response.getBody() == Boolean.FALSE);
    }

    @Test
    public void getTotalPrice_when_typical() {
        when(productRepository.findByProductCode("A23")).thenReturn(getProductEntityDetail("A23", new BigDecimal("6.00"), BuyerService.BUY_2_GET_50_PERCENT_OFF_ON_3));
        when(productRepository.findByProductCode("B13")).thenReturn(getProductEntityDetail("B13", new BigDecimal("4.20"), ""));
        when(checkoutRepository.findByUserId(any())).thenReturn(Arrays.asList(getCheckoutEntityDetails(119l, "A23", 3l), getCheckoutEntityDetails(119l, "B13", 1l)));
        ResponseEntity<Checkout> response = buyerService.getCheckoutDetail(119l);
        assertTrue(response.getStatusCode() == HttpStatus.OK);
        assertTrue(response.getBody().getTotalPrice().equals(new BigDecimal("19.20")));
    }

    @Test
    public void getTotalPrice_when_no_product_in_bucket() {
        ResponseEntity<Checkout> response = buyerService.getCheckoutDetail(119l);
        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);

    }

    private ProductEntity getProductEntityDetail(String productCode, BigDecimal price, String discount) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductCode(productCode);
        productEntity.setPrice(price);
        productEntity.setDiscountDeal(discount);
        return productEntity;
    }

    private Checkout getCheckoutDetails(Long userId, String ProductCode, Long quantity) {
        Map<String, Long> productCodeAndQuantity = new HashMap<>();
        productCodeAndQuantity.put(ProductCode, quantity);
        Checkout checkout = Checkout.builder()
                .userId(userId)
                .productCodeAndQuantity(productCodeAndQuantity)
                .build();
        return checkout;
    }

    private CheckoutEntity getCheckoutEntityDetails(Long userId, String ProductCode, Long quantity) {
        Map<String, Long> productCodeAndQuantity = new HashMap<>();
        productCodeAndQuantity.put(ProductCode, quantity);
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setQuantity(quantity);
        checkoutEntity.setProductCode(ProductCode);
        checkoutEntity.setUserId(userId);
        return checkoutEntity;
    }
}