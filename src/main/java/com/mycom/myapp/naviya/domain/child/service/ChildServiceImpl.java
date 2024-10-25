package com.mycom.myapp.naviya.domain.child.service;

import com.mycom.myapp.naviya.domain.child.dto.ChildAddDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavCategoryDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildResultDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildFavorCategory;
import com.mycom.myapp.naviya.domain.child.repository.ChildFavorCategoryRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final UserRepository userRepository;
    private final ChildFavorCategoryRepository childFavorCategoryRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(ChildService.class);

    // 나이에 맞는 나이 range 입력
    @Override
    public String determineAgeRange(int age) {
        if (age >= 0 && age <= 3) {
            return "Infant";
        } else if (age >= 4 && age <= 6) {
            return "Preschool";
        } else if (age >= 7 && age <= 12) {
            return "Children";
        } else if (age >= 13 && age <= 17) {
            return "YoungAdult";
        } else {
            return "Adult"; // 혹은 다른 적절한 기본 값으로 설정
        }
    }

    // 자녀들 목록 조회
    @Override
    public List<Child> getChildrenByUserId(Long userId) {
        return childRepository.findByUser_UserId(userId);
    }

    // 자녀 추가
    @Override
    public ChildResultDto addChild(ChildAddDto childAddDto) {
        ChildResultDto childResultDto = new ChildResultDto();

        Child child = new Child();
        ChildDto childDto = childAddDto.getChildDto();
        child.setChildName(childDto.getChildName());
        child.setChildAge(childDto.getChildAge());
        child.setChildGender(childDto.getChildGender());
        child.setChildImage(childDto.getChildImage());
        child.setCodeMbti(childDto.getCodeMbti()); // 초기 MBTI
        child.setChildAgeRange(determineAgeRange(childDto.getChildAge())); // 나이 범위 설정
        child.setCreatedAt(LocalDateTime.now());
        child.setUpdatedAt(LocalDateTime.now());

        User user = userRepository.findById(childDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        child.setUser(user);

        Child savedChild = childRepository.save(child);
        childResultDto.setChildId(savedChild.getChildId());
        childResultDto.setResult("Child profile added successfully!");

        // 선호하는 카테고리 저장
        List<ChildFavCategoryDto> childFavCategoryDtoList = childAddDto.getChildFavCategoryDtoList();
        if (childFavCategoryDtoList != null) {
            List<ChildFavorCategory> favorCategories = childFavCategoryDtoList.stream()
                    .map(dto -> {
                        ChildFavorCategory childFavorCategory = new ChildFavorCategory();
                        childFavorCategory.setChild(savedChild);
                        childFavorCategory.setCategoryCode(dto.getCategoryCode());
                        childFavorCategory.setChildFavorCategoryWeight(10L); // 초기 가중치 설정
                        return childFavorCategory;
                    }).collect(Collectors.toList());

            childFavorCategoryRepository.saveAll(favorCategories);
        }

        return childResultDto;
    }

    // 자녀 한명 상세 조회
    @Override
    public ChildResultDto getChildDetailById(Long childId) {
        ChildResultDto childResultDto = new ChildResultDto();
        Optional<Child> optionalChild = childRepository.findById(childId);

        optionalChild.ifPresentOrElse(
                child -> {
                    ChildDto childDto = new ChildDto();
                    childDto.setChildId(child.getChildId());
                    childDto.setUserId(child.getUser().getUserId());
                    childDto.setChildName(child.getChildName());
                    childDto.setChildAge(child.getChildAge());
                    childDto.setChildGender(child.getChildGender());
                    childDto.setCodeMbti(child.getCodeMbti());
                    childDto.setChildImage(child.getChildImage());

                    // 자녀의 선호 카테고리 정보 생성
                    List<ChildFavCategoryDto> childFavCategoryDtoList = new ArrayList<>();
                    child.getChildFavorCategories().forEach(favCategory -> {
                        ChildFavCategoryDto childFavCategoryDto = new ChildFavCategoryDto();
                        childFavCategoryDto.setChildId(child.getChildId());
                        childFavCategoryDto.setCategoryCode(favCategory.getCategoryCode());
                        childFavCategoryDtoList.add(childFavCategoryDto);
                    });

                    ChildAddDto childAddDto = new ChildAddDto();
                    childAddDto.setChildDto(childDto);
                    childAddDto.setChildFavCategoryDtoList(childFavCategoryDtoList);
                    childResultDto.setChildAddDto(childAddDto);

                    childResultDto.setResult("success");
                },
                () -> {
                    LOGGER.error("No child found with childId: {}", childId);
                    childResultDto.setResult("fail");
                }
        );
        return childResultDto;
    }

    // 자녀 인적사항 수정
    @Transactional
    @Override
    public ChildResultDto updateChild(ChildDto childDto) {
        ChildResultDto childResultDto = new ChildResultDto();
        try {
            // 기존 자녀 정보 조회
            Child child = childRepository.findById(childDto.getChildId())
                    .orElseThrow(() -> new RuntimeException("Child not found with ID: " + childDto.getChildId()));

            // 자녀 정보 업데이트
            child.setChildName(childDto.getChildName());
            child.setChildAge(childDto.getChildAge());
            child.setChildGender(childDto.getChildGender());
            child.setChildImage(childDto.getChildImage());
            child.setCodeMbti(childDto.getCodeMbti());
            child.setUpdatedAt(LocalDateTime.now());
            child.setChildAgeRange(determineAgeRange(childDto.getChildAge())); // 나이 범위 설정

            // 변경 사항 저장
            childRepository.save(child);
            childResultDto.setResult("success");

        } catch (Exception e) {
            e.printStackTrace();
            childResultDto.setResult("자녀 인적사항 수정 실패");
        }
        return childResultDto;
    }

    // 자녀 선호 카테고리 수정
    @Transactional
    @Override
    public ChildResultDto updateChildFavCategory(Long childId, List<String> categoryCodeList) {
        ChildResultDto childResultDto = new ChildResultDto();

        try {
            // 기존 카테고리 삭제
            childFavorCategoryRepository.deleteCategoryByChildId(childId);

            // 자녀 엔티티 조회
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("Child not found"));

            // 새로운 카테고리 추가
            List<ChildFavorCategory> newChildFavorCategories = categoryCodeList.stream()
                    .map(categoryCode -> {
                        ChildFavorCategory favorCategory = new ChildFavorCategory();
                        favorCategory.setChild(child);
                        favorCategory.setCategoryCode(categoryCode);
                        favorCategory.setChildFavorCategoryWeight(50L); // 초기 가중치 설정
                        return favorCategory;
                    })
                    .collect(Collectors.toList());

            // 새로운 카테고리 저장
            childFavorCategoryRepository.saveAll(newChildFavorCategories);

            childResultDto.setResult("success");

        } catch (Exception e) {
            e.printStackTrace();
            childResultDto.setResult("카테고리 수정 실패");
        }

        return childResultDto;
    }

    @Override
    public ChildResultDto deleteChildById(Long childId) {
        ChildResultDto childResultDto = new ChildResultDto();

        try {
            // 자식 엔티티 삭제
            childFavorCategoryRepository.deleteCategoryByChildId(childId);

            // 부모 엔티티 삭제
            childRepository.deleteById(childId);

            childResultDto.setResult("success");
        } catch (Exception e) {
            e.printStackTrace();
            childResultDto.setResult("delete fail");
        }

        return childResultDto;
    }

}
