package com.evertix.financialwallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Wallet wallet;

    public Discount(LocalDate initialAt, LocalDate expirationAt, BigDecimal valueNominal, BigDecimal retention) {
        this.initialAt = initialAt;
        this.expirationAt = expirationAt;
        this.valueNominal = valueNominal;
        this.retention = retention;
    }
}
