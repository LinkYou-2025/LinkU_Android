package com.linku.file.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.file.R
import java.net.URI

// 앱 전역에서 재사용할 수 있는 정적 Map
val domainLogoMap: Map<String, Int> = mapOf(
    // Naver 계열
    "www.naver.com"      to R.drawable.domain_naver_logo,
    "naver.com"          to R.drawable.domain_naver_logo,
    "blog.naver.com"     to R.drawable.domain_blog_naver_logo,
    "cafe.naver.com"     to R.drawable.domain_cafe_naver_logo,
    "kin.naver.com"      to R.drawable.domain_kin_naver_logo,
    "shopping.naver.com" to R.drawable.domain_shopping_naver_logo,

    // 일반
    "github.com"         to R.drawable.domain_github_logo,
    "www.linkedin.com"   to R.drawable.domain_linkedin_logo,
    "www.tistory.com"    to R.drawable.domain_tistory_logo,
    "www.google.com"     to R.drawable.domain_google_logo,
    "www.nytimes.com"    to R.drawable.domain_nytimes_logo,
    "brunch.co.kr"       to R.drawable.domain_brunch_logo,
    "velog.io"           to R.drawable.domain_velog_logo,
    "www.daum.net"       to R.drawable.domain_daum_logo,
    "www.jobkorea.co.kr" to R.drawable.domain_jobkorea_logo,
    "www.wanted.co.kr"   to R.drawable.domain_wanted_logo,
    "www.musinsa.com"    to R.drawable.domain_musinsa_logo,
    "www.11st.co.kr"     to R.drawable.domain_11st_logo,
    "www.instagram.com"  to R.drawable.domain_instagram_logo,
    "www.facebook.com"   to R.drawable.domain_facebook_logo,

    // 트위터/X
    "twitter.com"        to R.drawable.domain_x_logo,
    "x.com"              to R.drawable.domain_x_logo,
)

/**
 * URL 또는 호스트 문자열에서 경로와 쿼리를 제외한 소문자 호스트를 추출합니다.
 *
 * 스킴이 없는 URL도 일관되게 파싱할 수 있도록 HTTPS URL로 정규화합니다.
 *
 * @param urlOrHost 전체 URL 또는 호스트 문자열입니다.
 * @return 추출한 호스트이며, 입력이 비어 있거나 올바르게 파싱되지 않으면 `null`입니다.
 */
internal fun extractDomainHost(urlOrHost: String): String? {
    val trimmed = urlOrHost.trim()
    if (trimmed.isEmpty()) return null

    val normalized = when {
        trimmed.startsWith("//") -> "https:$trimmed"
        trimmed.contains("://") -> trimmed
        else -> "https://$trimmed"
    }

    return runCatching { URI(normalized).host?.lowercase() }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
}

// URL → Painter 변환 함수
@Composable
fun domainLogoPainterOrNull(urlOrHost: String): Painter? {
    val host = extractDomainHost(urlOrHost) ?: return null

    // 1) 정확 매칭
    domainLogoMap[host]?.let {
        return painterResource(it)
    }

    // 2) 서브도메인 포함 매칭
    // 티스토리처럼 사용자마다 서브도메인이 바뀌는 도메인(계정명.tistory.com)은 맵의 "www." 접두사
    // 키와 절대 일치하지 않으므로, 접두사를 뗀 기준 도메인으로 서브도메인 여부를 비교합니다.
    val matched = domainLogoMap.entries.firstOrNull { (key, _) ->
        val baseKey = key.removePrefix("www.")
        host == baseKey || host.endsWith(".$baseKey")
    } ?: return null

    return painterResource(matched.value)
}
