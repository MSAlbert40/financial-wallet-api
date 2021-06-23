package com.evertix.financialwallet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")
@NoArgsConstructor
@Getter
@Setter
public class Wallet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Currency cannot be null")
    @NotBlank(message = "Currency cannot be blank")
    @Size(max = 30)
    private String currency;

    @Max(value = 9999)
    private Integer daysTotalPeriod;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueTotalReceived;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal valueTotalDelivered;

    @DecimalMin(value = "0.00")
    @Digits(integer = 4, fraction = 7)
    private BigDecimal valueTCEA;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "typeWallet_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TypeWallet typeWallet;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "enterprise_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Enterprise enterprise;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rade_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rate rate;

    @OneToMany(mappedBy = "wallet")
    @JsonIgnore
    private List<Expense> expenses;

    @OneToMany(mappedBy = "wallet")
    @JsonIgnore
    private List<Discount> discounts;
}
