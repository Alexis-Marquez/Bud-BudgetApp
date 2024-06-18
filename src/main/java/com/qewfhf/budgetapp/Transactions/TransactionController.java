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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions/{userId}")
public class TransactionController {
@Autowired
private TransactionService transactionService;

@PostMapping
    public ResponseEntity<Optional<Transaction>> createTransaction(@RequestBody Map<String, String> payload, @PathVariable String userId) {
    if(payload.containsKey("amount")&& payload.containsKey("accountId")&&payload.containsKey("name")&&payload.containsKey("type")&&payload.containsKey("dateTime")) {
        if(Objects.equals(payload.get("type"), "expense") || Objects.equals(payload.get("type"), "income")) {
        try {
            BigDecimal amount = new BigDecimal(payload.get("amount"));
            Optional<Transaction> transaction = transactionService.createTransaction(amount,
                    payload.get("accountId"), userId, LocalDateTime.parse(payload.get("dateTime")), payload.get("name"), payload.get("description"),
                    payload.get("category"), payload.get("type"));
            if(transaction.isPresent()) {
                return new ResponseEntity<>(transaction, HttpStatus.CREATED);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }}
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
}
@GetMapping("/{page}")
    public ResponseEntity<List<Transaction>> getNextPageRecentTransactions(@PathVariable String userId, @PathVariable int page){
    return new ResponseEntity<>(transactionService.getNext5RecentTransactions(userId, page),HttpStatus.OK);
}
}
