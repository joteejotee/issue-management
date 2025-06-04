package com.example.its.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername called with email: {}", username);
        
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("ユーザーが見つかりません: " + username);
                });

        logger.debug("User found: {}, password: {}", userEntity.getEmail(), userEntity.getPassword());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .disabled(!userEntity.getEnabled())
                .authorities("ROLE_USER")
                .build();
    }
} 