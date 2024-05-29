package com.qewfhf.budgetapp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
@Autowired
    private TransactionService transactionService;
@PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, String> payload) throws AccountNotFoundException {
        return new ResponseEntity<Transaction>(transactionService.createTransaction(new BigDecimal(payload.get("amount")),payload.get("accountId"), LocalDate.parse(payload.get("time"))), HttpStatus.CREATED);
}

}
