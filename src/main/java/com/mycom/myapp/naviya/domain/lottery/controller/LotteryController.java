package com.mycom.myapp.naviya.domain.lottery.controller;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;
import com.mycom.myapp.naviya.domain.lottery.service.LotteryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<String> submitLotteryEntry(@RequestBody LotteryEntryRequest request) {
        String resultMessage = lotteryService.submitLotteryEntry(request);

        if (resultMessage.equals("이미 응모한 전화번호입니다.")) {
            return ResponseEntity.ok(resultMessage);
        } else if (resultMessage.equals("응모가 마감되었습니다.")) {
            return ResponseEntity.ok(resultMessage);
        } else {
            return ResponseEntity.ok(resultMessage);
        }
    }

    @GetMapping("/form")
    public String showLotteryForm(Model model) {
        model.addAttribute("lotteryRequest", new LotteryEntryRequest());
        return "lotteryForm";
    }

    @PostMapping("/submit-form")
    public String submitLotteryEntryForm(@ModelAttribute LotteryEntryRequest request, Model model) {
        log.info("폼 요청으로 응모 처리 중 - 전화번호: {}, 선택 프로그램: {}",
                request.getPhone(), request.getSelectedProgram());
        String resultMessage = lotteryService.submitLotteryEntry(request);
        model.addAttribute("resultMessage", resultMessage);
        return "lotteryResult";
    }

    @GetMapping("/yesterday-entries")
    @ResponseBody
    public List<String> getYesterdayEntries() {
        log.info("어제의 응모 내역을 조회합니다.");
        return lotteryService.getCachedMaskedEntries();
    }
}