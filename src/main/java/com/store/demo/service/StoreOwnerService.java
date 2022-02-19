package com.store.demo.service;

import com.store.demo.entity.ProductEntity;
import com.store.demo.exception.StoreException;
import com.store.demo.model.Product;
import com.store.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class StoreOwnerService {
    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<Boolean> createProduct(Product product) {
        try {
            Optional.ofNullable(product).orElseThrow(StoreException::new);
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductCode(product.getProductCode());
            productEntity.setDesc(product.getDesc());
            productEntity.setPrice(product.getPrice());
            productEntity.setCurrency(product.getCurrency());
            productEntity.setQuantity(product.getQuantity());
            productEntity.setDiscountDeal(product.getDiscountDeal());
            productEntity.setBundleDeal(product.getBundleDeal());
            productRepository.saveAndFlush(productEntity);
        } catch (Exception ex) {
            log.error("product can not be added in a bucket!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Boolean.FALSE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE);

    }

    public ResponseEntity<Product> getProduct(String productCode) {
        Product product;
        try {
            ProductEntity productEntity = productRepository.findByProductCode(productCode);
            product = Product.builder()
                    .productCode(productEntity.getProductCode())
                    .desc(productEntity.getDesc())
                    .price(productEntity.getPrice())
                    .currency(productEntity.getCurrency())
                    .quantity(productEntity.getQuantity())
                    .discountDeal(productEntity.getDiscountDeal())
                    .bundleDeal(productEntity.getDiscountDeal())
                    .build();
        } catch (Exception ex) {
            log.error("product can not be retrieved!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }


    public ResponseEntity<Boolean> deleteProduct(String productCode) {
        try {
            ProductEntity productEntity = productRepository.findByProductCode(productCode);
            productRepository.deleteById(productEntity.getProductId());
        } catch (Exception ex) {
            log.error("product can not be deleted!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Boolean.FALSE);
        }
        return new ResponseEntity(Boolean.TRUE, HttpStatus.ACCEPTED);
    }


    public ResponseEntity<Boolean> amendProduct(Product product) {
        try {
            ProductEntity productEntity = productRepository.findByProductCode(product.getProductCode());
            if (Objects.nonNull(product.getPrice())) {
                productEntity.setPrice(product.getPrice());
            }
            if (Objects.nonNull(product.getDesc())) {
                productEntity.setDesc(product.getDesc());
            }
            if (Objects.nonNull(product.getDiscountDeal())) {
                productEntity.setDiscountDeal(product.getDiscountDeal());
            }
            if (Objects.nonNull(product.getBundleDeal())) {
                productEntity.setBundleDeal(product.getBundleDeal());
            }
            productRepository.saveAndFlush(productEntity);
        } catch (Exception ex) {
            log.error("product can not be updated!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Boolean.FALSE);
        }
        return new ResponseEntity(Boolean.TRUE, HttpStatus.ACCEPTED);

    }
}
