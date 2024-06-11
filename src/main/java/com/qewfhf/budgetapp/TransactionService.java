package com.qewfhf.budgetapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final int numberOfItemsPerPage = 5;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MongoTemplate mongoTemplate;//for more complex operations
    public Transaction createTransaction(BigDecimal amount, String accountId, String userId, LocalDateTime time,String name, String description, String category) throws AccountNotFoundException {
        Optional<Account> curr = accountService.singleAccount(accountId);
        if(curr.isEmpty()){
            System.out.println(accountId);
            throw new AccountNotFoundException();
        }
        Transaction transaction = transactionRepository.insert(new Transaction(accountId, userId, time, amount,name,curr.get().getName(),description, category));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("transactionList").value(transaction))
                    .first();
            Query query = new Query(new Criteria("accountId").is(accountId));
            BigDecimal currTotal = accountService.singleAccount(accountId).orElseThrow().getBalance();
            Update updateOp = new Update().set("balance", currTotal.add(amount));
            mongoTemplate.updateFirst(query, updateOp, Account.class);
            return transaction;
    }
    public List<Transaction> getNext5RecentTransactions(String userId, int page) {
        Pageable pageable = PageRequest.of(page-1, 5); // Skip 10, take 5
        return transactionRepository.findNext5ByUserIdOrderByTimeDesc(userId, pageable);
    }
}
