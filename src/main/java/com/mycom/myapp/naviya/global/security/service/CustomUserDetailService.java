package com.mycom.myapp.naviya.global.security.service;

import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
    public final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.info("userEmail" + email);
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new CustomUserDetails(user);
        }
        throw new UsernameNotFoundException("사용자를 찾지 못했습니다: " + email);
    }
}
