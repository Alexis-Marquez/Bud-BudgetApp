package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.Budgets.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
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
    public ResponseEntity<List<Category>> getAvailableCategories(@PathVariable String userId){
        return new ResponseEntity<>(userService.getAvailableCategories(userId), HttpStatus.OK);
    }
    @PostMapping("/{userId}/addCategory")
    public ResponseEntity<Optional<Category>> addCategory(@PathVariable String userId, @RequestBody Map<String, String> payload){
        if (payload.containsKey("name")){
            if(payload.containsKey("total")){
                return new ResponseEntity<>(userService.addCategory(userId, new BigDecimal(payload.get("total")), payload.get("name")), HttpStatus.CREATED);
            }
            Optional<Category> category =userService.addCategory(userId, BigDecimal.ZERO, payload.get("name"));
            if (category.isPresent()){
                return new ResponseEntity<Optional<Category>>(category, HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<Optional<Category>>(HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/new-user")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> payload){ //Only meant for testing purposes
        if(payload.get("name").isEmpty() || payload.get("email").isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<User>(userService.createUser(payload.get("name"), payload.get("email")), HttpStatus.CREATED);
    }
    @PatchMapping("/{userId}/modify-budget/{newTotal}/{monthYear}") //Only use when creating the first month budget of a new account or when modifying the current budget limit
    public ResponseEntity<Optional<User>> modifyBudget(@PathVariable String userId, @PathVariable BigDecimal newTotal, @PathVariable YearMonth monthYear){
        Optional<User> budget = userService.createBudget(userId, newTotal, monthYear);
        return new ResponseEntity<>(budget, HttpStatus.ACCEPTED);
    }
}

