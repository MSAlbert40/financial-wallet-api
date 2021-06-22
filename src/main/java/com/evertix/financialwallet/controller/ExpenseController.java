package com.evertix.financialwallet.controller;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.ExpenseRequest;
import com.evertix.financialwallet.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@Tag(name = "Expense", description = "API is Ready")
@RequestMapping("api/expense")
@RestController
public class ExpenseController {
    @Autowired
    ExpenseService expenseService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add Expense", description = "Add Expense",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> add(@RequestBody @Valid ExpenseRequest expense,
                                               @RequestParam String typeExpense) {
        return this.expenseService.addExpense(expense, typeExpense);
    }

    @DeleteMapping("/{expenseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete Expense", description = "Delete Expense",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> delete(@PathVariable Long expenseId) {
        return this.expenseService.deleteExpense(expenseId);
    }
}
