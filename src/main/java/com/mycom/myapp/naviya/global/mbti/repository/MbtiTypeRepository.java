package com.mycom.myapp.naviya.global.mbti.repository;

import com.mycom.myapp.naviya.global.mbti.entity.MbtiType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MbtiTypeRepository extends JpaRepository<MbtiType, Long> {

    @EntityGraph(attributePaths = {"descriptions", "tags"})
    MbtiType findByType(String type);

}