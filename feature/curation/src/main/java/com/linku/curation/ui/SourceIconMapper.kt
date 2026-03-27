package com.linku.curation.ui

import android.net.Uri
import com.linku.curation.R

//아이콘 목록 정리(이미지, 글자)

fun resolveSourceIcon(domain: String?): Int {
    val host = (domain ?: "").lowercase()
    // host 예: "blog.naver.com", "instagram.com", "ssg.com" ...
    return when {
        host.contains("blog.naver")      -> R.drawable.ic_blog
        host.contains("naver.com") &&
                host.contains("shopping")    -> R.drawable.ic_naver_shopping
        host.contains("naver.com") &&
                host.contains("cafe")        -> R.drawable.ic_naver_cafe
        host.contains("naver.com") &&
                host.contains("band")        -> R.drawable.ic_naver_band
        host.contains("naver.com") &&
                host.contains("kin")         -> R.drawable.ic_naver_knowledge

        host.contains("naver") -> R.drawable.ic_naver
        //host.contains("naver.com")       -> R.drawable.ic_naver
        host.contains("google.")         -> R.drawable.ic_google
        host.contains("facebook.")       -> R.drawable.ic_facebook
        host.contains("instagram.")      -> R.drawable.ic_instargram
        host.contains("x.com") ||
                host.contains("twitter.")        -> R.drawable.ic_x
        host.contains("threads.")        -> R.drawable.ic_threads
        host.contains("daum.")           -> R.drawable.ic_daum
        host.contains("musinsa.")        -> R.drawable.ic_musinsa
        host.contains("coupang.")        -> R.drawable.ic_coupang
        host.contains("gmarket.") ||
                host.contains("gmart.")          -> R.drawable.ic_gmart
        host.contains("kurly.")          -> R.drawable.ic_kurly
        host.contains("kream.")          -> R.drawable.ic_kream
        host.contains("ssg.")            -> R.drawable.ic_ssg
        host.contains("linkedin.")       -> R.drawable.ic_linkin
        host.contains("jobkorea.")       -> R.drawable.ic_jobkorea
        host.contains("ably.")           -> R.drawable.ic_ably
        host.contains("zigzag.")         -> R.drawable.ic_zigzag
        host.contains("oliveyoung.")     -> R.drawable.ic_oliveyoung
        host.contains("tistory.")        -> R.drawable.ic_tstory
        host.contains("github.")         -> R.drawable.ic_github
        host.contains("nytimes.") ||
                host.contains("nyt.")            -> R.drawable.ic_nytimes
        host.contains("todayhouse.") ||
                host.contains("todayhome.")      -> R.drawable.ic_todayhome
        host.contains("velog.")          -> R.drawable.ic_velog
        host.contains("branch.")         -> R.drawable.ic_branch
        host.contains("behance.") ||
                host.contains("bdhance.")        -> R.drawable.ic_bdhance
        host.contains("onetid.")         -> R.drawable.ic_onetid
        host.contains("cm.")             -> R.drawable.ic_cm
        host.contains("w.")              -> R.drawable.ic_w
        else -> R.drawable.ic_link_null    // 기본 아이콘(없으면 하나 추가)
    }
}

//아이콘 -> 실제 화면에서 보이는 글자 정리.
fun resolveSourceLabel(domain: String?): String {
    val host = (domain ?: "").lowercase()
    return when {
        //네이버 뉴스 분기 추가
        host.contains("news.naver") ||
                host.contains("n.news.naver") -> "네이버 뉴스"

        host.contains("blog.naver") -> "BLOG"
        host.contains("naver.com") &&
                host.contains("shopping") -> "네이버쇼핑"

        host.contains("naver.com") &&
                host.contains("cafe") -> "네이버카페"

        host.contains("naver.com") &&
                host.contains("band") -> "BAND"
        host.contains("naver.com") &&
                host.contains("kin") -> "네이버 지식IN"
        // 위에 아무것도 안 걸렸지만 네이버 계열이면
        host.contains("naver") -> "NAVER"
        host.contains("google.") -> "Google"
        host.contains("facebook.") -> "Facebook"
        host.contains("instagram.") -> "Instagram"
        host.contains("x.com") ||
                host.contains("twitter.") -> "X"

        host.contains("threads.") -> "Threads"
        host.contains("daum.") -> "Daum"
        host.contains("musinsa.") -> "MUSINSA"
        host.contains("coupang.") -> "Coupang"
        host.contains("gmarket.") ||
                host.contains("gmart.") -> "G마켓"

        host.contains("kurly.") -> "컬리"
        host.contains("kream.") -> "KREAM"
        host.contains("ssg.") -> "SSG.COM"
        host.contains("linkedin.") -> "LinkedIn"
        host.contains("jobkorea.") -> "잡코리아"
        host.contains("ably.") -> "에이블리"
        host.contains("zigzag.") -> "지그재그"
        host.contains("oliveyoung.") -> "올리브영"
        host.contains("tistory.") -> "Tistory"
        host.contains("github.") -> "GitHub"
        host.contains("nytimes.") ||
                host.contains("nyt.") -> "NYTimes"

        host.contains("todayhouse.") ||
                host.contains("todayhome.") -> "오늘의집"

        host.contains("velog.") -> "velog"
        host.contains("branch.") -> "브랜치스토리"
        host.contains("behance.") ||
                host.contains("bdhance.") -> "Behance"

        host.contains("onetid.") -> "원티드"
        host.contains("cm.") -> "29CM"
        host.contains("w.") -> "W컨셉"
        else -> (domain ?: "외부 링크")
    }
}
