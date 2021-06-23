package com.evertix.financialwallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "discounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Discount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Max(value = 9999)
    private Integer daysPeriod;

    @DecimalMin(value = "0.00")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal rateEffective;

    @DecimalMin(value = "0.00")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal rateDiscount;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueDiscount;

    @DecimalMin(value = "0.00")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal expenseInitial;

    @DecimalMin(value = "0.00")
    @Digits(integer = 5, fraction = 2)
    private BigDecimal expenseFinal;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueNet;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueReceived;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueDelivered;

    @DecimalMin(value = "0.00")
    @Digits(integer = 4, fraction = 7)
    private BigDecimal TCEA;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "discounts")
    @JsonIgnore
    private List<Wallet> wallets;

    public Discount(LocalDate initialAt, LocalDate expirationAt, BigDecimal valueNominal, BigDecimal retention) {
        this.initialAt = initialAt;
        this.expirationAt = expirationAt;
        this.valueNominal = valueNominal;
        this.retention = retention;
    }
}
