package com.adeliosys.insurance.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/**
 * Spring Security configuration.
 */
@Configuration
public class BlockingSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingSecurityConfig.class);

    private BlockingUserDetailsService blockingUserDetailsService;

    public BlockingSecurityConfig(BlockingUserDetailsService blockingUserDetailsService) {
        this.blockingUserDetailsService = blockingUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("Initializing the security configuration");
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authorizeRequests()
                .antMatchers("/companies").hasAnyRole("USER")
                .antMatchers("/**").permitAll()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(blockingUserDetailsService);
    }

    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
