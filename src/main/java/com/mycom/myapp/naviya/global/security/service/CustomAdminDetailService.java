package com.mycom.myapp.naviya.global.security.service;

import com.mycom.myapp.naviya.domain.common.entity.Admin;
import com.mycom.myapp.naviya.domain.common.repository.AdminRepository;
import com.mycom.myapp.naviya.global.security.dto.CustomAdminDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomAdminDetailService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(CustomAdminDetailService.class);
    public final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
        logger.info("adminEmail" + adminEmail);

        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        if (admin == null) {
            throw new UsernameNotFoundException("관리자를 찾을 수 없습니다.");
        }

        // Spring Security가 인식할 수 있는 User 객체를 반환
//        return new User(
//                admin.getAdminEmail(),
//                admin.getAdminPassword(),
//                getAuthorities(admin)
//        );
        return new CustomAdminDetails(admin);
    }

//    private Collection<? extends GrantedAuthority> getAuthorities(Admin admin) {
//        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        return authorities;
//    }
}
