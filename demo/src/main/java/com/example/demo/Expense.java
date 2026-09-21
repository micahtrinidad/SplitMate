package com.example.demo;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal; // for money
import java.time.LocalDate; // for date

@Entity
@Table (name = "expenses") 
public class Expense {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank (message = "Description cannot be empty")
    private String description;

    @NotNull (message = "Amount must be a valid number")
    @Positive (message = "Amount must be a positive number")
    private BigDecimal amount;
    
    private LocalDate date;

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }

    // Create the foreign key "userId" 
    @ManyToOne
    @JoinColumn (name = "userId") 
    private User user;

    // Create the foreign key "categoryId" 
    @ManyToOne
    @JoinColumn (name = "categoryId") 
    private Category category;

    // Setters and getters for user in Expense
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Setters and getters for user in Category
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }



}