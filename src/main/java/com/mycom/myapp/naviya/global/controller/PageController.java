package com.mycom.myapp.naviya.global.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookDetailDto;
import com.mycom.myapp.naviya.domain.book.dto.BookInsertDto;
import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.book.service.BookService;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.domain.child.service.ChildService;
import com.mycom.myapp.naviya.domain.common.entity.Admin;
import com.mycom.myapp.naviya.domain.common.repository.AdminRepository;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final AdminRepository adminRepository;
    private final ChildService childService;
    private final BookService bookService;

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {

        return "signup";
    }


    @GetMapping("/admin/login")
    public String adminLoginPage() {

        return "adminLogin";
    }

    @GetMapping("/admin/signup")
    public String adminSignupPage() {

        return "adminSignup";
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        // 세션에서 관리자 이메일 가져옴
        String adminEmail = (String) session.getAttribute("adminEmail");
        System.out.println("adminEmail---------------------------");
        System.out.println(adminEmail);
        System.out.println("adminEmail---------------------------");

        Admin admin = adminRepository.findByAdminEmail(adminEmail);
        model.addAttribute("admin", admin);

        return "admin";
    }

    @Controller
    public class BookController {

        @GetMapping("/book/image/{bookId}")
        @ResponseBody
        public ResponseEntity<byte[]> getBookImage(@PathVariable Long bookId) {
            byte[] imageBytes = bookService.getImageBytesByBookId(bookId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // PNG 형식이라면 IMAGE_PNG 사용
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
    }


    @GetMapping("/")
    public String mainPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일, 자녀 아이디를 가져옴
        String email = (String) session.getAttribute("userEmail");
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");

        System.out.println("---------------------------");
        System.out.println(email);
        System.out.println("---------------------------");

        System.out.println("---------------------------");
        System.out.println(selectedChildId);
        System.out.println("---------------------------");

        if (selectedChildId != null) { // 세션에 저장된 자녀 ID가 있을 경우
            Child child = childRepository.findByChildId(selectedChildId);
            model.addAttribute("child", child);
        } else {
            model.addAttribute("child", null);
        }

        if (email != null) {  // 세션에 저장된 이메일이 있을 경우
            User user = userRepository.findByEmail(email);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", null);
        }

        return "index";
    }

    @GetMapping("/ChildFavorBookList")
    public String ChildFavorBookListChildFavorBookList(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "BookCategoryHtml";
    }

    @GetMapping("/ChildRecentReadBook")
    public String   ChildRecentReadBook(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "ChildRecentReadBook";
    }

    @GetMapping("/BookCategoryHtml")
    public String   BookCategory(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "BookCategoryHtml";
    }

    @GetMapping("/search")
    public String search(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "search";
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

    @GetMapping("/adminBookDetail")
    public String adminDetailBook(@RequestParam Long bookId, Model model){
        BookDetailDto bookDetailDto = new BookDetailDto();
        bookDetailDto = bookService.adminBookDetail(bookId);
        model.addAttribute("book",bookDetailDto);
        return "adminBookDetailPage";
    }

    @GetMapping("/read")
    public String readBook(Model model, HttpSession session) {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        model.addAttribute("book", bookDetailDto);
        return "BookReadPage";
    }

    @GetMapping("/BookLike")
    public String BookLike(HttpSession session, Model model)
    {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        //Long childId = (Long) session.getAttribute("selectedChildId");
        String type = (String)session.getAttribute("Type");
        bookDetailDto.setLiked(true);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);
        bookService.ChildBookLike(bookDetailDto.getBookId(),1,"MBTI");
        return "BookDetailPage";
    }
    @GetMapping("/BookDisLike")
    public String BookDisLike(HttpSession session, Model model)
    {
        BookDetailDto bookDetailDto = (BookDetailDto) session.getAttribute("book");
        //Long childId = (Long) session.getAttribute("selectedChildId");
        bookDetailDto.setDisliked(true);
        session.setAttribute("book",bookDetailDto);
        model.addAttribute("book",bookDetailDto);
        bookService.ChildBookDisLike(bookDetailDto.getBookId(),1,"MBTI");
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

    @PostMapping("/insert")
    public String InsertBook(@ModelAttribute BookInsertDto bookInsertDto){
        bookService.insertBook(bookInsertDto);
        return "admin";
    }

    @PostMapping("/update")
    public String UpdateBook(@RequestBody BookInsertDto bookInsertDto){
        bookService.updateBook(bookInsertDto);
        return "admin";
    }

    @GetMapping("/BookDel")
    public String DeleteBook(@RequestParam long bookId)
    {
        bookService.delBook(bookId);
        return "admin";
    }



    @GetMapping("/List")
    public String AllBookList(Model model)
    {
        BookResultDto bookResultDto = bookService.listBook();
        model.addAttribute("books",bookResultDto.getBooks());
        return "admin";
    }
}
