package com.mycom.myapp.naviya.domain.book.repository;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    //@Query("SELECT b FROM Book b LEFT JOIN FETCH b.category LEFT JOIN FETCH b.recentBooks LEFT JOIN FETCH b.likedBooks")
    //List<Book> findAllWithAssociations();
}
