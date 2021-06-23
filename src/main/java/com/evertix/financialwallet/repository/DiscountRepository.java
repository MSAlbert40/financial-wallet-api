package com.evertix.financialwallet.repository;

import com.evertix.financialwallet.model.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findAllByWalletId(Long walletId);
    Page<Discount> findAllByWalletId(Long walletId, Pageable pageable);
}
