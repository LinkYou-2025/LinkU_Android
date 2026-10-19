package com.linku.core.navigation

/**
 * 여러 모듈이 함께 사용하는 하단 탭 루트 라우트 문자열의 단일 정의 위치입니다.
 *
 * 값은 기존 문자열 라우트와 동일하므로 이 객체를 사용해도 내비게이션 동작은 바뀌지 않습니다.
 * 특정 모듈 안에서만 쓰는 라우트는 해당 모듈이 자체적으로 소유합니다.
 */
object LinkuRoute {
    /** 홈 탭 루트 경로입니다. */
    const val HOME = "home"

    /** 파일 탭 루트 경로입니다. */
    const val FILE = "file"

    /** 큐레이션 탭 루트 경로입니다. */
    const val CURATION = "curation"

    /** 마이페이지 탭 루트 경로입니다. */
    const val MY_PAGE = "my_page"
}
