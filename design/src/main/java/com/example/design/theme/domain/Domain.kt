package com.example.design.theme.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.design.R
import java.net.URI

// 앱 전역에서 재사용할 수 있는 정적 Map
val domainLogoMap: Map<String, Int> = mapOf(
    // Naver 계열
    "www.naver.com"      to R.drawable.ic_domain_naver_logo,
    "naver.com"          to R.drawable.ic_domain_naver_logo,
    "blog.naver.com"     to R.drawable.ic_domain_blog_naver_logo,
    "cafe.naver.com"     to R.drawable.ic_domain_cafe_naver_logo,
    "kin.naver.com"      to R.drawable.ic_domain_kin_naver_logo,
    "shopping.naver.com" to R.drawable.ic_domain_shopping_naver_logo,

    // 일반
    "github.com"         to R.drawable.ic_domain_github_logo,
    "www.linkedin.com"   to R.drawable.ic_domain_linkedin_logo,
    "www.tistory.com"    to R.drawable.ic_domain_tistory_logo,
    "www.google.com"     to R.drawable.ic_domain_google_logo,
    "www.nytimes.com"    to R.drawable.ic_domain_nytimes_logo,
    "brunch.co.kr"       to R.drawable.ic_domain_brunch_logo,
    "velog.io"           to R.drawable.ic_domain_velog_logo,
    "www.daum.net"       to R.drawable.ic_domain_daum_logo,
    "www.jobkorea.co.kr" to R.drawable.ic_domain_jobkorea_logo,
    "www.wanted.co.kr"   to R.drawable.ic_domain_wanted_logo,
    "www.musinsa.com"    to R.drawable.ic_domain_musinsa_logo,
    "www.11st.co.kr"     to R.drawable.ic_domain_11st_logo,
    "www.instagram.com"  to R.drawable.ic_domain_instagram_logo,
    "www.facebook.com"   to R.drawable.ic_domain_facebook_logo,

    // 트위터/X
    "twitter.com"        to R.drawable.ic_domain_x_logo,
    "x.com"              to R.drawable.ic_domain_x_logo,
)

// URL 또는 host 문자열에서 host 추출
private fun extractHost(urlOrHost: String): String? {
    if (!urlOrHost.contains("://")) {
        val cleaned = urlOrHost.trim().lowercase()
        return if (cleaned.contains('.')) cleaned else null
    }
    return try {
        URI(urlOrHost).host?.lowercase()
    } catch (_: Exception) {
        null
    }
}

// URL → Painter 변환 함수
@Composable
fun domainLogoPainterOrNull(urlOrHost: String): Painter? {
    val host = extractHost(urlOrHost) ?: return null

    // 1) 정확 매칭
    domainLogoMap[host]?.let {
        return painterResource(it)
    }

    // 2) 서브도메인 포함 매칭
    val matched = domainLogoMap.entries.firstOrNull { (key, _) ->
        host.endsWith(".$key")
    } ?: return null

    return painterResource(matched.value)
}