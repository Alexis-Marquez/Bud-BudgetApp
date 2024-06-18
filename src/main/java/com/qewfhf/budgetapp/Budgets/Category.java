package com.qewfhf.budgetapp.Budgets;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.UUID;

public class Category {
    @Setter
    @Getter
    private String name;
    private String id;
    @Getter
    private BigDecimal balance;
    @Getter
    private BigDecimal total;
    public Category(String name, BigDecimal total){
        this.id = String.valueOf(UUID.randomUUID());
        this.balance=total;
        this.name=name;
        this.total=total;
    }

    public Category(String name) {
        this.id = String.valueOf(UUID.randomUUID());
        this.balance=BigDecimal.ZERO;
        this.name=name;
        this.total=BigDecimal.ZERO;
    }
}

