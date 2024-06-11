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
import java.util.List;
import java.util.UUID;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@EnableScheduling
public class User {
    private String name;
    @Id
    private ObjectId id;
    private String userId;
    private String email;
    private List<Account> accountList;
    private List<Transaction> transactionList;
    private List<Budget> budgetsList;
    private BigDecimal budgetMonth;
    public User(String name, String email) {
        this.email = email;
        this.name = name;
        this.userId = UUID.randomUUID().toString();
    }
    @Scheduled(cron = "0 0 0 1 * ?") // Run on the 1st day of each month
    public void scheduleMonthlyTask() {
        budgetsList.addFirst(new Budget(YearMonth.now(),budgetMonth));
    }
}
