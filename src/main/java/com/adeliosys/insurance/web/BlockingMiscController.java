package com.adeliosys.insurance.web;

import com.adeliosys.insurance.Utils;
import com.adeliosys.insurance.model.Company;
import com.adeliosys.insurance.model.User;
import com.adeliosys.insurance.repository.BlockingCompanyRepository;
import com.adeliosys.insurance.repository.BlockingUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for various utility methods.
 */
@RestController
public class BlockingMiscController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingMiscController.class);

    private BlockingUserRepository blockingUserRepository;

    private BlockingCompanyRepository blockingCompanyRepository;

    public BlockingMiscController(BlockingUserRepository blockingUserRepository, BlockingCompanyRepository blockingCompanyRepository) {
        this.blockingUserRepository = blockingUserRepository;
        this.blockingCompanyRepository = blockingCompanyRepository;
    }

    /**
     * Pause for a given duration in msec.
     */
    @GetMapping("/pause/{duration}")
    public void pause(@PathVariable long duration) {
        Utils.pause(duration);
    }

    /**
     * Same with logs.
     */
    @GetMapping("/pause2/{duration}")
    public void pause2(@PathVariable long duration) {
        LOGGER.info("Pausing for {} msec", duration);
        Utils.pause(duration);
        LOGGER.info("Paused for {} msec", duration);
    }

    /**
     * Initialize the persistent data of the application.
     */
    @GetMapping("/init")
    void initialize() {
        LOGGER.info("Initializing the users");
        blockingUserRepository.deleteAll();
        blockingUserRepository.insert(User.getDefaultUsers());

        LOGGER.info("Initializing the companies");
        blockingCompanyRepository.deleteAll();
        blockingCompanyRepository.insert(Company.getDefaultCompanies());
    }
}
