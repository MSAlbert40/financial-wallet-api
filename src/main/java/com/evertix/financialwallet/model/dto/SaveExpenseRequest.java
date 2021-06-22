package com.evertix.financialwallet.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SaveExpenseRequest {
    private String reason;
    private String typeValue;
    private BigDecimal value;
    private String typeExpense;
}
