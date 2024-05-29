package com.qewfhf.budgetapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Document(collection = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private ObjectId id;
    private String accountId;
    private LocalDate time;
    private BigDecimal amount;
    public Transaction(String accountId, LocalDate time, BigDecimal amount) {
        this.accountId = accountId;
        this.time = time;
        this.amount = amount;
    }
}
