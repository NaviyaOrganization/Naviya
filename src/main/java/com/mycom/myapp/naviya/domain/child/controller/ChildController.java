package com.mycom.myapp.naviya.domain.child.controller;

import com.mycom.myapp.naviya.domain.child.dto.ChildWithMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildMbtiService childMbtiService;

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
}
