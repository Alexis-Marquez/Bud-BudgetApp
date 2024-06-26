package com.qewfhf.budgetapp.Budgets;

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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "budgets")
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Budget {
    @Id
    private ObjectId id;
    private BigDecimal currentBalance;
    private BigDecimal budgetMax;
    private String monthYear;
    private String userId;
    private List<Category> categories = new ArrayList<>();

    public Budget(String monthYear, BigDecimal budgetMax, String userId) {
        this.monthYear = monthYear;
        this.budgetMax=budgetMax;
        this.currentBalance = BigDecimal.ZERO;
        this.userId = userId;
        categories.add(new Category("Expenses" ,BigDecimal.ZERO,this.userId));
        categories.add(new Category("Income",BigDecimal.ZERO,this.userId));
    }
    public Budget(String monthYear, BigDecimal budgetMax, String userId, BigDecimal currentBalance){
        this.monthYear = monthYear;
        this.budgetMax=budgetMax;
        this.currentBalance = currentBalance;
        this.userId = userId;
    }

}
