package com.qewfhf.budgetapp.Accounts;

import com.qewfhf.budgetapp.Transactions.TransactionRepository;
import com.qewfhf.budgetapp.Users.User;
import com.qewfhf.budgetapp.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private User user;

    public Optional<List<Account>> getAccountsByUserId(String userId){
        return accountRepository.findAccountsByUserId(userId);
    }
    public Optional<Account> singleAccountByUserId(String id, String userId){
        return accountRepository.findAccountByAccountIdAndUserId(id, userId);
    }
    public Optional<Account> singleAccount(String id){
        return accountRepository.findAccountByAccountId(id);
    }
    public Optional<ArrayList<Account>> accountsByTypeAndUserId(String type, String userId){
        return accountRepository.findAccountsByTypeIgnoreCaseAndUserId(type, userId);
    }

    public Optional<Account> createAccount(String userId, String type, String name) {
        if(userService.getUserByUserId(userId).isPresent()) {
            Account account = accountRepository.insert(new Account(userId, type, name));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("accountList").value(account))
                    .first();
            return Optional.of(account);
        }
        return Optional.empty();
    }

    public Optional<Account> deleteAccountById(String id) {
        transactionRepository.deleteAllByAccountId(id);
        return accountRepository.deleteAccountByAccountId(id);
    }
}
