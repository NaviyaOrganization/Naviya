package com.mycom.myapp.naviya.domain.child.controller;

import com.mycom.myapp.naviya.domain.child.dto.ChildWithMbtiHistoryDto;
import com.mycom.myapp.naviya.domain.child.dto.MBTIScoresDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiService;
import com.mycom.myapp.naviya.domain.child.service.ChildService;
import com.mycom.myapp.naviya.global.response.ResponseCode;
import com.mycom.myapp.naviya.global.response.ResponseForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/mbti")
public class ChildMbtiController {

    @Autowired
    private ChildMbtiService childMbtiService;
    private ChildService childService;

    /**
     * 자녀 성향(Mbti) 진단 후 결과 반환
     *
     * @param mbtiScoresDto MBTI 점수 데이터
     * @return 자녀의 진단 결과를 JSON 형태로 반환
     */
    @PostMapping("/diagnose")
    public ResponseEntity<ResponseForm> diagnoseChildMbti(HttpSession session, @RequestBody MBTIScoresDto mbtiScoresDto) {
        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        try {
            // MBTI 진단 및 저장
            childMbtiService.createChildMbti(selectedChildId, mbtiScoresDto);
            // 성공 응답 반환
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_DIAGNOSIS_SUCCESS, "MBTI 진단이 성공적으로 완료되었습니다."));
        } catch (Exception e) {
            // 실패 응답 반환
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_DIAGNOSIS_FAIL, "MBTI 진단에 실패했습니다."));
        }
    }

    /**
     * 아이의 MBTI 히스토리 조회
     */
    @GetMapping("/history")
    public ResponseEntity<ResponseForm> getChildMbtiHistory(@PathVariable Long childId) {
        try {
            ChildWithMbtiHistoryDto result = childMbtiService.getChildMbtiHistory(childId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_HISTORY_SUCCESS, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_HISTORY_FAIL, e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseForm> deleteChildMbti(HttpSession session) {
        if (session == null) {
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_DELETE_FAIL, "세션이 유효하지 않습니다."));
        }

        Long selectedChildId = (Long) session.getAttribute("selectedChildId");
        if (selectedChildId == null) {
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_DELETE_FAIL, "선택된 자녀가 없습니다."));
        }

        try {
            childMbtiService.softdelete(selectedChildId);
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_DELETE_SUCCESS));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_DELETE_FAIL, e.getMessage()));
        }
    }


    @GetMapping("/exist")
    public ResponseForm isChildMbtiDeleted(@RequestParam Long childId) {
        boolean isDeleted = childMbtiService.existsChildMbti(childId);
        return ResponseForm.of(ResponseCode.EXAMPLE_SUCCESS, isDeleted);
    }
    @GetMapping("/CategoryExist")
    public ResponseForm BookCategoryOne(@RequestParam Long childId)
    {
        boolean isDeleted = childMbtiService.existsChildMbti(childId);
        return ResponseForm.of(ResponseCode.EXAMPLE_SUCCESS, isDeleted);
    }
}