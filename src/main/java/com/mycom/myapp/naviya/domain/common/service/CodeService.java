package com.mycom.myapp.naviya.domain.common.service;

import com.mycom.myapp.naviya.domain.common.entity.Code;
import com.mycom.myapp.naviya.domain.common.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

    @Autowired
    private CodeRepository codeRepository;

    public String getCodeNameByCode(String code) {
        return codeRepository.findCodeNameByCode(code);
    }
}
