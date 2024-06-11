package com.qewfhf.budgetapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.YearMonth;

@Document(collection = "budgets")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Budget {
    @Id
    private ObjectId id;
    private BigDecimal currentBalance;
    private BigDecimal budgetMax;
    private final YearMonth monthYear;

    public Budget(YearMonth monthYear, BigDecimal budgetMax) {
        this.monthYear = monthYear;
        this.budgetMax=budgetMax;
        this.currentBalance = budgetMax;
    }

}
