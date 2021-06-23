package com.evertix.financialwallet.controller;

import com.evertix.financialwallet.controller.commons.MessageResponse;
import com.evertix.financialwallet.model.request.ExpenseRequest;
import com.evertix.financialwallet.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "View All Expense by Type Expense and Wallet", description = "View All Expense by Type Expense and Wallet",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> getAll(@RequestParam String typeExpense,
                                                  @RequestParam Long walletId) {
        return this.expenseService.getAllExpense(typeExpense, walletId);
    }

    @GetMapping("/paged")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "View All Expense paginated by Type Expense and Wallet", description = "View All Expense paginated by Type Expense and Wallet",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY
                            , description = "Page you want to retrieve (0..N)"
                            , name = "page"
                            , content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))),
                    @Parameter(in = ParameterIn.QUERY
                            , description = "Number of records per page."
                            , name = "size"
                            , content = @Content(schema = @Schema(type = "integer", defaultValue = "20"))),
            },
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> getAllPaginated(@PageableDefault @Parameter(hidden = true) Pageable pageable,
                                                           @RequestParam String typeExpense,
                                                           @RequestParam Long walletId) {
        return this.expenseService.getAllExpensePaginated(typeExpense, walletId, pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add Expense", description = "Add Expense",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> add(@RequestBody @Valid ExpenseRequest expense,
                                               @RequestParam String typeExpense,
                                               @RequestParam Long walletId) {
        return this.expenseService.addExpense(expense, typeExpense, walletId);
    }

    @DeleteMapping("/{expenseId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete Expense", description = "Delete Expense",
            security = @SecurityRequirement(name = "bearerAuth"), tags = {"Expense"})
    public ResponseEntity<MessageResponse> delete(@PathVariable Long expenseId) {
        return this.expenseService.deleteExpense(expenseId);
    }
}
