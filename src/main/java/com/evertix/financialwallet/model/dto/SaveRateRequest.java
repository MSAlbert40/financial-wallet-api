package com.evertix.financialwallet.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class SaveRateRequest {
    private Integer daysRate;
    private String periodRate;
    private BigDecimal valueRate;
    private String periodCapitalization;
    private Integer daysCapitalization;
    private LocalDate discountAt;
    private String typeRate;
}
