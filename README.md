# Naviya

# Git 브랜치 전략

## 브랜치 종류

### 1. **Main 브랜치 (Main)**
   - **역할**: 실제 제품으로 출시되는 안정된 버전이 배포되는 브랜치입니다.
   - **규칙**:
     - `main` 브랜치에는 직접 커밋하지 않습니다.
     
### 2. **Dev 브랜치 (dev)**
   - **역할**: 다음 출시 버전을 개발하는 메인 개발 브랜치입니다.
   - **규칙**:
     - 기능 개발이 완료되면 `feature` 브랜치를 `dev`에 병합합니다.
   - **병합 대상**: `feature`

### 3. **Feature 브랜치 (feature/기능명)**
   - **역할**: 새로운 기능을 개발하는 브랜치입니다.
   - **규칙**:
     - 새로운 기능을 개발할 때마다 `feature/기능명` 형식으로 브랜치를 생성합니다.
     - 기능 개발이 완료되면 `dev` 브랜치로 병합합니다.
   - **이름 규칙**: `feature/기능명` (예: `feature/login-page`)
   - **병합 대상**: `dev`

## 요약
- **main**: 출시된 안정된 코드만 존재 (최종 제품 버전)
- **dev**: 다음 버전을 개발하는 메인 브랜치
- **feature/기능명**: 새로운 기능 개발

## 커밋 / 이슈 컨벤션

### 커밋 메시지 / 이슈 제목 구조:
- [feat] 회원가입 기능 추가 회원가입 API와 프론트엔드 연동 완료

### 타입 예시:
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정
- **style**: 코드 포맷팅, 세미콜론 누락 등 기능과 상관없는 변경 사항
- **refactor**: 코드 리팩토링 (기능 변경 없음)
- **test**: 테스트 코드 추가/수정
- **chore**: 빌드 업무 수정, 패키지 매니저 설정 등

### 커밋 메시지 / 이슈 제목 작성 규칙:
- 간결하고 명확하게: 한 줄 요약(50자 이내)을 목표로 작성합니다.
- 영어 또는 한국어로 일관성 있게 작성합니다.
- 현재 시제 사용: "추가함" 대신 "추가"와 같은 표현을 사용합니다.
- 변경 목적을 명확히: 왜 이 변경이 필요한지, 무엇을 변경했는지를 간결하게 설명합니다.

## 푸시(Push) 규칙
1. **작업 단위가 끝났을 때 푸시**: 작은 단위의 기능이나 변경 사항이 완성되었을 때 푸시합니다.
2. **기능별/버그 수정별로 푸시**: 각각의 기능이나 수정 사항이 완성될 때마다 따로 커밋하고 푸시합니다.
3. **미리 Pull**: 푸시 전에 항상 최신 코드를 pull하여 병합 충돌을 해결한 후 푸시합니다.

## PR(Pull Request) 규칙
1. **명확한 제목 작성**: PR 제목에 어떤 기능이나 수정 사항인지 명확히 표시합니다.
   - [타입예시] : #이슈 번호 - 제목
3. **설명 추가**: 변경 사항, 테스트 방법, 참조 이슈 등을 PR 설명에 명시합니다.
4. **리뷰어 할당**: 코드 리뷰를 받을 팀원을 할당하여 협업을 원활하게 진행합니다.
