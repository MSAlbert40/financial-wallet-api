package com.evertix.financialwallet.service;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.WalletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<MessageResponse> getAllWallet();
    ResponseEntity<MessageResponse> getWallet(Long walletId);
    ResponseEntity<MessageResponse> getAllWallet(String typeWallet, Long enterpriseId);
    ResponseEntity<MessageResponse> getAllWalletPaginated(String typeWallet, Long enterpriseId, Pageable pageable);
    ResponseEntity<MessageResponse> addWallet(WalletRequest wallet, Long enterpriseId);
}
