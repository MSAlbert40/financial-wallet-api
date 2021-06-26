package com.evertix.financialwallet.model.request;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DiscountRequest {
    @NotNull(message = "DocumentName cannot be null")
    @NotBlank(message = "DocumentName cannot be blank")
    @Size(max = 30, message = "DocumentName name must be less than 60 characters")
    private String documentName;

    @Column(nullable = false, updatable = false)
    private LocalDate initialAt;

    @Column(nullable = false, updatable = false)
    private LocalDate expirationAt;

    @DecimalMin(value = "0.00")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal valueNominal;

    @DecimalMin(value = "0.00")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal retention;
}
