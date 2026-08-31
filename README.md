# LinkU Android
링큐 프로젝트의 안드로이드 리포지토리입니다.

<br/>

<p align="center">
  
</p>
<br/>

**📌 진행 사항 확인**

- **Notion**에서 자세한 진행사항 보러가기 -> 
  [![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)](https://sapphire-lamprey-5db.notion.site/1f393020f65580e2a504d2e1538e2c73?source=copy_link)
<br>

### 🙌 팀원 소개

|유지민|채윤지|홍지현|문현우|
|:---:|:---:|:---:|:---:|
|<img src="https://github.com/user-attachments/assets/6ff7f9f5-5e8c-40e9-b9c2-8ca96a1d06e2"  width="250" height="210">|<img src="https://github.com/user-attachments/assets/54e0f671-5e36-4aac-b547-7ec0e37811e7"  width="250" height="210">|<img src="https://github.com/Hongji03.png"  width="250" height="210">|<img src="https://github.com/codebidoof.png"  width="250" height="210">|
|[@ugmin1030](https://github.com/ugmin1030)|[@KateteDeveloper](https://github.com/KateteDeveloper)|[@Hongji03](https://github.com/Hongji03)|[@Bidoof](https://github.com/codebidoof)|
<br/>


# Tech Stack

다음은 프로젝트의 구현을 위해 사용하는 기술 스택을 정리한 표입니다.

<div align=center>
    <table>
        <thead>
            <tr>
                <th>이름</th>
                <th>설명</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Kotlin</td>
                <td>프로그래밍 언어</td>
            </tr>
            <tr>
                <td>Jetpack Compose</td>
                <td>인-코드 선언형 앱 설계</td>
            </tr>
            <tr>
                <td>Git</td>
                <td>체계적인 코드 관리 및 협업</td>
            </tr>
        </tbody>
    </table>
</div>

다음과 같은 라이브러리 의존성을 가지고 있습니다.

<div align=center>
    <table>
        <thead>
            <tr>
                <th>이름</th>
                <th>버전</th>
                <th>설명</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Jetpack Navigation</td>
                <td>2.0.21</td>
                <td>화면 전환 관리를 위한 라이브러리</td>
            </tr>
            <tr>
                <td>Hilt</td>
                <td>2.51.1</td>
                <td>의존성 주입을 위한 라이브러리</td>
            </tr>
            <tr>
                <td>Retrofit2</td>
                <td>2.11.0</td>
                <td>HTTP 통신을 위한 라이브러리</td>
            </tr>
            <tr>
                <td>SharedPreference</td>
                <td>1.2.1</td>
                <td>로컬 데이터 저장 라이브러리</td>
            </tr>
            <tr>
                <td>Room</td>
                <td>2.6.1</td>
                <td>로컬 데이터베이스 라이브러리</td>
            </tr>
            <tr>
                <td>Android JUnit</td>
                <td>1.2.1</td>
                <td>단위 테스트를 위한 라이브러리</td>
            </tr>
        </tbody>
    </table>
</div>

본 프로젝트는 멀티 모듈 아키텍쳐를 기반으로, 뷰모델을 사용한 MVVM 디자인 패턴으로 구성합니다.

# Conventions

다음은 본 프로젝트에 기여하는 개발자가 지켜야 할 컨벤션입니다.

## Branch

본 프로젝트는 Gitflow 브랜치 전략을 따릅니다.

<div align=center>
    <img src="https://techblog.woowahan.com/wp-content/uploads/img/2017-10-30/git-flow_overall_graph.png" width=50% alt="브랜치 전략 설명 이미지"/>
</div>

- `master`: 배포 가능한 단위의 브랜치
- `release`: 배포 전 테스트가 가능한 단위의 브랜치
- `develop`: 개발 중인 브랜치
- `feature/#issue_number`: 개발 단위별 브랜치
- `hotfix`: `master` 브랜치의 긴급 버그 수정 브랜치

모든 기능 개발은 다음 흐름을 따릅니다.

1. 개발하고자 하는 기능에 대한 이슈를 등록하여 번호를 발급합니다.
2. `develop` 브랜치로부터 분기하여 이슈 번호를 사용해 이름을 붙인 `feature` 브랜치를 만든 후 작업합니다.
3. 작업이 완료되면 `develop` 브랜치에 풀 요청을 작성하고, 팀원의 동의를 얻으면 병합합니다.

## Commit

커밋은 [Gitmoji](https://gitmoji.dev/)를 사용해 시각적으로 작성합니다. 다음은 본 프로젝트의 커밋 형식입니다. 각 줄 사이에는 빈 줄이 추가로 있음에 주의해주세요.

```text
[깃모지] [제목]

[본문]

[이슈 번호 참조(선택)]
```

예시)

```text
:bug: 버튼 버그 수정

키보드 콜백이 불러지지 않는 버그를 수정

관련 이슈 번호: #123, #234
```

각 깃모지의 의미는 [이 블로그](https://treasurebear.tistory.com/70)를 참고합니다. [Android Studio 제공 플러그인](https://plugins.jetbrains.com/plugin/12383-gitmoji-plus-commit-button)을 사용하여 깃모지를 편리하게 이용할 수 있습니다.

## Issue

이슈는 본 리포지토리에 등록된 목적에 맞는 이슈 템플릿을 사용하여 작성합니다.

- `Feature Template`: 기능 추가를 위한 이슈에 사용
- `Bug Template`: 버그 수정을 위한 이슈에 사용

## Pull Request

풀 요청은 본 리포지토리에 등록된 템플릿을 사용하여 작성합니다.

## Code

코드의 스타일은 [Android 공식문서의 Kotlin 스타일 가이드](https://developer.android.com/kotlin/style-guide?hl=ko)를 최대한 따릅니다. 다음은 주요 네이밍 규칙입니다.

- 작성되는 모든 소스 파일은 UTF-8로 인코딩되어야 합니다.
- 코틀린 파일의 제목은 되도록이면 `PascalCase`를 사용하여야 합니다.
- 컴포저블 함수의 이름은 `PascalCase`, 그 외 함수의 이름은 동사로 시작하는 `camelCase`를 사용하며, 변수명은 `camelCase`를 사용합니다. (람다식을 저장하는 변수도 `camelCase`를 사용합니다.)
- 콜백 함수를 전달하는 변수일 경우 `on`으로 시작합니다. ex) `onButtonClicked`, `onDataLoaded`
- 안드로이드 스튜디오 상의 IDE의 노란 줄에 주의합니다.

# Environment

다음은 본 프로젝트의 안드로이드 개발 환경입니다.

- `targetSDK`: 36, `minSDK`: 26
- Android 16(API 36) 대상 동작은 API 36 기기 또는 에뮬레이터에서 별도 런타임 회귀 검증이 필요합니다.
- 안드로이드 스튜디오 버전: Meerkat | 2024.3.2 또는 그 이상
- 테스트 환경: 안드로이드 스튜디오 제공 에뮬레이터(AVD)
- - 기기명: Pixel 8
  - API 35 (Android 15.0, x86_64)
  - 1080 x 2400 px (412 x 915 dp)

## 로컬 빌드 설정

다음 앱 설정은 키마다 `local.properties`의 non-blank 값, LinkU 전용 환경 변수 순서로 읽습니다.
따라서 Android Studio가 `sdk.dir`만 포함한 `local.properties`를 생성해도 환경 변수 fallback이 동작합니다.

| `local.properties` 키 | 환경 변수 |
| --- | --- |
| `KAKAO_NATIVE_APP_KEY` | `LINKU_KAKAO_NATIVE_APP_KEY` |
| `GOOGLE_WEB_CLIENT_ID` | `LINKU_GOOGLE_WEB_CLIENT_ID` |
| `SERVER_DOMAIN` | `LINKU_SERVER_DOMAIN` |
| `SERVER_HOST` | `LINKU_SERVER_HOST` |
| `API_VERSION` | `LINKU_API_VERSION` |

우선순위는 파일 전체가 아니라 각 키를 기준으로 합니다. `local.properties`에 특정 앱 설정이 없거나
공백이면 표에 매핑된 `LINKU_` 환경 변수를 사용합니다. `sdk.dir`은 Android Studio가 관리하는 파일에
그대로 둘 수 있으며, 파일이 없는 환경에서는 Android SDK 경로를 표준 `ANDROID_HOME` 환경 변수로
제공해야 합니다.

이 환경 변수들은 로컬 파일 복사 없이 빌드 설정을 전달하기 위한 수단입니다. 값은 `BuildConfig` 또는
Android Manifest 등에 포함될 수 있으므로 서버 비밀이나 관리자 토큰을 저장하는 용도로 사용하지 않습니다.
Windows 사용자 환경 변수와 외부 백업 파일도 암호화된 비밀 저장소가 아니며, 동일 사용자 권한의
프로세스가 읽을 수 있습니다.

환경 변수 등록 후에는 Android Studio와 JetBrains Toolbox를 모두 완전히 종료한 뒤 다시 실행해야
새 프로세스가 Windows User 환경 변수를 상속합니다.

## CI 릴리스 AAB 생성 및 서명

`Android Release AAB` workflow는 GitHub Actions에서 수동으로만 실행합니다. 입력한
`version_code`를 release variant에 전달해 AAB와 R8 mapping을 생성하고, 기존 upload key로 AAB를
서명한 다음 `jarsigner -verify -strict` 검증을 통과한 signed AAB만 업로드합니다.

임의의 새 keystore나 key를 생성하지 않습니다. Play App Signing을 사용하는 경우 upload key는
개발자가 Google Play에 AAB를 제출할 때 사용하는 키이며, Google이 최종 APK를 서명하는 app signing
key와는 다른 키입니다.

### `version_code` 입력 계약

- 앞뒤 공백과 불필요한 선행 0 없이, ASCII 10진수로만 구성된 `1..2,100,000,000` 범위의 양의 정수를
  입력합니다(예: `28`).
- 현재 Play Console에 등록된 최대 versionCode보다 크고, 이전에 사용하지 않은 값이어야 합니다.
- workflow는 Play Console을 조회하지 않으므로 값의 고유성과 배포 가능성은 실행자가 확인합니다.
- Gradle은 앞뒤 공백을 제거한 뒤 값을 검증합니다. 누락, 공백만 있는 값, 0, 음수, 소수, 내부 공백,
  숫자가 아닌 값 또는 상한 초과 값은 release 빌드 전에 거부됩니다.
- artifact 이름에는 정리 전 workflow 입력이 사용되므로 ` 28 `이나 `0028`처럼 입력하면 AAB 내부의 정수
  versionCode와 artifact 이름 표기가 달라질 수 있습니다. 따라서 허용되더라도 이런 표기는 사용하지 않습니다.

### 필수 GitHub Actions Secrets

workflow는 다음 11개 Secret이 모두 non-blank인지 빌드 전에 검사합니다. 이름은
`.github/workflows/android-release.yml`의 계약과 정확히 일치해야 합니다.

| 구분 | GitHub Actions Secret | 용도와 취급 원칙 |
| --- | --- | --- |
| 앱 구성 | `LINKU_KAKAO_NATIVE_APP_KEY` | Kakao SDK 초기화와 Manifest/BuildConfig에 전달하는 Android client key |
| 앱 구성 | `LINKU_GOOGLE_WEB_CLIENT_ID` | Google 로그인 요청에 사용하는 OAuth web client ID |
| 앱 구성 | `LINKU_SERVER_DOMAIN` | scheme과 host를 포함하는 HTTP(S) 서버 기준 URL |
| 앱 구성 | `LINKU_SERVER_HOST` | scheme, port, path를 제외한 서버 host 이름 |
| 앱 구성 | `LINKU_API_VERSION` | 서버 기준 URL 뒤에 결합하는 API path version |
| Firebase 구성 | `LINKU_GOOGLE_SERVICES_JSON_BASE64` | `app/google-services.json` 원본 파일의 Base64 표현 |
| AAB 서명 | `LINKU_UPLOAD_KEYSTORE_BASE64` | 기존 upload keystore 파일의 Base64 표현 |
| AAB 서명 | `LINKU_UPLOAD_KEYSTORE_TYPE` | 기존 keystore 형식(예: `JKS` 또는 `PKCS12`) |
| AAB 서명 | `LINKU_UPLOAD_KEYSTORE_PASSWORD` | 기존 upload keystore 비밀번호 |
| AAB 서명 | `LINKU_UPLOAD_KEY_ALIAS` | 기존 upload key alias |
| AAB 서명 | `LINKU_UPLOAD_KEY_PASSWORD` | 기존 upload key 비밀번호 |

앞의 앱 설정 5개는 로컬 빌드에서 사용하는 `LINKU_*` 환경 변수와 같은 이름입니다.
`LINKU_GOOGLE_SERVICES_JSON_BASE64`는 workflow가 임시 runner에서 `app/google-services.json`으로
복원하기 위한 CI 전용 전달 값입니다.

앱 구성값은 BuildConfig, Manifest 또는 Firebase resource로 최종 AAB에 포함될 수 있으므로 서버
비밀, 관리자 token 또는 backend 전용 credential을 넣지 않습니다. 반면 upload keystore와 비밀번호는
외부에 공개해서는 안 되는 배포 credential입니다. Base64는 파일 형식을 문자열로 바꾸는 encoding일
뿐 암호화가 아니므로, Base64 결과도 원본 keystore와 같은 수준으로 보호합니다.

### Workflow 실행 흐름과 실패 경계

1. 11개 Secret이 non-blank인지 검증합니다.
2. `LINKU_GOOGLE_SERVICES_JSON_BASE64`를 임시 runner의 `app/google-services.json`으로 복원합니다.
3. Gradle이 `version_code` 입력 계약을 검증한 뒤
   `:app:bundleRelease -PlinkuVersionCode=<입력값>`으로 R8이 적용된 unsigned AAB와 mapping을 생성합니다.
4. 기존 upload keystore를 runner의 임시 경로에 제한된 권한으로 복원합니다.
5. unsigned AAB와 다른 경로에 signed AAB를 생성합니다.
6. 기존 keystore의 지정 alias를 기준으로 `jarsigner -verify -strict` 검증을 수행합니다.
7. 성공·실패 여부와 관계없이 일반적인 step 종료 경로에서는 임시 keystore를 삭제합니다.
8. 앞 단계가 모두 성공한 경우에만 signed AAB와 R8 mapping을 각각 artifact로 업로드합니다.

누락된 Secret, Base64 decode 오류, release 빌드 실패, keystore type/비밀번호/alias 불일치, 서명 실패
또는 strict 검증 실패가 발생하면 workflow는 실패하며 artifact upload 단계로 진행하지 않습니다. 다만
runner 강제 종료처럼 cleanup step 자체가 실행될 수 없는 상황까지 로컬 삭제를 보장하지는 않습니다.
GitHub-hosted runner는 작업 후 폐기되지만, Secret 원본의 별도 보관·회수·회전 정책은 저장소 밖에서
관리해야 합니다.

현재 workflow는 복원한 `app/google-services.json`을 별도로 삭제하지 않지만, 이 파일을 artifact로
업로드하지 않으며 GitHub-hosted runner의 작업 공간이 폐기될 때 함께 사라집니다. 향후
self-hosted runner로 변경한다면 workflow에 이 파일의 명시적인 cleanup 단계를 추가해야 합니다.

### 산출물 계약

성공한 run은 입력한 versionCode를 이름에 포함한 다음 두 artifact를 생성합니다.

| Artifact 이름 | 업로드 소스 경로 | 용도 |
| --- | --- | --- |
| `linku-signed-release-vc-<version_code>` | `app/build/outputs/bundle/release/app-release-signed.aab` | Google Play에 제출할 upload key 서명 AAB |
| `linku-mapping-vc-<version_code>` | `app/build/outputs/mapping/release/mapping.txt` | 해당 release의 R8 난독화 stack trace 복원 |

`app-release.aab`은 서명 전 중간 산출물이며 artifact로 업로드하지 않습니다. signed AAB와 mapping은
서로 다른 artifact이므로 mapping 업로드에서만 실패하면 앞서 업로드된 AAB가 실패한 run에 남을 수
있습니다. 일부 step이 성공했더라도 전체 workflow 결론이 실패 또는 취소라면 그 run의 artifact를
배포에 사용하지 않습니다.

### 실행 전후 확인 목록

- GitHub Actions Secrets 11개가 정확한 이름으로 등록되어 있고 공백이 아닌지 확인합니다.
- keystore type, alias와 두 비밀번호가 기존 앱의 upload key 계약과 일치하는지 확인합니다.
- `version_code`가 앞뒤 공백이나 불필요한 선행 0 없이 입력되었고, 현재 Play Console 최대값보다 크며
  아직 사용되지 않은 값인지 확인합니다.
- workflow 전체 결론이 성공인지 확인합니다.
- signed AAB와 mapping artifact 이름의 versionCode가 입력값과 같은지 확인합니다.
- Play 업로드 후 같은 release의 mapping을 보존하고 crash/ANR 분석에 연결합니다.

Secret 값, Base64 결과, keystore 원본과 복원 파일은 저장소·issue·PR·workflow 로그에 추가하지
않습니다. Secret 이름, 주입 위치 또는 artifact 계약을 변경할 때는 workflow와 이 문서를 같은
변경에서 함께 갱신합니다.
