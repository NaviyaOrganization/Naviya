package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;

import java.util.List;

public interface MbtiRecommendService {
    List<BookDto> recommendBooks(Long childId);

}
