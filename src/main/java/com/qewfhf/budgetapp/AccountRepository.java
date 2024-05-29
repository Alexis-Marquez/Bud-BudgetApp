package com.qewfhf.budgetapp;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, ObjectId> {
    Optional<ArrayList<Account>> findAccountsByTypeIgnoreCaseAndUserId(String type,String userId);
    Optional<Account> findAccountByAccountIdAndUserId(String accountId, String userId);

    Optional<List<Account>> findAccountsByUserId(String userId);

    Optional<Account> findAccountByAccountId(String id);

    Optional<Account> deleteAccountByAccountId(String id);
}
