package com.store.demo.service;

import com.store.demo.entity.CheckoutEntity;
import com.store.demo.entity.ProductEntity;
import com.store.demo.exception.StoreException;
import com.store.demo.model.Checkout;
import com.store.demo.repository.CheckoutRepository;
import com.store.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BuyerService {
    public static final String BUY_1_GET_25_PERCENT_OFF_ON_2 = "buy 1 get 25% off the second";
    public static final String BUY_2_GET_50_PERCENT_OFF_ON_3 = "buy 2 get 50% off the third";

    @Autowired
    private CheckoutRepository checkoutRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ResponseEntity<Boolean> addProducts(Checkout checkout) {
        // for all type of product to be added
        try {
            if (!CollectionUtils.isEmpty(checkout.getProductCodeAndQuantity())) {
                checkout.getProductCodeAndQuantity().entrySet().forEach(e -> {
                    //if new product to be added - do verify we are adding product available in store and has sufficient stock
                    ProductEntity validProductEntity = productRepository.findByProductCode(e.getKey());
                    if (!(Objects.nonNull(validProductEntity) && validProductEntity.getQuantity() >= e.getValue())) {
                        throw new StoreException("Either product is not valid or less stock !");
                    }

                    CheckoutEntity checkoutEntityToAdd = checkoutRepository.findByUserIdAndProductCode(checkout.getUserId(), e.getKey());
                    if (Objects.isNull(checkoutEntityToAdd)) {
                        checkoutEntityToAdd = new CheckoutEntity();
                        checkoutEntityToAdd.setProductCode(e.getKey());
                        checkoutEntityToAdd.setQuantity(e.getValue());
                        checkoutEntityToAdd.setUserId(checkout.getUserId());
                    } else {
                        // for existing product add additional quantity
                        Long existingQuantity = checkoutEntityToAdd.getQuantity();
                        Long addedQuantity = existingQuantity + e.getValue();
                        checkoutEntityToAdd.setQuantity(addedQuantity);
                    }
                    checkoutRepository.saveAndFlush(checkoutEntityToAdd);

                    //once added into bucket, deduct the same number from product store
                    validProductEntity.setQuantity(validProductEntity.getQuantity() - e.getValue());
                    productRepository.saveAndFlush(validProductEntity);
                });
            } else {
                log.error("No product is there to add for checkout!");
                throw new StoreException("No product is there to add for checkout!");
            }

        } catch (Exception ex) {
            log.error(" product item can not be added!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Boolean.FALSE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Boolean.TRUE);
    }


    @Transactional
    public ResponseEntity<Boolean> deleteProduct(Checkout checkout) {
        try {
            if (!CollectionUtils.isEmpty(checkout.getProductCodeAndQuantity())) {
                checkout.getProductCodeAndQuantity().entrySet().forEach(e -> {
                    //verify if product is present in database first, before deleting its quantity
                    CheckoutEntity checkoutEntityToSubtract = checkoutRepository.findByUserIdAndProductCode(checkout.getUserId(), e.getKey());
                    if (Objects.nonNull(checkoutEntityToSubtract)) {
                        Long existingQuantity = checkoutEntityToSubtract.getQuantity();
                        Long currentQuantity = existingQuantity - e.getValue();
                        //verify if existing quantity is sufficient for deduction
                        if (currentQuantity >= 0) {
                            checkoutEntityToSubtract.setQuantity(currentQuantity);
                            checkoutRepository.saveAndFlush(checkoutEntityToSubtract);

                            //once deleted from bucket, add back to product store
                            ProductEntity validProductEntity = productRepository.findByProductCode(e.getKey());
                            validProductEntity.setQuantity(validProductEntity.getQuantity() + e.getValue());
                            productRepository.saveAndFlush(validProductEntity);
                        } else {
                            log.error("Existing checkout product quantity is less than the deletion number!");
                            throw new StoreException("Existing checkout product quantity is less than the deletion number!");
                        }
                    } else {
                        log.error("No product is there to be considered for deletion!");
                        throw new StoreException("No product is there to be considered for deletion!");
                    }
                });
            } else {
                log.error("No product is there to be considered for deletion!");
                throw new StoreException("No product is there to be considered for deletion!");
            }
        } catch (Exception ex) {
            log.error(" product item can not be deleted!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Boolean.FALSE);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Boolean.TRUE);

    }


    public ResponseEntity<Checkout> getCheckoutDetail(Long userId) {
        Checkout checkout;
        try {
            List<CheckoutEntity> checkoutEntities = checkoutRepository.findByUserId(userId);

            if (CollectionUtils.isEmpty(checkoutEntities)) {
                log.error("No product is there in bucket!");
                throw new StoreException("No product is there in bucket!");
            }
            Map<String, Long> productCodeAndQuantityMap = checkoutEntities.stream().collect(Collectors.toMap(CheckoutEntity::getProductCode, CheckoutEntity::getQuantity));
            StringBuilder bundleDealsDetail = new StringBuilder();
            BigDecimal totalFinalPrice = productCodeAndQuantityMap.entrySet().stream().map(e -> {
                ProductEntity validProductEntity = productRepository.findByProductCode(e.getKey());
                if (StringUtils.isNotBlank(validProductEntity.getBundleDeal())) {
                    bundleDealsDetail.append(validProductEntity.getBundleDeal()).append(",");
                }

                return getDiscountedPrice(validProductEntity, e.getValue());
            }).reduce(new BigDecimal("0.00"), (k, v) -> k.add(v));

            checkout = Checkout.builder()
                    .bundleDeal(bundleDealsDetail.toString())
                    .totalPrice(totalFinalPrice)
                    .build();
        } catch (Exception ex) {
            log.error("Checkout detail can not be retrieved!", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Checkout.builder().build());
        }
        return ResponseEntity.status(HttpStatus.OK).body(checkout);

    }

    private BigDecimal getDiscountedPrice(ProductEntity productEntity, Long quantity) {
        //Discount Rules
        switch (productEntity.getDiscountDeal()) {
            case BUY_1_GET_25_PERCENT_OFF_ON_2:
                if (quantity > 1) {
                    return calculateTotalDiscountPrice(productEntity, quantity, 4);
                }
            case BUY_2_GET_50_PERCENT_OFF_ON_3:
                if (quantity > 2) {
                    return calculateTotalDiscountPrice(productEntity, quantity, 2);
                }
            default:
                return productEntity.getPrice().multiply(BigDecimal.valueOf(quantity));
        }

    }

    private BigDecimal calculateTotalDiscountPrice(ProductEntity productEntity, Long quantity, Integer discount) {
        BigDecimal finalPrice;
        BigDecimal discountPrice = productEntity.getPrice().divide(BigDecimal.valueOf(discount));
        BigDecimal otherPrice = productEntity.getPrice().multiply(BigDecimal.valueOf(quantity - 1));
        finalPrice = discountPrice.add(otherPrice);
        return finalPrice;
    }

}