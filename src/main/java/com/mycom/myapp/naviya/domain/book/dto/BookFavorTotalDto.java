package com.mycom.myapp.naviya.domain.book.dto;

import com.mycom.myapp.naviya.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookFavorTotalDto {
    private Long count;
}
