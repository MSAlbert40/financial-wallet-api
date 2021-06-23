package com.evertix.financialwallet.service;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.Discount;
import com.evertix.financialwallet.model.Wallet;
import com.evertix.financialwallet.model.request.DiscountRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DiscountService extends FinancialService{
    ResponseEntity<MessageResponse> getAllDiscount();
    ResponseEntity<MessageResponse> getAllDiscount(Long walletId);
    ResponseEntity<MessageResponse> getAllDiscountPaginated(Long walletId, Pageable pageable);
    ResponseEntity<MessageResponse> addDiscount(DiscountRequest discount, Long walletId);
    void financialOperation(DiscountRequest request, Discount discount, Wallet wallet);
}
