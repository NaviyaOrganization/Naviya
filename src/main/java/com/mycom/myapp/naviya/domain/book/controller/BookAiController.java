package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookAiRequestDto;
import com.mycom.myapp.naviya.domain.book.dto.BookAiResponseDto;
import com.mycom.myapp.naviya.domain.book.service.BookAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class BookAiController {

    private final BookAiService bookAiService;

    @PostMapping("/BookStoryGenerate")
    public BookAiResponseDto generateStory(@RequestBody BookAiRequestDto bookAiRequestDto){
        System.out.println("controller 들어옴");
        return bookAiService.generateFullStory(bookAiRequestDto);
    }


}
