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

    private final ExpenseRepository expenseRepository;

    // Constructor injection
    public ExpenseController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Handles GET expenses
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Handles POST expenses
    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(savedExpense);
    }

    // GET expense by id
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/expenses/{id}") 
    public Optional<Expense> getExpenseById(@PathVariable Long id) {
        return expenseRepository.findById(id);
    }

    // UPDATE expense by id
    // uses @pathvariable etc.
    @PutMapping("/expenses/{id}")
    public Optional<Expense> putExpenseById(@PathVariable Long id, @RequestBody Expense expense) {
        Optional<Expense> result = getExpenseById(id);
        
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
