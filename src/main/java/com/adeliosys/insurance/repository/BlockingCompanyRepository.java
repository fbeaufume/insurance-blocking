package com.adeliosys.insurance.repository;

import com.adeliosys.insurance.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockingCompanyRepository extends MongoRepository<Company, String> {
}
