package com.qewfhf.budgetapp.Accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/{userId}")
public class AccountsController {
    @Autowired
    private AccountService accountService;
//    @GetMapping("/{type}")
//    public ResponseEntity<Optional<ArrayList<Account>>> getAccountByTypeAndUserId(@PathVariable String type, @PathVariable String userId){
//        return new ResponseEntity<Optional<ArrayList<Account>>>(accountService.accountsByTypeAndUserId(type,userId),HttpStatus.OK);
//    }
@DeleteMapping("/{id}")
public ResponseEntity<Optional<Account>> deleteAccountById(@PathVariable String id, @PathVariable String userId){
    return new ResponseEntity<>(accountService.deleteAccountById(id), HttpStatus.OK);
}
    @GetMapping("/all-accounts")
    public ResponseEntity<Optional<List<Account>>> getAllAccounts(@PathVariable String userId){
        return new ResponseEntity<>(accountService.getAccountsByUserId(userId), HttpStatus.OK);
    }
    @PostMapping("/new-account")
    public ResponseEntity<Optional<Account>> createAccount(@RequestBody Map<String, String> payload, @PathVariable String userId){
        if(payload.containsKey("type") && payload.containsKey("name")){
            Optional<Account> account = accountService.createAccount(userId, payload.get("type"), payload.get("name"));
            if(account.isPresent()) {
                return new ResponseEntity<>(account, HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
       @GetMapping("/account/{id}")
    public ResponseEntity<Optional<Account>> getSingleAccount(@PathVariable String id, @PathVariable String userId){
        return new ResponseEntity<Optional<Account>>(accountService.singleAccountByUserId(id, userId),HttpStatus.OK);
    }

}

