package com.mycom.myapp.naviya.domain.book.service;

import com.mycom.myapp.naviya.domain.child.dto.*;
import com.mycom.myapp.naviya.domain.child.entity.Child;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbti;
import com.mycom.myapp.naviya.domain.child.entity.ChildMbtiHistory;
import com.mycom.myapp.naviya.domain.child.repository.*;
import com.mycom.myapp.naviya.domain.child.service.ChildMbtiServiceImpl;
import com.mycom.myapp.naviya.domain.user.entity.User;
import com.mycom.myapp.naviya.domain.user.repository.UserRepository;
import com.mycom.myapp.naviya.global.mbti.entity.Mbti;
import com.mycom.myapp.naviya.global.mbti.repository.MbtiRepository;
import com.mycom.myapp.naviya.global.mbti.service.MbtiDiagnosisDataQuartzService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildMbtiServiceImplTest {

    // 테스트할 서비스 클래스 주입
    @InjectMocks
    private ChildMbtiServiceImpl childMbtiService;

    // Mock 객체로 선언하여 외부 의존성을 격리
    @Mock
    private ChildMbtiRepository childMbtiRepository;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private MbtiRepository mbtiRepository;
    @Mock
    private ChildMbtiHistoryRepository childMbtiHistoryRepository;
    @Mock
    private MbtiDiagnosisDataQuartzService mbtiDiagnosisDataQuartzService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChildBookLikeRepository childBookLikeRepository;
    @Mock
    private ChildBookDisLikeRepository childBookDislikeRepository;
    @Mock
    private ChildFavorCategoryRepository childFavorCategoryRepository;



    private Long childId;
    private Child testChild;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 자녀 ID와 자녀 객체 초기화
        childId = 1L;
        testChild = new Child();
        testChild.setChildId(childId);
    }

    @Test
    void testCreateChildMbti() {
        // given: 테스트 데이터 및 Mock 설정
        MBTIScoresDto scores = new MBTIScoresDto("I", "S", "T", "P");

        // 자녀 조회 시 testChild 객체 반환 설정
        when(childRepository.findById(childId)).thenReturn(Optional.of(testChild));

        // 자녀의 기존 MBTI 데이터가 없는 경우 null 반환 설정
        when(childMbtiRepository.findChildMbtiBycChildAndDeletedAt(testChild)).thenReturn(null);

        // Mbti 엔티티 저장 동작 모의: 인자로 받은 객체 그대로 반환
        when(mbtiRepository.save(any(Mbti.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ChildMbti 엔티티 저장 동작 모의: 인자로 받은 객체 그대로 반환
        when(childMbtiRepository.save(any(ChildMbti.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ChildMbtiHistory 엔티티 저장 동작 모의: 인자로 받은 객체 그대로 반환
        when(childMbtiHistoryRepository.save(any(ChildMbtiHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when: 테스트하려는 메서드 호출
        childMbtiService.createChildMbti(childId, scores);

        // then: 각 Mock 객체의 메서드가 예상대로 호출되었는지 검증
        verify(childRepository, times(1)).findById(childId); // 자녀 조회 확인
        verify(childMbtiRepository, times(1)).findChildMbtiBycChildAndDeletedAt(testChild); // 기존 MBTI 조회 확인
        verify(mbtiRepository, times(1)).save(any(Mbti.class)); // 새 MBTI 엔티티 저장 확인
        verify(childMbtiRepository, times(1)).save(any(ChildMbti.class)); // ChildMbti 엔티티 저장 확인
        verify(childMbtiHistoryRepository, times(1)).save(any(ChildMbtiHistory.class)); // 히스토리 엔티티 저장 확인
    }
    @Test
    void testCalculateMBTIWithScores() {
        // given
        MBTIScoresDto scores = new MBTIScoresDto("I", "S", "T", "P");

        // when
        String result = childMbtiService.calculateMBTI(scores);

        // then
        assertEquals("ISTP", result);
    }

    @Test
    void testCalculateMBTIWithMbtiEntity() {
        // given
        Mbti mbti = new Mbti();
        mbti.setEiType(-20);  // I
        mbti.setSnType(-20);  // S
        mbti.setTfType(-20);  // T
        mbti.setJpType(20);   // P

        // when
        String result = childMbtiService.calculateMBTI(mbti);

        // then
        assertEquals("ISTP", result);
    }

    @Test
    void testGetChildMbtiHistory() {
        // given
        Long childId = 1L;
        ChildDto childDto = new ChildDto();
        List<ChildMbtiHistoryDto> historyList = List.of(new ChildMbtiHistoryDto());
        when(childRepository.findChildDtoById(childId)).thenReturn(childDto);
        when(childMbtiHistoryRepository.findMbtiHistoryByChildId(childId)).thenReturn(historyList);

        // when
        ChildWithMbtiHistoryDto result = childMbtiService.getChildMbtiHistory(childId);

        // then
        assertThat(result.getChild()).isEqualTo(childDto);
        assertThat(result.getMbtiHistory()).isEqualTo(historyList);
    }

    @Test
    void testSoftDelete() {
        // given
        Long childId = 1L;
        Child child = new Child();
        when(childRepository.findById(childId)).thenReturn(Optional.of(child));

        // when
        childMbtiService.softdelete(childId);

        // then
        verify(childMbtiRepository, times(1)).updateDeletedAtForChild(eq(child), any());
    }

    @Test
    void testExistsChildMbti() {
        // given
        Long childId = 1L;
        when(childMbtiRepository.isDeletedByChildId(childId)).thenReturn(Optional.of(true));

        // when
        boolean result = childMbtiService.existsChildMbti(childId);

        // then
        assertTrue(result);
    }

    @Test
    void testGetChildMbtiInfo() {
        // given
        Long childId = 1L;
        ChildMbtiDto childMbtiDto = new ChildMbtiDto("name","ENTJ",3,4,5,7);
        when(childRepository.findChildMbtiById(childId)).thenReturn(Optional.of(childMbtiDto));

        // when
        Optional<ChildMbtiDto> result = childMbtiService.getChildMbtiInfo(childId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(childMbtiDto);
    }

    @Test
    void testNavbarInfo_UserExists() {
        // given
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        String email = "test@example.com";
        User user = new User();
        when(session.getAttribute("userEmail")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // when
        childMbtiService.navbarInfo(session, model);

        // then
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    void testNavbarInfo_UserDoesNotExist() {
        // given
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        when(session.getAttribute("userEmail")).thenReturn(null);

        // when
        childMbtiService.navbarInfo(session, model);

        // then
        verify(model, times(1)).addAttribute("user", null);
    }

    @Test
    void testUpdateDeletedAtForExistingRecords() {
        // given: 30일 뒤의 시간 설정
        LocalDateTime expectedFutureTime = LocalDateTime.now().plusDays(30);

        // when: 메서드 호출
        childMbtiService.updateDeletedAtForExistingRecords(testChild);

        // then: 각 레포지토리와 스케줄러가 예상대로 호출되었는지 검증
        verify(childMbtiRepository, times(1)).updateDeletedAtForChild(eq(testChild), any(LocalDateTime.class));
        verify(childMbtiHistoryRepository, times(1)).updateDeletedAtForChild(eq(testChild), any(LocalDateTime.class));
        verify(childBookLikeRepository, times(1)).updateDeletedAtForChild(eq(testChild), any(LocalDateTime.class));
        verify(childBookDislikeRepository, times(1)).updateDeletedAtForChild(eq(testChild), any(LocalDateTime.class));
        verify(childFavorCategoryRepository, times(1)).updateDeletedAtForChild(eq(testChild), any(LocalDateTime.class));

        // 스케줄러 호출 확인
        verify(mbtiDiagnosisDataQuartzService, times(1)).scheduleChildDeletion(eq(testChild), any(LocalDateTime.class));
    }
}
