package com.qewfhf.budgetapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MongoTemplate mongoTemplate;//for more complex operations
    public Transaction createTransaction(BigDecimal amount, String accountId, LocalDate time) throws AccountNotFoundException {
        if(accountService.singleAccount(accountId).isEmpty()){
            throw new AccountNotFoundException();
        }
        Transaction transaction = transactionRepository.insert(new Transaction(accountId, time, amount));
            mongoTemplate.update(Account.class)
                    .matching(Criteria.where("accountId").is(accountId))
                    .apply(new Update().push("transactionList").value(transaction))
                    .first();
            Query query = new Query(new Criteria("accountId").is(accountId));
            BigDecimal currTotal = accountService.singleAccount(accountId).orElseThrow().getBalance();
            Update updateOp = new Update().set("balance", currTotal.add(amount));
            mongoTemplate.updateFirst(query, updateOp, Account.class);
            return transaction;
    }
}
