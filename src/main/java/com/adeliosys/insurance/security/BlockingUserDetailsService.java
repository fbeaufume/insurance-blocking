package com.adeliosys.insurance.security;

import com.adeliosys.insurance.model.User;
import com.adeliosys.insurance.repository.BlockingUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service used to load the user details during authentication.
 */
@Service
public class BlockingUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingUserDetailsService.class);

    private BlockingUserRepository blockingUserRepository;

    public BlockingUserDetailsService(BlockingUserRepository blockingUserRepository) {
        this.blockingUserRepository = blockingUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = blockingUserRepository.findByUsername(username);

        if (user == null) {
            LOGGER.warn("User '{}' not found", username);
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }
        else {
            LOGGER.info("Found user '{}'", username);
            return user;
        }
    }
}
