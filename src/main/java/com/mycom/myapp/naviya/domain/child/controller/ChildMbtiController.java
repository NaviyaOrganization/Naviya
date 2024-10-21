package com.mycom.myapp.naviya.domain.child.controller;

import com.mycom.myapp.naviya.domain.child.dto.MBTIScoresDto;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiService;
import com.mycom.myapp.naviya.global.response.ResponseCode;
import com.mycom.myapp.naviya.global.response.ResponseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/mbti")
public class ChildMbtiController {

    @Autowired
    private ChildMbtiService childMbtiService;

    /**
     * 자녀 성향(Mbti) 진단 후 결과 반환
     *
     * @param childId 자녀 ID
     * @param mbtiScoresDto MBTI 점수 데이터
     * @return 자녀의 진단 결과를 JSON 형태로 반환
     */
    @PostMapping("/diagnose/{childId}")
    public ResponseEntity<ResponseForm> diagnoseChildMbti(@PathVariable String childId, @RequestBody MBTIScoresDto mbtiScoresDto) {
        try {
            // MBTI 진단 및 저장
            childMbtiService.createChildMbti(childId, mbtiScoresDto);
            // 성공 응답 반환
            return ResponseEntity.ok(ResponseForm.of(ResponseCode.MBTI_DIAGNOSIS_SUCCESS, "MBTI 진단이 성공적으로 완료되었습니다."));
        } catch (Exception e) {
            // 실패 응답 반환
            return ResponseEntity.badRequest().body(ResponseForm.of(ResponseCode.MBTI_DIAGNOSIS_FAIL, "MBTI 진단에 실패했습니다."));
        }
    }
}