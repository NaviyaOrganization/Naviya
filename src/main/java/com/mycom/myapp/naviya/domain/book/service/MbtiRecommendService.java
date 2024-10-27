package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;

import java.util.List;
import java.util.Map;

public interface MbtiRecommendService {
    List<BookDto> recommendBooks(Long childId);

    Map<String, Object> SNFTRecommendBooks(Long childId);

}
