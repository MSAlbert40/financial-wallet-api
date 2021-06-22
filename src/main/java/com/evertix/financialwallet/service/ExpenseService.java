package com.evertix.financialwallet.service;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.ExpenseRequest;
import org.springframework.http.ResponseEntity;

public interface ExpenseService {
    ResponseEntity<MessageResponse> addExpense(ExpenseRequest expense, String typeExpenseName);
    ResponseEntity<MessageResponse> deleteExpense(Long expenseId);
}
