package com.qewfhf.budgetapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MongoTemplate mongoTemplate;//for more complex operations
    public Transaction createTransaction(BigDecimal amount, String accountId, String userId, LocalDateTime time,String name, String description) throws AccountNotFoundException {
        Optional<Account> curr = accountService.singleAccount(accountId);
        if(curr.isEmpty()){
            throw new AccountNotFoundException();
        }
        Transaction transaction = transactionRepository.insert(new Transaction(accountId, userId, time, amount,name,curr.get().getName(),description));
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

    public List<Transaction> getRecentTransactions(String userId, int page) {
        List<Transaction> AllTrans=transactionRepository.findTransactionByUserId(userId);
        System.out.println(AllTrans.size());
        int upperIndex;
        if(page*10-1>AllTrans.size()){
            upperIndex = AllTrans.size();
        }
        else {
            upperIndex=page*10-1;
        }
        return AllTrans.subList(page-1, upperIndex);
    }
}
