package com.store.demo.service;

import com.store.demo.entity.ProductEntity;
import com.store.demo.exception.StoreException;
import com.store.demo.model.Product;
import com.store.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StoreOwnerServiceTest {

    @InjectMocks
    private StoreOwnerService storeOwnerService;

    @Mock
    private ProductRepository productRepository;

    @Captor
    private ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;

    @Test
    public void createProduct_when_typical() {
        ResponseEntity<Boolean> response = storeOwnerService.createProduct(getProductDetails("A23", 5l, "2.5"));
        assertTrue(response.getBody() == Boolean.TRUE);
    }

    @Test
    public void createProduct_when_database_exception() {
        when(productRepository.saveAndFlush(any())).thenThrow(new StoreException("Db issue!"));
        ResponseEntity<Boolean> response = storeOwnerService.createProduct(getProductDetails("A23", 5l, "2.5"));
        assertTrue(response.getBody() == Boolean.FALSE);
    }


    @Test
    public void getProduct_when_typical() {
        when(productRepository.findByProductCode(any())).thenReturn(getProductEntityDetails("A23", 5l, "2.5"));
        ResponseEntity<Product> response = storeOwnerService.getProduct("A23");
        assertTrue(response.getBody().getQuantity() == 5l);
    }

    @Test
    public void getProduct_no_such_product() {
        ResponseEntity<Product> response = storeOwnerService.getProduct("A23");
        assertTrue(response.getBody() == null);
    }


    @Test
    public void deleteProduct_when_typical() {
        when(productRepository.findByProductCode(any())).thenReturn(getProductEntityDetails("A23", 5l, "2.5"));
        ResponseEntity<Boolean> response = storeOwnerService.deleteProduct("A23");
        assertTrue(response.getBody() == Boolean.TRUE);
    }

    @Test
    public void deleteProduct_when_no_product_to_delete() {
        ResponseEntity<Boolean> response = storeOwnerService.deleteProduct("A23");
        assertTrue(response.getBody() == Boolean.FALSE);
    }

    @Test
    public void amendProduct() {
        when(productRepository.findByProductCode(any())).thenReturn(getProductEntityDetails("A23", 5l, "2.5"));
        ResponseEntity<Boolean> response = storeOwnerService.amendProduct(getProductDetails("A23", 5l, "2.6"));
        assertTrue(response.getBody() == Boolean.TRUE);
        verify(productRepository).saveAndFlush(productEntityArgumentCaptor.capture());
        assertTrue(productEntityArgumentCaptor.getValue().getPrice().equals( new BigDecimal("2.6")));

    }

    private Product getProductDetails(String productCode, Long quantity, String price) {
        Product product = Product.builder()
                .productCode(productCode)
                .quantity(quantity)
                .price(new BigDecimal(price))
                .build();
        return product;
    }

    private ProductEntity getProductEntityDetails(String productCode, Long quantity, String price) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(1l);
        productEntity.setProductCode(productCode);
        productEntity.setQuantity(quantity);
        productEntity.setPrice(new BigDecimal(price));
        return productEntity;
    }
}