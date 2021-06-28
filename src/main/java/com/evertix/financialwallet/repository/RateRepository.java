package com.evertix.financialwallet.repository;

import com.evertix.financialwallet.model.Rate;
import com.evertix.financialwallet.model.enums.ERate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Rate findByWalletId(Long wallet);
    List<Rate> findAllByTypeRateName(ERate name);
    Page<Rate> findAllByTypeRateName(ERate name, Pageable pageable);
}
