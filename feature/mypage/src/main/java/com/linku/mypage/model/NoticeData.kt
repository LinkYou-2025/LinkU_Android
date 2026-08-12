package com.linku.mypage.model

import androidx.annotation.StringRes
import com.linku.mypage.R

/**
 * 앱에 고정 제공되는 공지 한 건의 문자열 리소스 정의입니다.
 *
 * @property id 펼침 상태와 목록 key에 사용하는 안정적인 식별자
 * @property categoryResId 공지 카테고리 문자열 리소스 ID
 * @property titleResId 공지 제목 문자열 리소스 ID
 * @property contentResId 공지 본문 문자열 리소스 ID
 */
internal data class NoticeDefinition(
    val id: String,
    @StringRes val categoryResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val contentResId: Int,
)

/** 서비스 오픈 안내와 개인정보 처리방침 안내를 노출 순서대로 제공합니다. */
internal val noticeList: List<NoticeDefinition> = listOf(
    NoticeDefinition(
        id = "service_open",
        categoryResId = R.string.notice_category_system,
        titleResId = R.string.notice_service_open_title,
        contentResId = R.string.notice_service_open_content,
    ),
    NoticeDefinition(
        id = "privacy_policy",
        categoryResId = R.string.notice_category_system,
        titleResId = R.string.notice_privacy_policy_title,
        contentResId = R.string.notice_privacy_policy_content,
    ),
)
