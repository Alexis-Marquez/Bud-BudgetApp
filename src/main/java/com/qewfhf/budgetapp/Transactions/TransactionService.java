package com.qewfhf.budgetapp.Transactions;

import com.qewfhf.budgetapp.Accounts.Account;
import com.qewfhf.budgetapp.Accounts.AccountService;
import com.qewfhf.budgetapp.Budgets.Budget;
import com.qewfhf.budgetapp.Budgets.BudgetService;
import com.qewfhf.budgetapp.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private BudgetService budgetService;
    @Autowired
    private MongoTemplate mongoTemplate;//for more complex operations
    public Optional<Transaction> createTransaction(BigDecimal amount, String accountId, String userId, LocalDate time, String name, String description, String category, String type) throws AccountNotFoundException {
        Optional<Account> curr = accountService.singleAccount(accountId);
        if(curr.isEmpty()){
            return Optional.empty();
        }
        Transaction transaction = transactionRepository.insert(new Transaction(accountId, userId, time, amount,name,curr.get().getName(),description, category, type));
            mongoTemplate.update(User.class)
                    .matching(Criteria.where("userId").is(userId))
                    .apply(new Update().push("transactionList").value(transaction))
                    .first();
            accountUpdater(accountId,amount,type);
            if(type.equals("expense")){
                budgetUpdater(userId,amount,type);
            }
            return Optional.of(transaction);
    }
    private void accountUpdater(String accountId, BigDecimal amount, String type){
        Query queryAccountUpdate = new Query(new Criteria("accountId").is(accountId));
        BigDecimal currTotal = accountService.singleAccount(accountId).orElseThrow().getBalance();
        Update updateOpBalanceAccount = new Update().set("balance", currTotal.add(amount));
        mongoTemplate.updateFirst(queryAccountUpdate, updateOpBalanceAccount, Account.class);
    }
    private void budgetUpdater(String userId, BigDecimal amount, String type){
        Query queryFindUser = new Query(new Criteria("userId").is(userId));
        User currUser = mongoTemplate.findOne(queryFindUser, User.class);
        assert currUser != null;
        Budget currBudget = currUser.getBudgetList().get(0);
        Query queryBudgetUpdate = new Query(new Criteria("id").is(currBudget.getId()));
        BigDecimal currTotalBudget = budgetService.singleBudget(currBudget.getId()).orElseThrow().getCurrentBalance();
        Update updateOpBalanceBudget = new Update().set("currentBalance", currTotalBudget.add(amount.abs()));
        mongoTemplate.updateFirst(queryBudgetUpdate, updateOpBalanceBudget, Budget.class);
    }
    public List<Transaction> getNext5RecentTransactions(String userId, int page) {
        Pageable pageable = PageRequest.of(page-1, 5); // Skip 10, take 5
        return transactionRepository.findNext5ByUserIdOrderByTimeDesc(userId, pageable);
    }
}
