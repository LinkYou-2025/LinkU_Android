# 프로젝트 개요
이 프로젝트는 Jetpack Compose와 Clean Architecture 기반의 최신 안드로이드 애플리케이션입니다.

# 기술 스택
- UI: Jetpack Compose (Material 3)
- 비동기 처리: Kotlin Coroutines & Flow
- 의존성 주입: Dagger Hilt
- 로컬 DB: Room
- 네트워크: Retrofit2 + OkHttp

# 코딩 규칙 및 아키텍처 (MVVM)
- 컴포저블(Composable) 함수명은 항상 `UpperCamelCase`를 사용하고, 파일명도 동일하게 맞춥니다.
- UI 로직은 Activity나 Fragment에 직접 작성하지 않고, 반드시 ViewModel을 거쳐 상태(StateFlow)를 관찰(Collect)하는 방식으로 작성합니다.
- 데이터 소스(API, DB) 호출은 `data/` 패키지의 Repository 구현체에서 수행하고, UI에서는 `domain/` 패키지의 UseCase를 통해서만 접근합니다.
- 문자열, 색상 등 리소스를 하드코딩하지 말고 `stringResource`, `colorResource` 등을 사용하세요.

# 엄격한 금지 사항 (Strict Prohibitions)
- 절대 `findViewById`나 XML 기반 레이아웃 코드를 생성하지 마세요. 모든 UI는 Compose로 작성합니다.
- ViewModel 클래스 내부에서 `android.content.Context`를 참조하거나 Android 프레임워크 종속성을 추가하지 마세요.
- 로컬 DB(Room) 쿼리나 네트워크 요청을 Main Thread에서 실행하는 코드를 작성하지 마세요.
- `LiveData` 대신 `StateFlow`와 `SharedFlow`만 사용하세요.