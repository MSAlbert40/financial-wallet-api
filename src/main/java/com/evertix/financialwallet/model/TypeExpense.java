package com.evertix.financialwallet.model;

import com.evertix.financialwallet.model.enums.EExpense;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "type_expenses")
@NoArgsConstructor
@Getter
@Setter
public class TypeExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EExpense name;

    public TypeExpense(EExpense name) { this.name = name; }
}
