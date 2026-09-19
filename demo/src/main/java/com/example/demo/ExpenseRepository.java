package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    List<Expense> findByUserId(Long userId);
    List<Expense> findByCategoryId(Long categoryId);
}
