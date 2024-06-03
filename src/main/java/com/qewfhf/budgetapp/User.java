package com.qewfhf.budgetapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    @Id
    private ObjectId id;
    private String userId;
    private String email;
    private List<Account> accountList;
    private List<Transaction> transactionList;
    public User(String name, String email) {
        this.email = email;
        this.name = name;
        this.userId = UUID.randomUUID().toString();
    }
}
