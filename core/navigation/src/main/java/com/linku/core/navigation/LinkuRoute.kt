package com.linku.core.navigation

/**
 * 여러 모듈이 함께 사용하는 하단 탭 루트 라우트 문자열의 단일 정의 위치입니다.
 *
 * 값은 기존 문자열 라우트와 동일하므로 이 객체를 사용해도 내비게이션 동작은 바뀌지 않습니다.
 * 특정 모듈 안에서만 쓰는 라우트는 해당 모듈이 자체적으로 소유합니다.
 */
object LinkuRoute {
    /** 로그인 플로우 루트 경로입니다. 로그인 Activity를 여는 자리표시자 목적지로 등록됩니다. */
    const val LOGIN = "login_root"

    /** 홈 탭 루트 경로입니다. */
    const val HOME = "home"

    /** 파일 탭 루트 경로입니다. */
    const val FILE = "file"

    /** 큐레이션 탭 루트 경로입니다. */
    const val CURATION = "curation"

    /** 마이페이지 탭 루트 경로입니다. */
    const val MY_PAGE = "mypage"

    /** 알림 목록 화면 경로입니다. 홈과 마이페이지에서 이동합니다. */
    const val ALARM = "alarm_list"

    /** 알림 설정 화면 경로입니다. 홈과 알림 목록에서 이동합니다. */
    const val ALARM_SETTING = "alarm_setting"

    /** 공지 상세 화면 경로와 인자를 정의합니다. */
    object Notice {
        const val ARG_TARGET_ID = "targetId"
        const val PATTERN = "notice_screen/{$ARG_TARGET_ID}"

        /**
         * 공지 상세 화면으로 이동하는 경로를 만듭니다.
         *
         * @param targetId 이동할 공지 ID
         * @return `navigate`에 전달할 경로
         */
        fun create(targetId: Long): String = "notice_screen/$targetId"
    }
}
