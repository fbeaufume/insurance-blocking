package com.adeliosys.insurance.web;

import com.adeliosys.insurance.model.Company;
import com.adeliosys.insurance.repository.BlockingCompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoint used to manage (CRUD) insurance companies.
 */
@RestController
public class BlockingCompanyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingCompanyController.class);

    private BlockingCompanyRepository blockingCompanyRepository;

    public BlockingCompanyController(BlockingCompanyRepository blockingCompanyRepository) {
        this.blockingCompanyRepository = blockingCompanyRepository;
    }

    @GetMapping("/companies")
    List<Company> getCompanies() {
        LOGGER.info("Getting all companies");
        return blockingCompanyRepository.findAll();
    }

    @GetMapping("/companies/{id}")
    Company getCompany(@PathVariable String id) {
        LOGGER.info("Getting company '{}'", id);
        return blockingCompanyRepository.findById(id).get();
    }
}
