package com.qewfhf.budgetapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/{userId}/budget")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;
    @GetMapping("/latestBudget")
    public ResponseEntity<Optional<Budget>> getLatestBudget(@PathVariable String userId){
        return new ResponseEntity<>(budgetService.getBudgetByUserId(userId, YearMonth.now().toString()), HttpStatus.OK);
    }
}
