package com.qewfhf.budgetapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private ObjectId id;
    private String accountId;
    private String userId;
    private String type;
    private BigDecimal balance;
    private String name;
//    @DocumentReference can be used to connect only the reference of the transaction and keep the transaction in a different collection
    private List<Transaction> transactionList;

    public Account(String userId, String type, String name) {
        this.accountId = UUID.randomUUID().toString();
        this.userId = userId;
        this.type = type;
        this.balance = BigDecimal.valueOf(0);
        this.name = name;
    }
}
