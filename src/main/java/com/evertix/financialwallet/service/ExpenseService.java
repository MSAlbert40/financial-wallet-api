package com.evertix.financialwallet.service;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.ExpenseRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ExpenseService {
    ResponseEntity<MessageResponse> getAllExpense(String typeExpense, Long walletId);
    ResponseEntity<MessageResponse> getAllExpensePaginated(String typeExpense, Long walletId, Pageable pageable);
    ResponseEntity<MessageResponse> addExpense(ExpenseRequest expense, String typeExpenseName, Long walletId);
    ResponseEntity<MessageResponse> deleteExpense(Long expenseId);
}
