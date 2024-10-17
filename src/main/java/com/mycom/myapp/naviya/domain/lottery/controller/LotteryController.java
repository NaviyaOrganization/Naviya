package com.mycom.myapp.naviya.domain.lottery.controller;

import com.mycom.myapp.naviya.domain.lottery.dto.LotteryEntryRequest;
import com.mycom.myapp.naviya.domain.lottery.service.LotteryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    // API 방식으로 응모 요청 (JSON 데이터 처리)
    @PostMapping("/submit")
    public String submitLotteryEntry(@RequestBody LotteryEntryRequest request) {
        log.info("API 요청으로 응모 처리 중 - 이메일: {}", request.getEmail());
        return lotteryService.submitLotteryEntry(request);
    }

    // Thymeleaf 페이지에서 응모 폼 렌더링
    @GetMapping("/form")
    public String showLotteryForm(Model model) {
        model.addAttribute("lotteryRequest", new LotteryEntryRequest());
        return "lottery/form";
    }

    // Thymeleaf 페이지에서 응모 폼 제출 처리
    @PostMapping("/submit-form")
    public String submitLotteryEntryForm(@ModelAttribute LotteryEntryRequest request, Model model) {
        log.info("폼 요청으로 응모 처리 중 - 이메일: {}", request.getEmail());
        String resultMessage  = lotteryService.submitLotteryEntry(request);
        model.addAttribute("resultMessage ", resultMessage );
        return "lotteryResult";
    }
}
