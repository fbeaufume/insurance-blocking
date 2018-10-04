package com.adeliosys.insurance;

import com.adeliosys.insurance.web.BlockingLogFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

/**
 * Sample insurance application that provides insurance quotes.
 */
@SpringBootApplication
@EnableMongoRepositories
public class InsuranceBlocking {

    /**
     * Declare and map the log filter.
     */
    @Bean // Comment to disable
    public FilterRegistrationBean<BlockingLogFilter> contextFilterRegistrationBean() {
        FilterRegistrationBean<BlockingLogFilter> registrationBean = new FilterRegistrationBean<BlockingLogFilter>();
        registrationBean.setFilter(new BlockingLogFilter());
        registrationBean.setUrlPatterns(Collections.singletonList("/*")); // Patterns from @WebFilter are not used by Spring
        registrationBean.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1); // Before Spring Security filter
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(InsuranceBlocking.class, args);
    }
}
