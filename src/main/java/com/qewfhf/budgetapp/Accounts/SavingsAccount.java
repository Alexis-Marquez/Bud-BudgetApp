package com.qewfhf.budgetapp.Accounts;

import com.qewfhf.budgetapp.Accounts.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccount extends Account {
    private double rate;
}
