package com.qewfhf.budgetapp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "RefreshToken")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshToken {
    @Id
    private ObjectId id;
    private String userId;
    private String token;
    private Instant expires;

}
