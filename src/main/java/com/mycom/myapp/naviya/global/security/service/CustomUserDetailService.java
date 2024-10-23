package com.mycom.myapp.naviya.global.security.service;

import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    public final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("이메일로 사용자 찾기 시도: " + email);
        User user = userRepository.findByEmail(email);
        System.out.println("찾은 사용자: " + user);

        if (user != null) {
            return new CustomUserDetails(user);
        }
        throw new UsernameNotFoundException("사용자를 찾지 못했습니다: " + email);
    }
}
