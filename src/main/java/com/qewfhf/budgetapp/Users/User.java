package com.qewfhf.budgetapp.Users;

import com.qewfhf.budgetapp.Accounts.Account;
import com.qewfhf.budgetapp.Budgets.Budget;
import com.qewfhf.budgetapp.Budgets.BudgetService;
import com.qewfhf.budgetapp.Budgets.Category;
import com.qewfhf.budgetapp.Transactions.Transaction;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.net.SocketOption;
import java.time.YearMonth;
import java.util.*;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@Builder
public class User implements UserDetails {
    private String name;
    @Id
    private ObjectId id;
    private String userId;
    private String email;
    private String password;

    private Role role;

    private List<Account> accountList;
    private List<Transaction> transactionList;
    private List<Budget> budgetList;
    private BigDecimal budgetMonthTotal;
    @Getter
    private List<Category> availableCategories = new ArrayList<>();

    public User(String name, String email, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userId = UUID.randomUUID().toString();
        availableCategories.add(new Category("expenses", budgetMonthTotal, this.userId));
        availableCategories.add(new Category("income", budgetMonthTotal, this.userId));
        setRole(Role.USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
