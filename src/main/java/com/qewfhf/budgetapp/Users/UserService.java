package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.Budgets.Budget;
import com.qewfhf.budgetapp.Budgets.BudgetRepository;
import com.qewfhf.budgetapp.Budgets.BudgetService;
import com.qewfhf.budgetapp.Budgets.Category;
import com.qewfhf.budgetapp.Transactions.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;

    public Optional<User> getUserByUserId(String userId){
        return userRepository.findUserByUserId(userId);
    }

    public User createUser(String name, String email) {
        return userRepository.insert(new User(name, email));
    }

    private void modifyBudget(String userId, BigDecimal newTotal) { //Sets the max budget field
        Query queryFindUser = new Query(new Criteria("userId").is(userId));
        Update updateOpBudget = new Update().set("budgetMonthTotal", newTotal);
        mongoTemplate.updateFirst(queryFindUser, updateOpBudget, User.class);
    }
    public Optional<User> createBudget(String userId, BigDecimal newTotal) { //creates a new budget with the current balance in case it is in the middle of the month
        Optional<User> user =  userRepository.findUserByUserId(userId);
        if(user.isEmpty()){
            return Optional.empty();
        }
        modifyBudget(userId, newTotal);
        Optional<Budget> curr = budgetService.getBudgetByUserId(userId);
        if (curr.isPresent()) {
            BigDecimal currBudget = curr.orElseThrow().getCurrentBalance();
            Budget newBudget = budgetRepository.insert(new Budget(YearMonth.now().toString(), newTotal, userId, currBudget));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("budgetList").value(newBudget))
                    .first();
        }
        else{
            Budget newBudget = budgetRepository.insert(new Budget(YearMonth.now().toString(), newTotal, userId));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("budgetList").value(newBudget))
                    .first();
        }
        return userRepository.findUserByUserId(userId);
    }

    public List<Category> getAvailableCategories(String userId) {
        Optional<User> user =  userRepository.findUserByUserId(userId);
        if(user.isPresent()){
            return user.get().getAvailableCategories();
        }
        else{
            return new ArrayList<>();
        }
    }

    public Optional<Category> addCategory(String userId, String name) {
        if (userRepository.findUserByUserId(userId).isPresent()) {
        Category value = new Category(name);
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

    public Optional<Category> addCategory(String userId, BigDecimal total, String name) {
        if (userRepository.findUserByUserId(userId).isPresent()) {
            Category value = new Category(name, total);
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

