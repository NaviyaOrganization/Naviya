package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildSelectDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    // 아이의 기본 정보만 조회
    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildDto(c.childId, c.childName, c.childAge, c.codeMbti) " +
            "FROM Child c WHERE c.childId = :childId")
    ChildDto findChildDtoById(Long childId);

    List<Child> findByUser_UserId(Long userId);

    @Query("SELECT new com.mycom.myapp.naviya.domain.child.dto.ChildSelectDto(c.childId, c.childName, c.childImage) " +
            "FROM Child c left JOIN c.user u " +
            "WHERE u.userId = :userId")
    List<ChildSelectDto> findChildSelectDtoListByUserId(@Param("userId") Long userId);


    Child findByChildId(Long childId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Child c WHERE c.childId = :childId")
    int deleteChildByChildId(Long childId);

}
