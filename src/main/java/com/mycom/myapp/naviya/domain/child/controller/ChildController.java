package com.mycom.myapp.naviya.domain.child.controller;

import com.mycom.myapp.naviya.domain.child.dto.ChildAddDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildResultDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildWithMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiService;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mycom.myapp.naviya.domain.child.service.ChildService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildMbtiService childMbtiService;
    private final ChildRepository childRepository;
    private final ChildService childService;
    private final Logger LOGGER = LoggerFactory.getLogger(ChildController.class);
    private final UserRepository userRepository;

    @GetMapping("/{childId}/mbti-history")
    public String getChildMbtiHistory(@PathVariable Long childId, Model model) {
        ChildWithMbtiHistoryDto childWithMbtiHistory = childMbtiService.getChildMbtiHistory(childId);
        model.addAttribute("childWithMbtiHistory", childWithMbtiHistory);
        model.addAttribute("expanded", false);
        return "mbtiHistory"; // 반환하는 뷰 이름 (예: childMbtiHistory.html)
    }


    @GetMapping("/{childId}/diagnosisForm")
    public String getDiagnosisForm(@PathVariable Long childId, Model model) {
        model.addAttribute("childId", childId);
        return "diagnosisForm";
    }

    // 테스트 용도
    @GetMapping("/history")
    public String getChildMbtiHistory2(HttpSession session, Model model) {
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        ChildWithMbtiHistoryDto childWithMbtiHistory = childMbtiService.getChildMbtiHistory(selectedChildId);

        model.addAttribute("expanded", false);
        model.addAttribute("childWithMbtiHistory", childWithMbtiHistory);

        return "mbtiHistory"; // 반환하는 뷰 이름 (예: childMbtiHistory.html)
    }

    // 테스트 용도
    @GetMapping("/diagnosisForm")
    public String getDiagnosisForm() {
        return "diagnosisForm";
    }

    @GetMapping("/select")
    public String selectChildPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일 가져오기
        String email = (String) session.getAttribute("userEmail");

        if (email != null) {
            // 이메일로 사용자 정보를 조회
            User user = userRepository.findByEmail(email);
            // 사용자에게 등록된 자녀 목록 가져오기
            List<Child> children = childRepository.findByUser_UserId(user.getUserId());
            model.addAttribute("children", children);
        }
        return "childSelectPage"; // 자녀 선택 페이지 렌더링
    }

    @PostMapping("/select")
    public String selectChild(@RequestParam("childId") Long childId, HttpSession session) {
        // 세션에 선택한 자녀의 childId를 저장
        session.setAttribute("selectedChildId", childId);

        // 필요한 경우 다른 페이지로 리다이렉트 또는 처리
        return "redirect:/";
    }


    // 자녀 프로필 조회
    @GetMapping("/page")
    public String getChildren(Model model, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");


        User user = userRepository.findByEmail(email);
        Long userId = user.getUserId();
        List<Child> childDto = childService.getChildrenByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("childDto", childDto);

        return "childPage";

    }


    // 자녀 추가
    @PostMapping("/addPage")
    @ResponseBody
    public ChildResultDto addChild(@RequestBody ChildAddDto childAddDto, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        Long userId = user.getUserId();

        ChildDto childDto = childAddDto.getChildDto();
        childDto.setUserId(userId);
        childAddDto.setChildDto(childDto); // 변경된 childDto를 다시 설정

        return childService.addChild(childAddDto);
    }


    //     자녀 인적사항 조회
    @GetMapping("/detailPage")
    public String getChildDetail(@RequestParam("childId") Long childId, Model model, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        model.addAttribute("user", user);

        ChildResultDto childResultDto = childService.getChildDetailById(childId);
        ChildAddDto childAddDto = childResultDto.getChildAddDto();
        model.addAttribute("childAddDto", childAddDto);
        return "childUpdate";
    }

//    // 자녀 인적사항 조회 AJAX
//    @GetMapping("/detailPage")
//    @ResponseBody
//    public ChildResultDto getChildDetail(@RequestParam("childId") Long childId, HttpSession session) {
//        String email = (String) session.getAttribute("userEmail");
//        User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
//        if (user == null) {
//            throw new RuntimeException("User not found with email: " + email);
//        }
//        Long userId = user.getUserId();
//
//
//        System.out.println("-------------------------");
//        System.out.println("childId: " + childId);
//        System.out.println("-------------------------");
//        ChildResultDto childResultDto = childService.getChildDetailById(childId);
//        ChildAddDto childAddDto = childResultDto.getChildAddDto();
//        return childResultDto;
//    }


    // 자녀 정보 수정
    @PutMapping("/detailPage/update")
    @ResponseBody
    public ChildResultDto updateChild(@RequestBody ChildDto childDto, @RequestBody List<String> categoryCodeList, HttpSession session) {
        return childService.updateChildWithCategories(childDto, categoryCodeList);
    }
}
