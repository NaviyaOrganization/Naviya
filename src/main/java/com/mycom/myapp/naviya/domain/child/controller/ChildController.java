package com.mycom.myapp.naviya.domain.child.controller;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.child.dto.*;
import com.mycom.myapp.naviya.domain.child.dto.ChildAddDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildResultDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildWithMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiService;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;

import com.mycom.myapp.naviya.global.mbti.service.MbtiTypeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mycom.myapp.naviya.domain.child.service.ChildService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildMbtiService childMbtiService;
    private final ChildRepository childRepository;
    private final ChildService childService;
    private final Logger LOGGER = LoggerFactory.getLogger(ChildController.class);
    private final UserRepository userRepository;
    private final MbtiTypeService mbtiTypeService;

    // 자녀 성향 진단 히스토리 테스트 by HttpSession
    @GetMapping("/history")
    public String getChildMbtiHistory(HttpSession session, Model model) {
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        if (selectedChildId == null) {
            model.addAttribute("errorMessage", "선택된 아이 정보가 없습니다.");
            return "index"; // 같은 페이지에서 에러 메시지를 표시하도록 설정
        }
        ChildWithMbtiHistoryDto childWithMbtiHistory = childMbtiService.getChildMbtiHistory(selectedChildId);

        model.addAttribute("expanded", false);
        model.addAttribute("childWithMbtiHistory", childWithMbtiHistory);
        childMbtiService.navbarInfo(session, model);

        return "mbtiHistory"; // 반환하는 뷰 이름 (예: childMbtiHistory.html)
    }

    // diagnosisForm by HttpSession
    @GetMapping("/diagnosisForm")
    public String getDiagnosisForm(HttpSession session, Model model) {
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        // selectedChildId가 없는 경우 - 에러 메시지를 모델에 추가하고 기본 페이지 반환
        if (selectedChildId == null) {
            model.addAttribute("errorMessage", "선택된 아이 정보가 없습니다.");
            return "index"; // 같은 페이지에서 에러 메시지를 표시하도록 설정
        }
        model.addAttribute("ChildId", selectedChildId);
        childMbtiService.navbarInfo(session, model);
        return "diagnosisForm";
    }


    @GetMapping("/childMBTIResult")
    public String getChildMBTIResult(HttpSession session, Model model) {
        // 1. 세션에서 selectedChildId를 가져옴
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");

        // 2. selectedChildId가 없는 경우 - 에러 메시지를 모델에 추가하고 기본 페이지 반환
        if (selectedChildId == null) {
            model.addAttribute("errorMessage", "선택된 아이 정보가 없습니다.");
            return "index"; // 같은 페이지에서 에러 메시지를 표시하도록 설정
        }

        // 3. ChildMbti 정보 조회
        Optional<ChildMbtiDto> childMbtiDtoOpt = childMbtiService.getChildMbtiInfo(selectedChildId);

        // 4. ChildMbti 정보가 있는 경우 - MBTI 결과를 모델에 추가
        if (childMbtiDtoOpt.isPresent()) {
            ChildMbtiDto childMbtiDto = childMbtiDtoOpt.get();
            model.addAttribute("childMbti", childMbtiDto);
            model.addAttribute("responseMessage", "MBTI 조회가 성공적으로 완료되었습니다.");

            // MBTI 타입 정보 추가 (codeMbti가 빈 문자열이 아닌 경우에만)
            if (!childMbtiDto.getCodeMbti().isEmpty()) {
                model.addAttribute("mbtiType", mbtiTypeService.getMbtiTypeDtoByType(childMbtiDto.getCodeMbti()));
            }
        } else {
            // ChildMbti 정보가 없는 경우 - 에러 메시지를 모델에 추가
            model.addAttribute("errorMessage", "아이의 MBTI 정보가 없습니다.");
        }

        // 5. 네비게이션 정보 추가
        childMbtiService.navbarInfo(session, model);

        // 6. 결과 페이지로 이동
        return "childMBTIResult";
    }



    // 자녀 프로필 선택
    @GetMapping("/select")
    public String selectChildPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일 가져오기
        String email = (String) session.getAttribute("userEmail");


        if (email != null) {
            // 이메일로 사용자 정보를 조회
            User user = userRepository.findByEmail(email);
            // 사용자에게 등록된 자녀 목록 가져오기
            List<ChildSelectDto> children = childRepository.findChildSelectDtoListByUserId(user.getUserId());
            model.addAttribute("children", children);
            model.addAttribute("user", user);
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


    // 자녀들 프로필 모두 조회
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


    @GetMapping("/addPage")
    public String childAddPage(HttpSession session, Model model) {
        // 세션에서 사용자 이메일을 가져옴
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        return "childAdd";
    }

    // 자녀 추가
    @PostMapping("/addPage")
    @ResponseBody
    public ChildResultDto addChild(@RequestBody ChildAddDto childAddDto, HttpSession session) {
        ChildResultDto childResultDto = new ChildResultDto();
        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);  // 이메일로 사용자 정보 조회
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        Long userId = user.getUserId();

        ChildDto childDto = childAddDto.getChildDto();
        childDto.setUserId(userId);
        childAddDto.setChildDto(childDto); // 변경된 childDto를 다시 설정

        childResultDto = childService.addChild(childAddDto);
        // 세션에 childId 저장
        session.setAttribute("selectedChildId", childResultDto.getChildId());

        System.out.println("-------------------------------------");
        System.out.println(childResultDto.getChildId());
        System.out.println(session.getAttribute("selectedChildId"));
        System.out.println("-------------------------------------");

        return childResultDto;
    }

    @GetMapping("/askMbti")
    public String askMbtiPage(HttpSession session, Model model) {

        String email = (String) session.getAttribute("userEmail");

        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);

        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        model.addAttribute("selectedChildId", selectedChildId);

        return "childAddAskMbti";
    }

    // 자녀 인적사항 조회
    @GetMapping("/detailPage/{childId}")
    public String getChildDetail(@PathVariable Long childId, Model model, HttpSession session) {
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

    // 자녀정보 수정
    @PutMapping("/detailPage/update")
    @ResponseBody
    public ChildResultDto updateChild(@RequestBody ChildUpdateRequestDto childUpdateRequestDto) {
        ChildDto childDto = childUpdateRequestDto.getChildDto();
        Long childId = childDto.getChildId();
        List<String> categoryCodeList = childUpdateRequestDto.getCategoryCodeList();

        childService.updateChild(childDto);
        childService.updateChildFavCategory(childId, categoryCodeList);

        ChildResultDto childResultDto = new ChildResultDto();
        childResultDto.setResult("success");
        return childResultDto;
    }

    // 자녀 삭제
    @DeleteMapping("/detailPage/delete")
    @ResponseBody
    public ResponseEntity<ChildResultDto> deleteChild(@RequestParam("childId") Long childId) {
        try {
            ChildResultDto result = childService.deleteChildById(childId);
            if ("success".equals(result.getResult())) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

