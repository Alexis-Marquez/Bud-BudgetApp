package com.qewfhf.budgetapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    BudgetService budgetService;
    public Optional<User> getUserByUserId(String userId){
        return userRepository.findUserByUserId(userId);
    }

    public User createUser(String name, String email) {
        return userRepository.insert(new User(name, email));
    }

    private void modifyBudget(String userId, BigDecimal newTotal) {
        Query queryFindUser = new Query(new Criteria("userId").is(userId));
        Update updateOpBudget = new Update().set("budgetMonth", newTotal);
        mongoTemplate.updateFirst(queryFindUser, updateOpBudget, User.class);
    }
    public Optional<User> createBudget(String userId, BigDecimal newTotal) {
        modifyBudget(userId, newTotal);
        mongoTemplate.update(User.class)
                .matching(Criteria.where("userId").is(userId))
                .apply(new Update().push("budgetList").value(new Budget(YearMonth.now().toString(),newTotal,userId)))
                .first();
        return userRepository.findUserByUserId(userId);
    }
}

