package com.mycom.myapp.naviya.domain.common.repository;

import com.mycom.myapp.naviya.domain.common.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, String> {

    @Query("SELECT c.codeName FROM Code c WHERE c.code = :code")
    String findCodeNameByCode(@Param("code") String code);

    @Query("SELECT c.code FROM Code c WHERE c.codeName = :codeName")
    String findCodeByCodeName(@Param("codeName") String codeName);

    Optional<Code> findByCode(String code);

}
