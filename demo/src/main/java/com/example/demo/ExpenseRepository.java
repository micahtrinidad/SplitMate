package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import org.springframework.data.repository.query.Param;


public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    List<Expense> findByUserId(Long userId);
    List<Expense> findByCategoryId(Long categoryId);
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query ("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpenseAmount(@Param("userId")Long userId);
}


