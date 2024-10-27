//package com.mycom.myapp.naviya.domain.book.controller;
//
//import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
//import com.mycom.myapp.naviya.domain.book.dto.BookDetailWithLikeDto;
//import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
//import com.mycom.myapp.naviya.domain.book.service.BookService;
//import com.mycom.myapp.naviya.domain.child.service.ChildService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/Book")
//public class BookCRUDController {
//    private final BookService bookService;
//    private final ChildService childService;
//
//
//    @GetMapping("/detail")
//    public String detailBook(@RequestParam long bookId, Model model, HttpSession session) {
//        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
//
//        // 책의 상세 정보를 가져옴
//        BookResultDto bookResultDto = new BookResultDto();
//
//        // 좋아요 여부 확인
//        boolean isLiked = childService.existsLike(selectedChildId, bookId);
//
//        // 싫어요 여부 확인
//        boolean isDisliked = childService.existsDislike(selectedChildId, bookId);
//
//        // 새로 만든 DTO에 정보 저장
//        BookDetailDto bookDetailDto = bookService.detailBook(bookId).getBookDetail();
//        bookDetailDto.setLiked(isLiked);
//        bookDetailDto.setDisliked(isDisliked);
//
//        // 모델에 추가
//        model.addAttribute("book", bookDetailDto);
//        session.setAttribute("book", bookDetailDto);
//
//        return "BookDetailPage";
//    }
//    @GetMapping("/read")
//    public String readBook(Model model, HttpSession session) {
//        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
//        model.addAttribute("book", bookDetailDto);
//        return "BookReadPage";
//    }
//}
