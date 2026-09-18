package com.example.demo;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "categories") 
public class Category {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // eating out
    private String restaurant;

    // leisure shopping
    private String shopping;

    // groceries like essentials
    private String groceries;

    // like car service
    private String services;

    private String entertainment;
    private String gas;
    private String subscription;

    
    private String medical;

    public Long getId() { return id; }
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    // Create the foreign key "userId" 
    @ManyToOne
    @JoinColumn (name = "userId") 
    private User user;

    // Setters and getters for user in Expense
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}