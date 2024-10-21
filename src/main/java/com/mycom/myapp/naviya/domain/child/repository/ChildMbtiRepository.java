package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildMbtiRepository extends JpaRepository<ChildMbti, Long> {
    // child_id로 ChildMbti 엔티티를 찾기 위한 메서드
    ChildMbti findByChild_ChildId(Long childId);
}