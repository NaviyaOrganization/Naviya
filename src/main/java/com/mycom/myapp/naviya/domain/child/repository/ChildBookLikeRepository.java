package com.mycom.myapp.naviya.domain.child.repository;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import com.mycom.myapp.naviya.domain.child.entity.ChildBookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChildBookLikeRepository extends JpaRepository<ChildBookLike, Long> {

    // JPQL 쿼리 사용
    //@Query("SELECT cbl.book FROM ChildBookLike cbl WHERE cbl.child.childId = :childId ORDER BY cbl.createdate DESC ")
    //List<Book> findBooksByChildId(long childId);

}