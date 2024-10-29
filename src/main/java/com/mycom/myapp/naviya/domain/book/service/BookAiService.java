package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;

public interface BookAiService {
    BookAiResponseDto generateFullStory(BookAiRequestDto requestDto);
    BookInsertDto generateBookInfo();
    String generateBookImage(String title, String summary);
}
