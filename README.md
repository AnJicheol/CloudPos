# CloudPos


## 🌀 Git Flow 전략

| 브랜치명 | 용도 | 비고 |
|---------|------|------|
| `master` | 실제 배포 브랜치 | 운영용, 코드 리뷰 후 병합 |
| `dev` | 개발 통합 브랜치 | 모든 feature 브랜치가 여기로 병합 |
| `feature/*` | 기능 개발 | `ex) feature/AUTH-SCRUM-20` 형식 |
| `fix/*` | 버그 수정 | |
| `hotfix/*` | 긴급 수정 | |
| `release/*` | 배포 준비 | 테스트 완료 후 master로 병합 |

- ✅ 브랜치 명명 규칙: **`스프린트 이름 + SCRUM-번호` 형식**  
  - 예: `feature/AUTH-SCRUM-20`  
  - 한글 사용 지양 (검색 및 동기화 문제 방지)

<br><br>

## 💬 커밋 컨벤션

| 태그 | 설명 |
|------|------|
| `feat:` | 새로운 기능 추가 |
| `fix:` | 버그 수정 |
| `refactor:` | 리팩토링 (기능 변경 없음) |
| `docs:` | 문서 변경 |
| `style:` | 코드 스타일/포맷 변경 |
| `test:` | 테스트 코드 관련 |
| `chore:` | 설정, 빌드, 패키지 등 기타 변경 |

<br><br>

## 📦 API 응답 포맷


✅ 성공 응답:
```json
{
  "success": true,
  "status": 200,
  "data": {
    // 실제 DTO 값
  }
}
```
❌ 실패 응답:

```json
{
  "success": false,
  "status": 400,
  "data": null
}
```

<br>

## 🚦 HTTP 상태코드 통일

| 코드 | 의미                   | 사용 예                                |
|------|------------------------|----------------------------------------|
| 200  | OK                     | 일반 요청 성공 (GET, POST 요청 등)    |
| 201  | Created                | 자원 생성 완료 (POST 성공 시)         |
| 204  | No Content             | 응답 없음 (DELETE 요청 등)            |
| 400  | Bad Request            | 클라이언트 요청 오류 (유효성 실패 등) |
| 401  | Unauthorized           | 인증 실패 (로그인 필요)               |
| 403  | Forbidden              | 권한 없음 (비인가 요청)               |
| 404  | Not Found              | 리소스 없음 (잘못된 ID 등)           |
| 409  | Conflict               | 중복 충돌 (이메일 중복 등)            |
| 500  | Internal Server Error  | 서버 내부 에러 (처리 불가능한 예외)   |
