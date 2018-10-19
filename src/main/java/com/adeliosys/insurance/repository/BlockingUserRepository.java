package com.adeliosys.insurance.repository;

import com.adeliosys.insurance.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockingUserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
}
