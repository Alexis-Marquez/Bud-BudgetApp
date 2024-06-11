package com.qewfhf.budgetapp;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;
@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    public Optional<Budget> singleBudget(ObjectId id) {
        return budgetRepository.findBudgetById(id);
    }

    public Optional<Budget> getBudgetByUserId(String userId, String currentYearMonth) {
        return budgetRepository.findBudgetByUserIdAndMonthYear(userId, currentYearMonth);
    }
}
