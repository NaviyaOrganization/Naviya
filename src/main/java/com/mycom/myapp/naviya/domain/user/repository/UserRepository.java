package com.mycom.myapp.naviya.domain.user.repository;

import com.mycom.myapp.naviya.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);
    User findByEmail(String email);
}
