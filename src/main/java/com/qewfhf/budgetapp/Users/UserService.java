package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.Budgets.Budget;
import com.qewfhf.budgetapp.Budgets.BudgetRepository;
import com.qewfhf.budgetapp.Budgets.BudgetService;
import com.qewfhf.budgetapp.Budgets.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
@Service
@EnableScheduling
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private User user;

    @Scheduled(cron = "0 0 0 1 * *") // Run on the 1st day of each month
    public void scheduleMonthlyAddBudget() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            this.createBudget(user.getUserId(), user.getBudgetMonthTotal(), YearMonth.now());
        }
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByEmail(username);
    }
    public Optional<User> getUserByUserId(String userId){
        return userRepository.findUserByUserId(userId);
    }

    public User createUser(String name, String email, String password) {
        return userRepository.insert(new User(name,email,password));
    }

    private void modifyBudget(String userId, BigDecimal newTotal) { //Sets the max budget field
        Query queryFindUser = new Query(new Criteria("userId").is(userId));
        Update updateOpBudget = new Update().set("budgetMonthTotal", newTotal);
        mongoTemplate.updateFirst(queryFindUser, updateOpBudget, User.class);
    }
    public Optional<User> createBudget(String userId, BigDecimal newTotal, YearMonth yearMonth) { //creates a new budget with the current balance in case it is in the middle of the month
        Optional<User> user =  userRepository.findUserByUserId(userId);
        if(user.isEmpty()){
            return Optional.empty();
        }
        modifyBudget(userId, newTotal);
        Optional<Budget> curr = budgetService.getBudgetByUserIdAndMonthYear(userId, yearMonth);
        Optional<Budget> prev = budgetService.getBudgetByUserIdAndMonthYear(userId, yearMonth.minusMonths(1));
        if (curr.isPresent()) {
            BigDecimal currBudget = curr.orElseThrow().getCurrentBalance();
            Budget newBudget = budgetRepository.insert(new Budget(yearMonth.toString(), newTotal, userId, currBudget));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("budgetList").value(newBudget))
                    .first();
        } else if(prev.isPresent()){
            Budget newBudget = new Budget(yearMonth.toString(), newTotal, userId, BigDecimal.ZERO);
            for(Category category: prev.get().getCategories()){
                newBudget.getCategories().add(category);
            }
            budgetRepository.insert(newBudget);
            mongoTemplate.update(User.class).matching(Criteria.where("userId").is(userId)).apply(new Update().push("budgetList").value(newBudget)).first();
        }
        else{
            Budget newBudget = new Budget(yearMonth.toString(), newTotal, userId);
            for(Category category: user.get().getAvailableCategories()){
                newBudget.getCategories().add(category);
            }
            budgetRepository.insert(newBudget);
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("budgetList").value(newBudget))
                    .first();
        }
        return userRepository.findUserByUserId(userId);
    }

    public List<Category> getAvailableCategories(String userId) {
        Optional<Budget> budget =  budgetRepository.findBudgetByUserIdAndMonthYear(userId, YearMonth.now().toString());
        if(budget.isPresent()){
            return budget.orElseThrow().getCategories();
        }
        else{
            return new ArrayList<>();
        }
    }


    public Optional<Category> addCategory(String userId, BigDecimal total, String name) {
        Optional<Budget> budgetByUserIdAndMonthYear = budgetRepository.findBudgetByUserIdAndMonthYear(userId, YearMonth.now().toString());
        if (budgetByUserIdAndMonthYear.isPresent()) {
            Category value = new Category(name, total, userId);
            mongoTemplate.update(Budget.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("categories").value(value))
                    .all();
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                        .apply(new Update().push("availableCategories").value(value))
                    .first();
            return Optional.of(value);
        }
        else{
            return Optional.empty();
        }
    }
}

