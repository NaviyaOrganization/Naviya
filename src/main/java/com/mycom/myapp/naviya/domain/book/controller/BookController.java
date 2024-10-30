package com.mycom.myapp.naviya.domain.book.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.dto.BookDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
//import com.mycom.myapp.naviya.domain.book.service.BookServiceImpl;
import com.mycom.myapp.naviya.domain.book.service.BookService;
import com.mycom.myapp.naviya.domain.book.service.MbtiRecommendService;
import com.mycom.myapp.naviya.domain.child.service.ChildService;
import com.mycom.myapp.naviya.global.response.ResponseForm;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.mycom.myapp.naviya.global.response.ResponseCode.EXAMPLE_SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/Book")
public class BookController {
    private final BookService bookService;
    private final MbtiRecommendService mbtiRecommendService;
    private final ChildService childService;

    @PostMapping("insert")
    public BookResultDto InsertBook(BookInsertDto bookInsertDto){
        return bookService.insertBook(bookInsertDto);
    }

    @GetMapping("/List")
    public BookResultDto AllBookList()
    {
        return bookService.listBook();
    }
    @GetMapping("/ListOrderDate")
    public BookResultDto ListOrderDate(@RequestParam  long childId)
    {
        return bookService.listbookOrderByCreateDate(childId);
    }
    @GetMapping("/ListBookFavorCount")
    public BookResultDto ListBookFavorCount(@RequestParam  long childId)
    {
        return bookService.listBookFavorCount(childId);
    }
    @GetMapping("/ListBookChildFavor")
    public BookResultDto ListBookChildFavor(@RequestParam long childId)
    {
        return bookService.listBookChildFavor(childId);
    }
    @GetMapping("/ListBookChildRecntRead")
    public BookResultDto ListBookChildRecntRead(@RequestParam long childId)
    {
        return bookService.listBookChildRecntRead(childId);
    }
    @DeleteMapping("/BookDel")
    public BookResultDto DeleteBook(@RequestParam long bookId)
    {
        return bookService.delBook(bookId);
    }
//    @GetMapping("/detail")
//    public String detailBook(@RequestParam long bookId, Model model, HttpSession session) {
//        BookResultDto bookResultDto = bookServiceImpl.detailBook(bookId);
//        model.addAttribute("book", bookResultDto);
//        session.setAttribute("bookDto", bookResultDto);
//        return "BookDetailPage";
//    }
//    @GetMapping("/read")
//    public String readBook(Model model, HttpSession session) {
//        BookResultDto bookResultDto = (BookResultDto) session.getAttribute("bookDto");
//        model.addAttribute("book", bookResultDto);
//        return "BookReadPage";
//    }

    @GetMapping("/BookLike")
    public String BookLike(HttpSession session, Model model)
    {
        /* BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        Long childId = (Long) session.getAttribute("selectedChildId");
        String type = (String)session.getAttribute("Type");
        bookDetailDto.setLiked(true);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);i*/
        bookService.ChildBookLike(1,1,"MBTI");
        return "BookDetailPage";
    }
    @GetMapping("/BookDisLike")
    public String BookDisLike(HttpSession session, Model model)
    {
        /*BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        Long childId = (Long) session.getAttribute("selectedChildId");
        String type = (String)session.getAttribute("Type");
        bookDetailDto.setDisliked(true);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);*/
        bookService.ChildBookDisLike(1,1,"MBTI");
        return "BookDetailPage";
    }
    @GetMapping("/DelBookLike")
    public String DelBookLike(HttpSession session,Model model)
    {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        //Long childId = (Long) session.getAttribute("selectedChildId");
        bookDetailDto.setLiked(false);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);
        bookService.DelChildBookLike(bookDetailDto.getBookId(),1);
        return "BookDetailPage";
    }
    @GetMapping("/DelBookDisLike")
    public String DelBookDisLike(HttpSession session, Model model)
    {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        //Long childId = (Long) session.getAttribute("selectedChildId");
        bookDetailDto.setDisliked(false);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);
        bookService.DelChildBookDisLike(bookDetailDto.getBookId(),1);
        return "BookDetailPage";
    }

    @GetMapping("/recommend")
    public ResponseForm recommendBook(@RequestParam Long childId) {
        List<BookDto> books = mbtiRecommendService.recommendBooks(childId);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }

    @GetMapping("/snftrecommend")
    public ResponseForm SNFTRecommendBooks(@RequestParam Long childId) {
        Map<String, Object> books = mbtiRecommendService.SNFTRecommendBooks(childId);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }

    @GetMapping("/search")
    public ResponseForm searchBooks(@RequestParam String searchType, String keyword) {
        List<BookDto> books = bookService.searchBooks(searchType, keyword);
        return ResponseForm.of(EXAMPLE_SUCCESS, books);
    }
    //카테고리 리스트
    @GetMapping("/CategoryList")
    public BookResultDto BookCategoryLike(@RequestParam long childId) {
        return bookService.CategoryList(childId);
    }
    //카테고리 좋아요
    @GetMapping("/CategoryDisLike")
    public BookResultDto BookCategoryDisLike(@RequestParam long childId,@RequestParam String Ctegory) {
        return bookService.CategoryDisLike(childId,Ctegory);
    }
    //카테고리 싫어요
    @GetMapping("/CategoryLike")
    public BookResultDto BookCategoryLike(@RequestParam long childId,@RequestParam String Ctegory) {
        return bookService.CategoryLike(childId,Ctegory);
    }

    @GetMapping("/detail")
    public String detailBook(@RequestParam long bookId, Model model, HttpSession session) {
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");

        // 책의 상세 정보를 가져옴
        BookResultDto bookResultDto = new BookResultDto();

        // 좋아요 여부 확인
        boolean isLiked = childService.existsLike(selectedChildId, bookId);

        // 싫어요 여부 확인
        boolean isDisliked = childService.existsDislike(selectedChildId, bookId);

        // 새로 만든 DTO에 정보 저장
        BookDetailDto bookDetailDto = bookService.detailBook(bookId,selectedChildId).getBookDetail();
        bookDetailDto.setLiked(isLiked);
        bookDetailDto.setDisliked(isDisliked);

        // 모델에 추가
        model.addAttribute("book", bookDetailDto);
        session.setAttribute("book", bookDetailDto);

        return "BookDetailPage";
    }
    @GetMapping("/read")
    public String readBook(Model model, HttpSession session) {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        model.addAttribute("book", bookDetailDto);
        return "BookReadPage";
    }

    //유성이 요청 ALLbookList에서 child id  나이 고려, 좋아요 싫어요 고려
    @GetMapping("/ChildAllListBook")
    public BookResultDto ChildALLListBook(@RequestParam  long childId)
    {
        return bookService.ChildALLlistBook(childId);
    }
    @GetMapping("/NoChildlistBookChildFavor")
    public BookResultDto NoChildListBookFavorCount()
    {
        return bookService.NoChildlistBookChildFavor();
    }
    @GetMapping("/NoChildListOrderDate")
    public BookResultDto NoChildListOrderDate()
    {
        return bookService.NoCHildlistbookOrderByCreateDate();
    }

    @GetMapping("/BookCategoryOne")
    public BookResultDto BookCategoryOne(@RequestParam Long childId,@RequestParam String Ctegory)
    {
        return bookService.BookCategoryOne(childId,Ctegory);
    }
}
