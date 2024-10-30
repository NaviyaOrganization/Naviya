package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import com.mycom.myapp.naviya.domain.book.service.BookAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/ai")
public class BookAiController {

    private final BookAiService bookAiService;

    @PostMapping("/BookStoryGenerate")
    public BookAiResponseDto generateStory(@RequestBody BookAiRequestDto bookAiRequestDto){
        return bookAiService.generateFullStory(bookAiRequestDto);
    }

    @GetMapping("/admin/book/insert")
    public String generateBookInfo(Model model) {
        // ChatGPT에서 생성된 책 정보 가져오기
        BookInsertDto bookInsertDto = bookAiService.generateBookInfo();

        // 필요한 데이터를 모델에 담아 HTML에 전달
        model.addAttribute("book", bookInsertDto);

        return "BookInsertPage";  // 책 삽입 페이지
    }

}
