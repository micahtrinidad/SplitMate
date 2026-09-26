package com.example.demo;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityReturnValueHandler;


import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    // Repositories for constructor injection
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    // Constructor injection for expense
    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        
    }

    // Handles GET expenses
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Handles POST expenses
    @PostMapping("/users/{userId}/categories/{categoryId}/expenses")
    public ResponseEntity<Expense> createExpense(
        @Valid @RequestBody Expense expense, 
        @PathVariable Long userId, 
        @PathVariable Long categoryId) {
        Optional<User> userResult = userRepository.findById(userId);
        Optional<Category> categoryResult = categoryRepository.findById(categoryId);

        if (userResult.isPresent()) { 
            if (categoryResult.isPresent()) {
                User user = userResult.get();
                Category category = categoryResult.get();
            
                expense.setUser(user);
                expense.setCategory(category);
            
                Expense savedExpense = expenseRepository.save(expense);

                return ResponseEntity.ok(savedExpense);
            }
            
        }
        
        
        return ResponseEntity.notFound().build();
    }

    // Handles GET expenses by user id and category id
    // Spring Data JPA interprets the method you invoke
    @GetMapping("/users/{userId}/categories/{categoryId}/expenses")
    public List<Expense> getExpensesByUserIdAndCategoryId(
        @PathVariable Long userId, 
        @PathVariable Long categoryId) {
        
            return expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
    
    }

    // GET expense by id
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/expenses/{id}") 
    public ResponseEntity<Expense> getExpensesById(@PathVariable Long id) {
        Optional<Expense> expenseResult = expenseRepository.findById(id);

        // check if the expense is present
        if (expenseResult.isPresent()) {
            Expense expense = expenseResult.get();

            return ResponseEntity.ok(expense);
        }

        return ResponseEntity.notFound().build();
    }

    // GET expense by userId
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/users/{id}/expenses") 
    public ResponseEntity<List<Expense>> getExpensesByUserId(@PathVariable Long id) {

        // check if the user is present
        Optional<User> userResult = userRepository.findById(id);

        // if user is present then check for their expenses
        if (userResult.isPresent()) {

            List<Expense> expenses = expenseRepository.findByUserId(id);

            return ResponseEntity.ok(expenses);
            
        }

        return ResponseEntity.notFound().build();
    }

    // GET expense amount total by userId
    // gets the total amount of expenses using a specific user id
    @GetMapping("/users/{userId}/expenses/total")
    public ResponseEntity<BigDecimal> getTotalExpenses(@PathVariable Long userId) {
        BigDecimal total = expenseRepository.getTotalExpenseAmount(userId);

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        return ResponseEntity.ok(total);
    }

    // UPDATE expense by id
    // uses @pathvariable etc.
    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> putExpenseById(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        Optional<Expense> result = expenseRepository.findById(id);
        
        if (result.isPresent()) {
            Expense existingExpense = result.get();

            existingExpense.setAmount(expense.getAmount());
            existingExpense.setDate(expense.getDate());
            existingExpense.setDescription(expense.getDescription());

            Expense savedExpense = expenseRepository.save(existingExpense);

            return ResponseEntity.ok(savedExpense);
            //return Optional.of(savedExpense); 
        }
        
        return ResponseEntity.notFound().build();
        //return Optional.empty(); commented out temporarily
    }

    // DELETE expense by id
    // uses @PathVariable etc.
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpenseById(@PathVariable Long id) {
        Optional<Expense> result = expenseRepository.findById(id);

        if (result.isPresent()) {
            expenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    
}
