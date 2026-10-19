package com.linku.curation.navigation

import com.linku.core.navigation.LinkuRoute

/**
 * 큐레이션 모듈이 소유하는 내비게이션 라우트와 경로 인자 정의입니다.
 *
 * 하위 화면은 모두 `curation/` 접두사를 사용합니다. 앱의 탭 판정과 두 번 뒤로가기 종료 로직이
 * 이 접두사로 큐레이션 탭을 식별하므로 값을 바꾸지 않습니다.
 */
object CurationRoute {
    /** 큐레이션 탭 루트 경로입니다. */
    const val ROOT = LinkuRoute.CURATION

    const val ARG_MONTH = "month"
    const val ARG_CURATION_ID = "curationId"
    const val ARG_KEYWORD = "keyword"
    const val ARG_YEAR = "year"

    const val DETAIL_PATTERN = "curation/detail/{$ARG_MONTH}/{$ARG_CURATION_ID}"
    const val KEYWORD_DETAIL_PATTERN = "curation/card2/{$ARG_MONTH}"
    const val KEYWORD_LINKS_PATTERN = "curation/keyword_links/{$ARG_KEYWORD}"
    const val REMIND = "curation/card3"
    const val MONTHLY_PATTERN = "curation/monthly/{$ARG_YEAR}"

    /**
     * 월별 큐레이션 상세 화면으로 이동하는 경로를 만듭니다.
     *
     * @param month `yyyy-MM` 형식의 월
     * @param curationId 이동할 큐레이션 ID
     * @return `navigate`에 전달할 경로
     */
    fun detail(month: String, curationId: Long): String =
        "curation/detail/$month/$curationId"

    /**
     * 월별 키워드 상세 화면으로 이동하는 경로를 만듭니다.
     *
     * @param month `yyyy-MM` 형식의 월
     * @return `navigate`에 전달할 경로
     */
    fun keywordDetail(month: String): String = "curation/card2/$month"

    /**
     * 키워드별 링크 목록 화면으로 이동하는 경로를 만듭니다.
     *
     * @param encodedKeyword URI 인코딩이 끝난 키워드
     * @return `navigate`에 전달할 경로
     */
    fun keywordLinks(encodedKeyword: String): String =
        "curation/keyword_links/$encodedKeyword"

    /**
     * 연도별 지난 큐레이션 화면으로 이동하는 경로를 만듭니다.
     *
     * @param year 조회할 연도
     * @return `navigate`에 전달할 경로
     */
    fun monthly(year: Int): String = "curation/monthly/$year"
}
