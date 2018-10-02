package com.adeliosys.insurance;

import com.adeliosys.insurance.web.AccessLogFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

/**
 * Sample insurance application that provides insurance quotes.
 */
@SpringBootApplication
@EnableMongoRepositories
public class InsuranceBlocking {

    /**
     * Declare and map the access log filter.
     * <p>
     * Currently disabled since it does not work well for reactive requests.
     * When enabled, also enable the right logging pattern in application.yml
     */
    //@Bean // Uncomment to enable
    public FilterRegistrationBean contextFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AccessLogFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/*")); // Patterns from @WebFilter are not used by Spring
        registrationBean.setOrder(2);
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(InsuranceBlocking.class, args);
    }
}
