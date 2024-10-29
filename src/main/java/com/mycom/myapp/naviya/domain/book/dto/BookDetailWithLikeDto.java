package com.mycom.myapp.naviya.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailWithLikeDto {
    private BookDetailDto book;
    private boolean isLiked;
    private boolean isDisliked;
}
