package com.qewfhf.budgetapp.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable String userId){
        return new ResponseEntity<Optional<User>>(userService.getUserByUserId(userId), HttpStatus.OK);
    }
    @GetMapping("/{userId}/categories")
    public ResponseEntity<List<String>> getAvailableCategories(@PathVariable String userId){
        return new ResponseEntity<>(userService.getAvailableCategories(userId), HttpStatus.OK);
    }
    @PostMapping("/new-user")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> payload){
        return new ResponseEntity<User>(userService.createUser(payload.get("name"), payload.get("email")), HttpStatus.CREATED);
    }
    @PatchMapping("/{userId}/modify-budget/{newTotal}") //Only use when creating the first month budget of a new account or when modifying the current budget limit
    public ResponseEntity<Optional<User>> modifyBudget(@PathVariable String userId, @PathVariable BigDecimal newTotal){
        return new ResponseEntity<>(userService.createBudget(userId, newTotal), HttpStatus.ACCEPTED);
    }
}
