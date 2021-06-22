package com.evertix.financialwallet.repository;

import com.evertix.financialwallet.model.Expense;
import com.evertix.financialwallet.model.enums.EExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByTypeExpenseName(EExpense name);
    Page<Expense> findAllByTypeExpenseName(EExpense name, Pageable pageable);
}
