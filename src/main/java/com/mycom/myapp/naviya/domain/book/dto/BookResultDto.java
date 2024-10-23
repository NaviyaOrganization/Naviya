package com.mycom.myapp.naviya.domain.book.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookResultDto {
    List<BookDto> books;
    BookDetailDto bookDetail;
    String success;
}
