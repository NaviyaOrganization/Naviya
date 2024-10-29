package com.mycom.myapp.naviya.domain.common.repository;

import com.mycom.myapp.naviya.domain.common.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {

    Boolean existsByAdminEmail(String adminEmail);
    Admin findByAdminEmail(String adminEmail);
}
