package com.evertix.financialwallet.service;

import com.evertix.financialwallet.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialService {
    Integer daysPeriod(LocalDate expiration, LocalDate discount);
    BigDecimal rateEffective(Integer daysPeriod, BigDecimal valueRate);
    BigDecimal rateEffectiveNominal(Integer daysPeriod, BigDecimal valueRate, Integer daysRate, Integer daysCapitalization);
    BigDecimal rateDiscount(BigDecimal rateEffective);
    BigDecimal valueDiscount(BigDecimal valueNominal, BigDecimal rateDiscount);
    BigDecimal expenseInitial(List<Expense> expenses, BigDecimal valueNominal);
    BigDecimal expenseFinal(List<Expense> expenses, BigDecimal valueNominal);
    BigDecimal valueNet(BigDecimal valueNominal, BigDecimal valueDiscount);
    BigDecimal valueReceived(BigDecimal valueNet, BigDecimal expenseInitial, BigDecimal retention);
    BigDecimal valueDelivered(BigDecimal valueNominal, BigDecimal expenseFinal, BigDecimal retention);
    BigDecimal TCEA(BigDecimal valueReceived, BigDecimal valueDelivered, Integer daysPeriod, Integer daysYear);
}
