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
public class UserController {

    private final UserRepository userRepository;

    // Constructor injection
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Handles GET users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Handles POST users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // GET user by id
    // Uses @PathVariable to tell the function that the id parameter comes from the URL
    @GetMapping("/users/{id}") 
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    // UPDATE user by id
    // uses @pathvariable etc.
    @PutMapping("/users/{id}")
    public Optional<User> putUserById(@PathVariable Long id, @RequestBody User user) {
        Optional<User> result = getUserById(id);
        
        if (result.isPresent()) {
            User existingUser = result.get();

            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());

            User savedUser = userRepository.save(existingUser);

            return Optional.of(savedUser); 
        }
        
        return Optional.empty();
    }

    // DELETE user by id
    // uses @PathVariable etc.
    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // DELETE all users
    // uses @PathVariable etc.
    @DeleteMapping("/users")
    public void deleteUsers() {
        userRepository.deleteAll();
    }


}
