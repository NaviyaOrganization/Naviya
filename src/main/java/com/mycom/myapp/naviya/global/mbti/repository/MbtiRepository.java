package com.mycom.myapp.naviya.global.mbti.repository;

import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MbtiRepository extends JpaRepository<Mbti, Long> {

}