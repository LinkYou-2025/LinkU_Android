package com.linku.home.util

import com.linku.core.util.UrlValidationResult

/** 링크 형식 검사 결과를 링크 저장 화면의 안내 문구로 변환합니다. */
fun UrlValidationResult.toToastMessage(): String {
    return when (this) {
        UrlValidationResult.MultipleLinks -> "링크는 1개만 등록할 수 있어요."
        UrlValidationResult.InvalidFormat -> "유효하지 않은 링크입니다!"
        UrlValidationResult.Valid -> "유효한 링크입니다!"
    }
}
