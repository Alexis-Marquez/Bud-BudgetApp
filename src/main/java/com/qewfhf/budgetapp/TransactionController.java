package com.qewfhf.budgetapp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
@Autowired
    private TransactionService transactionService;
@PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, String> payload) throws AccountNotFoundException {
        return new ResponseEntity<Transaction>(transactionService.createTransaction(new BigDecimal(payload.get("amount")),payload.get("accountId"),payload.get("userId"), LocalDateTime.parse(payload.get("time")), payload.get("name"), payload.get("description")), HttpStatus.CREATED);
}
@GetMapping("/{userId}/{page}")
    public ResponseEntity<List<Transaction>> getRecentTransactions(@PathVariable String userId, @PathVariable int page){
        return new ResponseEntity<List<Transaction>>(transactionService.getRecentTransactions(userId, page), HttpStatus.OK);
}

}
