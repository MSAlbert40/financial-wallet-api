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

@Entity
@Table(name = "expenses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Expense implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Period Rate cannot be null")
    @NotBlank(message = "Period Rate cannot be blank")
    @Size(max = 50)
    private String reason;

    @NotNull(message = "Period Rate cannot be null")
    @NotBlank(message = "Period Rate cannot be blank")
    @Size(max = 30)
    private String typeValue;

    @DecimalMin(value = "0.00")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "typeExpense_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TypeExpense typeExpense;

    public Expense(String reason, String typeValue, BigDecimal value) {
        this.reason = reason;
        this.typeValue = typeValue;
        this.value = value;
    }
}
