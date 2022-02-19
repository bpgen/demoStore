package com.store.demo.repository;

import com.store.demo.entity.CheckoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutRepository extends JpaRepository<CheckoutEntity, Long> {
    CheckoutEntity findByUserIdAndProductCode(Long userId, String productCode);

    List<CheckoutEntity> findByUserId(Long userId);
}
