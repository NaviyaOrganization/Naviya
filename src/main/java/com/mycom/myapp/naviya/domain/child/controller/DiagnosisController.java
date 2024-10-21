package com.mycom.myapp.naviya.domain.child.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiagnosisController {

    @GetMapping("/diagnosisForm")
    public String getDiagnosisForm() {
        return "diagnosisForm";
    }
}
