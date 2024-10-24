package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    // 아이의 기본 정보만 조회
    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildDto(c.childId, c.childName, c.childAge, c.codeMbti) " +
            "FROM Child c WHERE c.childId = :childId")
    ChildDto findChildDtoById(Long childId);

    List<Child> findByUser_UserId(Long userId);
}
