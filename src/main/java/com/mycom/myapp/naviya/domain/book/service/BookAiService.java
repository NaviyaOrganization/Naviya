package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;

public interface BookAiService {
    BookAiResponseDto generateFullStory(BookAiRequestDto requestDto);
}
