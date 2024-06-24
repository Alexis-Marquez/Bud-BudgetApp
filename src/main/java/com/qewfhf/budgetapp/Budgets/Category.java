package com.qewfhf.budgetapp.Budgets;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.UUID;
@Data
public class Category {
    private String name;
    private String id;
    private String userId;
    private BigDecimal balance;
    private BigDecimal total;
    public Category(String name, BigDecimal total, String userId){
        this.id = String.valueOf(UUID.randomUUID());
        this.userId = userId;
        this.balance=BigDecimal.ZERO;
        this.name=name;
        this.total=total;
    }
}

