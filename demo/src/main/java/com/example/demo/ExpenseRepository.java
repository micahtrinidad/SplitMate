package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;


public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    List<Expense> findByUserId(Long userId);
    List<Expense> findByCategoryId(Long categoryId);
    List<Expense> findByUserIdAndCategoryId(long userId, Long categoryId);

    @Query ("SELECT SUM(e.amount) FROM Expense e")
    BigDecimal getTotalExpenseAmount();
}
