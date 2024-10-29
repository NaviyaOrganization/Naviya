package com.mycom.myapp.naviya.global.mbti.service;

import com.mycom.myapp.naviya.global.mbti.Dto.MbtiTypeDescriptionDto;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiTypeDto;
import com.mycom.myapp.naviya.global.mbti.Dto.MbtiTypeTagDto;
import com.mycom.myapp.naviya.global.mbti.entity.MbtiType;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiTypeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MbtiTypeService {

    private final MbtiTypeRepository mbtiTypeRepository;

    @Transactional(readOnly = true)
    public MbtiTypeDto getMbtiTypeDtoByType(String type) {
        MbtiType mbtiType = mbtiTypeRepository.findByType(type);

        // 이미지 파일명을 기반으로 이미지 URL 생성
        String image = "/images/" + mbtiType.getImage();

        return MbtiTypeDto.builder()
                .type(mbtiType.getType())
                .image(image)
                .descriptions(mbtiType.getDescriptions().stream()
                        .map(desc -> MbtiTypeDescriptionDto.builder()
                                .description(desc.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .tags(mbtiType.getTags().stream()
                        .map(tag -> MbtiTypeTagDto.builder()
                                .tag(tag.getTag())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}

