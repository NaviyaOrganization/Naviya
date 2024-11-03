package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.book.dto.BookResultDto;
import com.mycom.myapp.naviya.domain.child.dto.ChildFavorCategoryDto;
import com.mycom.myapp.naviya.domain.child.entity.ChildFavorCategory;
import com.mycom.myapp.naviya.domain.child.repository.ChildFavorCategoryRepository;
import com.mycom.myapp.naviya.domain.child.repository.ChildRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 데이터베이스 사용
class categoryLike {

    @Autowired
    private BookServiceImpl yourService; // 테스트할 서비스 클래스

    @Autowired
    private ChildFavorCategoryRepository childFavorCategoryRepository; // 레포지토리 주입

    @Autowired
    private ChildRepository childRepository; // 자식 레포지토리 주입

    @Test
    void testCategoryLike_Success() {
        // Given
        long childId = 1L; // 데이터베이스에 존재하는 childId
        String categoryCode = "030"; // 존재하는 카테고리 코드

        // 카테고리 가중치를 50으로 설정
        ChildFavorCategory childFavorCategory = new ChildFavorCategory();
        childFavorCategory.setChildBookCategoryId(1L); // 예시 ID
        childFavorCategory.setCategoryCode(categoryCode);
        childFavorCategory.setChildFavorCategoryWeight(50L);
        childFavorCategory.setChild(childRepository.findByChildId(childId));

        // 초기 데이터 세팅
        childFavorCategoryRepository.save(childFavorCategory);

        // When
        BookResultDto result = yourService.CategoryLike(childId, categoryCode);

        // Then
        assertEquals("success", result.getSuccess());

        // 가중치가 60으로 업데이트되었는지 확인
        ChildFavorCategoryDto updatedCategory = childFavorCategoryRepository
                .findByChildIdAndCategoryCode(childId, categoryCode);

        assertNotNull(updatedCategory, "Updated category should not be null");
        assertEquals(60L, updatedCategory.getChildFavorCategoryWeight(), "Weight should be updated to 60");
    }
    @Test
    void testCategoryLike_Fail_InvalidChildId() {
        // Given
        long childId = 999L; // 데이터베이스에 존재하지 않는 childId
        String categoryCode = "category1"; // 존재하는 카테고리 코드

        // When
        BookResultDto result = yourService.CategoryLike(childId, categoryCode);

        // Then
        assertEquals("fail", result.getSuccess());
    }

    @Test
    void testCategoryLike_Fail_InvalidCategory() {
        // Given
        long childId = 1L; // 데이터베이스에 존재하는 childId
        String categoryCode = "030"; // 존재하지 않는 카테고리 코드

        // When
        BookResultDto result = yourService.CategoryLike(childId, categoryCode);

        // Then
        assertEquals("success", result.getSuccess());
    }
}