package com.evertix.financialwallet.repository;

import com.evertix.financialwallet.model.TypeExpense;
import com.evertix.financialwallet.model.enums.EExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeExpenseRepository extends JpaRepository <TypeExpense, Long>{
    Optional<TypeExpense> findByName(EExpense name);
}
