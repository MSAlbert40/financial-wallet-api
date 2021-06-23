package com.evertix.financialwallet.service.impl;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.controller.constants.ResponseConstants;
import com.evertix.financialwallet.model.Expense;
import com.evertix.financialwallet.model.TypeExpense;
import com.evertix.financialwallet.model.Wallet;
import com.evertix.financialwallet.model.dto.SaveExpenseRequest;
import com.evertix.financialwallet.model.enums.EExpense;
import com.evertix.financialwallet.model.request.ExpenseRequest;
import com.evertix.financialwallet.repository.ExpenseRepository;
import com.evertix.financialwallet.repository.TypeExpenseRepository;
import com.evertix.financialwallet.repository.WalletRepository;
import com.evertix.financialwallet.service.ExpenseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TypeExpenseRepository typeExpenseRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Override
    public ResponseEntity<MessageResponse> getAllExpense(String typeExpense, Long walletId) {
        try {
            // Identify Type Expense
            EExpense expense;
            if (typeExpense == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Expense not found")
                                .build());
            } else {
                expense = switch (typeExpense) {
                    case "EXPENSE_INITIAL" -> EExpense.EXPENSE_INITIAL;
                    case "EXPENSE_FINAL" -> EExpense.EXPENSE_FINAL;
                    default -> throw new RuntimeException("Sorry, Type Expense is wrong.");
                };
            }

            List<Expense> expenseList = this.expenseRepository.findAllByTypeExpenseNameAndWalletId(expense, walletId);
            if (expenseList == null || expenseList.isEmpty()) {
                return this.getNotExpenseContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(expenseList)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> getAllExpensePaginated(String typeExpense, Long walletId, Pageable pageable) {
        try {
            // Identify Type Expense
            EExpense expense;
            if (typeExpense == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Expense not found")
                                .build());
            } else {
                expense = switch (typeExpense) {
                    case "EXPENSE_INITIAL" -> EExpense.EXPENSE_INITIAL;
                    case "EXPENSE_FINAL" -> EExpense.EXPENSE_FINAL;
                    default -> throw new RuntimeException("Sorry, Type Expense is wrong.");
                };
            }

            Page<Expense> expensePage = this.expenseRepository.findAllByTypeExpenseNameAndWalletId(expense, walletId, pageable);
            if (expensePage == null || expensePage.isEmpty()) {
                return this.getNotExpenseContent();
            }
            MessageResponse response = MessageResponse.builder()
                    .code(ResponseConstants.SUCCESS_CODE)
                    .message(ResponseConstants.MSG_SUCCESS_CONS)
                    .data(expensePage)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> addExpense(ExpenseRequest expense, String typeExpenseName, Long walletId) {
        try {
            // Create New Expense
            Expense saveExpense = this.convertToEntity(expense);

            // Identify Type Expense
            TypeExpense typeExpense;
            if (typeExpenseName == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Sorry, Type Expense not found")
                                .build());
            } else {
                typeExpense = switch (typeExpenseName) {
                    case "EXPENSE_INITIAL" -> typeExpenseRepository.findByName(EExpense.EXPENSE_INITIAL)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Expense not found"));
                    case "EXPENSE_FINAL" -> typeExpenseRepository.findByName(EExpense.EXPENSE_FINAL)
                            .orElseThrow(() -> new RuntimeException("Sorry, Type Expense not found"));
                    default -> throw new RuntimeException("Sorry, Type Expense is wrong.");
                };
            }

            // Identify if Wallet Exists
            Wallet wallet = walletRepository.findById(walletId).orElse(null);
            if (wallet == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists wallet with ID: " + walletId)
                                .build());
            }

            // Set Type Expense & Wallet
            saveExpense.setTypeExpense(typeExpense);
            saveExpense.setWallet(wallet);
            // Save Expense
            expenseRepository.save(saveExpense);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful creation request")
                            .data(this.convertToResource(saveExpense))
                            .build());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    @Override
    public ResponseEntity<MessageResponse> deleteExpense(Long expenseId) {
        try {
            // Validate if Expense Exists
            Expense expense = this.expenseRepository.findById(expenseId).orElse(null);
            if (expense == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(MessageResponse.builder()
                                .code(ResponseConstants.ERROR_CODE)
                                .message("Don't exists expense with ID: " + expenseId)
                                .build());
            }

            // Delete Expense
            expenseRepository.delete(expense);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.SUCCESS_CODE)
                            .message("Successful delete")
                            .data(this.convertToResource(expense))
                            .build());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MessageResponse.builder()
                            .code(ResponseConstants.ERROR_CODE)
                            .message("Internal Error: " + sw.toString())
                            .build());
        }
    }

    private Expense convertToEntity(ExpenseRequest expense) { return modelMapper.map(expense, Expense.class); }

    private SaveExpenseRequest convertToResource(Expense expense) {
        SaveExpenseRequest resource = modelMapper.map(expense, SaveExpenseRequest.class);
        resource.setTypeExpense(expense.getTypeExpense().getName().toString());
        return resource;
    }

    private ResponseEntity<MessageResponse> getNotExpenseContent(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(MessageResponse.builder()
                        .code(ResponseConstants.WARNING_CODE)
                        .message(ResponseConstants.MSG_WARNING_CONS)
                        .data(null)
                        .build());
    }
}
