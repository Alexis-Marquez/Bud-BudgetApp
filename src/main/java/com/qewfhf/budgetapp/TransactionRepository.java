package com.qewfhf.budgetapp;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.SplittableRandom;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, ObjectId> {

    void deleteAllByAccountId(String id);
    List<Transaction> findNext5ByUserIdOrderByTimeDesc(String userId, Pageable pageable);
}
