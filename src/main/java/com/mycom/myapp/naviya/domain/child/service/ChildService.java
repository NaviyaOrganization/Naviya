package com.mycom.myapp.naviya.domain.child.service;

import com.mycom.myapp.naviya.domain.child.dto.ChildAddDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildResultDto;
import com.mycom.myapp.naviya.domain.child.entity.Child;

import java.util.List;

public interface ChildService {

    String determineAgeRange(int age); // 나이 측정 -> 범위 설정
    List<Child> getChildrenByUserId(Long userId);
    ChildResultDto addChild(ChildAddDto ChildAddDto);

    ChildResultDto getChildDetailById(Long childId);
    ChildResultDto updateChild(ChildDto childDto);
    ChildResultDto updateChildFavCategory(Long childId, List<String> categoryCodeList);

    ChildResultDto deleteChildById(Long childId);
}
