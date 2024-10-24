package com.mycom.myapp.naviya.global.mbti.repository;

import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Mbti 리포지토리
@Repository
public interface MbtiRepository extends JpaRepository<Mbti, Long> {

}
