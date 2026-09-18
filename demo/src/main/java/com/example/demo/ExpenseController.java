package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;



import java.util.List;
import java.util.Optional;

@RestController
public class ExpenseController {

    // Repositories for constructor injection
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    // Constructor injection for expense
    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        
    }

    // Handles GET expenses
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Handles POST expenses
    @PostMapping("/users/{id}/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense, @PathVariable Long id) {
        Optional<User> result = userRepository.findById(id);

        if (result.isPresent()) { 
            
            User user = result.get();
            
            expense.setUser(user);
            
            Expense savedExpense = expenseRepository.save(expense);

            return ResponseEntity.ok(savedExpense);
        }
        
        
        return ResponseEntity.notFound().build();
    }

    // GET expense by id
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/expenses/{id}") 
    public Optional<Expense> getExpensesById(@PathVariable Long id) {
        return expenseRepository.findById(id);
    }

    // GET expense by userId
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/users/{id}/expenses") 
    public List<Expense> getExpensesByUserId(@PathVariable Long id) {
        return expenseRepository.findByUserId(id);
    }

    // UPDATE expense by id
    // uses @pathvariable etc.
    @PutMapping("/expenses/{id}")
    public Optional<Expense> putExpenseById(@PathVariable Long id, @RequestBody Expense expense) {
        Optional<Expense> result = getExpensesById(id);
        
        if (result.isPresent()) {
            Expense existingExpense = result.get();

            existingExpense.setAmount(expense.getAmount());
            existingExpense.setDate(expense.getDate());
            existingExpense.setDescription(expense.getDescription());

            Expense savedExpense = expenseRepository.save(existingExpense);

            return Optional.of(savedExpense); 
        }
        
        return Optional.empty();
    }

    // DELETE expense by id
    // uses @PathVariable etc.
    @DeleteMapping("/expenses/{id}")
    public void deleteExpenseById(@PathVariable Long id) {
        expenseRepository.deleteById(id);
    }

    
}
