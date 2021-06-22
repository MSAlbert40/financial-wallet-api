package com.evertix.financialwallet.model.request;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ExpenseRequest {
    @Column(unique = true)
    @NotNull(message = "Period Rate cannot be null")
    @NotBlank(message = "Period Rate cannot be blank")
    @Size(max = 50)
    private String reason;

    @NotNull(message = "Period Rate cannot be null")
    @NotBlank(message = "Period Rate cannot be blank")
    @Size(max = 30)
    private String typeValue;

    @DecimalMin(value = "0.00")
    @Digits(integer = 2, fraction = 2)
    private BigDecimal value;
}
