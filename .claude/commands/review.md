---
description: 지정한 PR의 변경사항을 분석하고 인라인 코멘트로 코드 리뷰를 게시한다
argument-hint: <PR 번호> 또는 t <PR 번호>
allowed-tools: Bash, Read, Glob, Grep, Edit
---

# /review — LinkU Android PR 코드 리뷰 커맨드

## 말투 모드 결정

`$ARGUMENTS`를 파싱하여 PR 번호와 말투 모드를 결정한다.
- `t <PR번호>` 형식 → **냉철 모드**, 그냥 `<PR번호>` → **따뜻 모드**
- PR 번호는 숫자만 추출한다 (예: `t 153` → PR #153, 냉철 모드)

PR 정보 수집 후 작성자(`author.login`)를 확인한다.
**작성자가 `ugmin1030`이면 `t`를 입력해도 조용히 따뜻 모드로 전환한다. 안내하지 않는다.**

### 따뜻 모드 (기본)
격려하는 말투. 잘한 점을 먼저 언급하고, 개선 제안도 응원하는 톤으로 작성한다.
> 예: "고생했어요! 이 부분은 ~하면 더 좋을 것 같아요 😊"

### 냉철 모드 (`t`)
감정 없이 사실만. 칭찬 없음. 문제점과 수정 방법만 간결하게 나열한다.
> 예: "~는 잘못됐습니다. ~로 수정하십시오."

---

## 진행 순서

1. **PR 정보 수집 및 브랜치 체크아웃**
   - `gh pr view <PR번호> --json title,body,state,files,headRefName,headRefOid,baseRefName,author` — PR 메타데이터
   - `gh pr diff <PR번호>` — 코드 변경사항
   - 워킹 트리가 dirty(`git status --porcelain` 비어있지 않음)이면 **중단**하고 정리 후 재실행 안내
   - PR 브랜치를 로컬에 가져와 체크아웃: `git fetch origin <headRefName> && git checkout <headRefName>`

2. **프로젝트 구조 파악**
   변경된 파일이 속한 모듈과 주변 코드를 읽어 기존 패턴·컨벤션을 파악한다.

3. **코드 리뷰 수행**
   아래 리뷰 기준에 따라 변경사항을 분석한다.

4. **리뷰 결과 출력**
   발견된 사항을 카테고리별로 정리하여 사용자에게 보여준다.
   - 직접 수정 가능한 항목은 🔧 태그를 붙이고, 구체적인 수정 코드를 코드 블록으로 제시한다.

5. **코드 수정 제안**
   🔧 항목이 있으면 "수정 가능한 항목이 N개 있습니다. 코드를 직접 수정할까요?"라고 사용자에게 확인한다.
   - 사용자가 승인하면 PR 브랜치에서 코드를 수정한다.
   - 수정 후 변경사항을 보여주고 **`/done`으로 커밋**하도록 안내 (자동 커밋 금지).

6. **PR 리뷰 제출**
   리뷰 결과 출력 후 자동으로 `gh api`로 발견 사항 하나당 하나의 인라인 코멘트를 해당 파일·라인에 게시한다.
   - `gh api repos/{owner}/{repo}/pulls/<PR번호>/comments` 사용
   - 각 코멘트 필수 필드: `body`, `commit_id`(PR HEAD SHA), `path`, `line`(diff 내 라인 번호), `side`("RIGHT")
   - `commit_id`: `gh pr view <PR번호> --json headRefOid --jq '.headRefOid'`
   - 작성자: `gh pr view <PR번호> --json author --jq '.author.login'`
   - 코멘트 본문: 첫 줄 `@{작성자}` 멘션 → 줄바꿈 → 심각도 태그 + 설명 → `\n\n> 🤖 Claude Code가 작성한 리뷰입니다.`
   - 코멘트 말투도 결정된 모드를 따른다.
   - 👏 Good 항목은 인라인 대신 `gh pr review <PR번호> --comment --body "내용"`으로 전체 코멘트
   - **Approve / Request Changes는 사용자가 명시적으로 요청할 때만 실행**

---

## LinkU 프로젝트 구조 요약 (리뷰 기준 참고)

```
app/          — Application, Navigation, DI 루트
core/         — 도메인 모델(core/model/) + Repository 인터페이스(core/repository/)
data/         — API(data/api/dto/), RepositoryImpl(data/implementation/), DI 모듈(data/di/)
design/       — 공통 UI 컴포넌트, 테마(LinkuTheme, LocalColorTheme)
feature/      — 기능별 모듈 (home, login, mypage, file, curation)
  └─ 각 feature: Screen(Compose) + ViewModel + component/
test/         — 기능별 테스트 모듈
```

**의존성 방향 규칙**: `feature → core ← data` (feature가 data를 직접 import하면 위반)
**현재 UseCase 레이어 없음**: ViewModel이 Repository를 직접 호출하는 것이 현재 패턴

---

## 리뷰 기준

### 1. 정확성 (Correctness)
- 버그 또는 잠재적 버그
- 엣지 케이스 미처리
- 널 안전성 (nullable 처리 누락, `!!` 연산자 무분별 사용)
- 에러 핸들링 누락 또는 에러를 조용히 무시(`.onFailure {}` 빈 블록)
- `runCatching` 이후 성공/실패 처리 누락

### 2. 아키텍처 레이어 규칙 (Architecture)
- `feature/*` 모듈이 `data` 모듈을 직접 import하는지 (`com.linku.data.*` import가 feature 코드에 있으면 위반)
- `core` 모듈이 `data` / `feature` 모듈에 의존하는지 (역방향 의존)
- Repository 구현체(`*RepositoryImpl`)가 `data` 모듈에 있는지, 인터페이스가 `core`에 있는지
- ViewModel이 Repository가 아닌 API를 직접 호출하는지

### 3. Android 특화
- **Compose**: `remember` / `derivedStateOf` 누락, state hoisting 위반, `LaunchedEffect` key 부적절
- **Lifecycle**: UI에서 Flow 구독 시 `collectAsStateWithLifecycle` 사용 여부 (`collect {}` 직접 사용은 메모리 누수)
- **CoroutineScope**: `viewModelScope` 외 `GlobalScope` 사용 금지, `lifecycleScope` 적절성
- **상태 노출 방식**: `mutableStateOf` + `private set` 패턴 vs `StateFlow` 패턴이 기존과 일관적인지
- **Modifier 순서**: `clickable` 전에 `padding`이 오면 터치 영역 오류 가능

### 4. 코드 품질
- **주석처리된 코드 블록** 대량 존재 여부 (`//` 주석으로 막아둔 이전 코드)
- **과도한 Log.d/e** — 프로덕션 빌드에 민감 정보가 찍히는지 (토큰, userId 등)
- **매직 넘버/스트링** — 의미 없는 리터럴(`60_000L`, `"LINKU4003"` 등) 상수화 여부
- **함수 길이** — 100줄 이상 함수는 분리 고려 권고
- **불명확한 네이밍** — `val v`, `val info` 같이 맥락 없는 변수명

### 5. 성능 (Performance)
- 불필요한 리컴포지션 (`remember` 없이 람다 생성, `keys` 미설정)
- 메인 스레드 블로킹 (Dispatcher 지정 없이 I/O 작업)
- 캐시 없는 반복 API 호출 (리컴포지션마다 API 호출)

### 6. 보안 (Security)
- 하드코딩된 API 키 / 토큰
- `Log.d`에 토큰·비밀번호·이메일 출력
- `SharedPreferences` 평문 민감 정보 저장

---

## 리뷰 결과 출력 형식

```
## 리뷰 결과

### 요약
- 전체적인 변경사항 한줄 평가
- 주요 발견 사항: N개 (🔴 Must Fix N / 🟡 Should Fix N / 🟢 Suggestion N)

### 발견 사항

#### [심각도] 카테고리 — 파일명:라인
설명과 개선 제안

#### 🔧 [심각도] 카테고리 — 파일명:라인
설명과 수정 코드
```

### 심각도 구분
- 🔴 **Must Fix**: 버그, 보안 이슈, 데이터 손실, 빌드 오류
- 🟡 **Should Fix**: 아키텍처 위반, 메모리 누수 가능성, 성능 이슈
- 🟢 **Suggestion**: 가독성·스타일 개선, 주석 정리, 상수화 권고
- 👏 **Good**: 잘 작성된 코드, 좋은 설계 판단

---

## 규칙
- 자명한 변경(import 정리, 포맷팅, 단순 텍스트 수정)에는 코멘트하지 않는다.
- 문제를 지적할 때는 반드시 **개선 방안**을 함께 제시한다.
- 좋은 코드가 있으면 👏 으로 언급한다.
- 리뷰는 **한국어**로 작성한다.
- PR 맥락(제목, 본문, 관련 이슈)을 고려하여 리뷰한다.
- **Approve / Request Changes는 Claude가 자동으로 하지 않는다.**
- 코드 수정 후 커밋은 `/done`으로 처리한다. 커맨드 내에서 직접 커밋하지 않는다.
