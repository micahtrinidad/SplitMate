package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetController(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/users/{userId}/budgets")
    public ResponseEntity<Budget> createBudget(
        @PathVariable Long userId,
        @Valid @RequestBody Budget budget) {
        Optional<User> userResult = userRepository.findById(userId);

        if (userResult.isPresent()) {
            budget.setUser(userResult.get());
            Budget savedBudget = budgetRepository.save(budget);

            return ResponseEntity.ok(savedBudget);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/budgets")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<Budget> budgets = budgetRepository.findByUserId(userId);

            return ResponseEntity.ok(budgets);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/budgets/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Optional<Budget> budgetResult = budgetRepository.findById(id);

        if (budgetResult.isPresent()) {
            return ResponseEntity.ok(budgetResult.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/budgets/{id}")
    public ResponseEntity<Budget> updateBudget(
        @PathVariable Long id,
        @Valid @RequestBody Budget budget) {
        Optional<Budget> budgetResult = budgetRepository.findById(id);

        if (budgetResult.isPresent()) {
            Budget existingBudget = budgetResult.get();
            existingBudget.setAmount(budget.getAmount());
            existingBudget.setMonth(budget.getMonth());
            existingBudget.setYear(budget.getYear());

            Budget savedBudget = budgetRepository.save(existingBudget);

            return ResponseEntity.ok(savedBudget);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        if (budgetRepository.findById(id).isPresent()) {
            budgetRepository.deleteById(id);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
