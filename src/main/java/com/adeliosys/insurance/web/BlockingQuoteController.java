package com.adeliosys.insurance.web;

import com.adeliosys.insurance.Utils;
import com.adeliosys.insurance.model.Company;
import com.adeliosys.insurance.model.Quote;
import com.adeliosys.insurance.repository.BlockingCompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ForkJoinPool;

/**
 * REST endpoint used to provide insurance quotes.
 */
@RestController
public class BlockingQuoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingQuoteController.class);

    private BlockingCompanyRepository blockingCompanyRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port}")
    private int serverPort = 8080;

    private ForkJoinPool pool = new ForkJoinPool(10); // Sample thread name: "ForkJoinPool-1-worker-3"

    public BlockingQuoteController(BlockingCompanyRepository blockingCompanyRepository) {
        this.blockingCompanyRepository = blockingCompanyRepository;
    }

    /**
     * Simulate an insurance backend by returning an insurance quote.
     */
    @GetMapping("/quote/{id}")
    public Quote getQuote(@PathVariable String id, @RequestParam(name = "pause", defaultValue = "0", required = false) long duration) {
        LOGGER.info("Simulating quote for company '{}'", id);
        Quote quote = blockingCompanyRepository.findById(id)
                .map(Quote::new)
                .orElseGet(Quote::new);
        Utils.pause(duration);
        LOGGER.info("Simulated quote for company '{}'", id);
        return quote;
    }

    /**
     * Return the best quote from the registered insurance companies.
     */
    @GetMapping("/bestQuoteSerial")
    public Quote getBestQuoteSerial(@RequestParam(name = "pause", defaultValue = "0", required = false) long duration) {
        long timestamp = System.nanoTime();
        LOGGER.info("Getting the best quote");

        Quote quote = blockingCompanyRepository.findAll()
                .stream()
                .map(c -> getRemoteQuote(c, duration))
                .min(Quote::compareTo)
                .orElseGet(Quote::new);

        LOGGER.info("Found best quote cost of {} in {} msec", quote.getCost(), Utils.getDuration(timestamp));
        return quote;
    }

    /**
     * Return the best quote from the registered insurance companies.
     */
    @GetMapping("/bestQuote")
    public Quote getBestQuote(@RequestParam(name = "pause", defaultValue = "0", required = false) long duration) {
        long timestamp = System.nanoTime();
        LOGGER.info("Getting the best quote");

        Quote quote = pool.submit(() -> blockingCompanyRepository.findAll()
                .parallelStream()
                .map(c -> getRemoteQuote(c, duration))
                .min(Quote::compareTo))
                .join()
                .orElseGet(Quote::new);

        LOGGER.info("Found best quote cost of {} in {} msec", quote.getCost(), Utils.getDuration(timestamp));
        return quote;
    }

    /**
     * Get a remote quotation using an HTTP call.
     */
    private Quote getRemoteQuote(Company company, long duration) {
        String url = "http://localhost:" + serverPort + "/quote/" + company.getId() + "?pause=" + duration;
        LOGGER.info("Getting quote for company '{}' from '{}'", company.getId(), url);
        return restTemplate.getForObject(url, Quote.class);
    }
}
