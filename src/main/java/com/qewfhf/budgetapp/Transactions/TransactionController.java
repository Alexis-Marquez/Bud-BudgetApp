package com.qewfhf.budgetapp.Transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions/{userId}")
public class TransactionController {
@Autowired
    private TransactionService transactionService;
@PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, String> payload, @PathVariable String userId) throws AccountNotFoundException {
        return new ResponseEntity<Transaction>(transactionService.createTransaction(new BigDecimal(payload.get("Amount")),payload.get("AccountId"),userId, LocalDateTime.parse(payload.get("DateTime")), payload.get("Name"), payload.get("Description"), payload.get("Category"), payload.get("type")), HttpStatus.CREATED);
}
@GetMapping("/{page}")
    public ResponseEntity<List<Transaction>> getNextPageRecentTransactions(@PathVariable String userId, @PathVariable int page){
    return new ResponseEntity<>(transactionService.getNext5RecentTransactions(userId, page),HttpStatus.OK);
}
}
