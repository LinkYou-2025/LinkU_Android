package com.example.data.util

import java.net.URI

object DomainIdMapper {

    // tail(호스트) → id
    private val tailToId: LinkedHashMap<String, Long> = linkedMapOf(
        "blog.naver.com" to 2L,
        "cafe.naver.com" to 3L,
        "kin.naver.com" to 4L,
        "shopping.naver.com" to 5L,
        "github.com" to 6L,
        "linkedin.com" to 7L,
        "tistory.com" to 8L,
        "google.com" to 9L,
        "nytimes.com" to 10L,
        "brunch.co.kr" to 11L,
        "velog.io" to 12L,
        "daum.net" to 13L,
        "jobkorea.co.kr" to 14L,
        "wanted.co.kr" to 15L,
        "musinsa.com" to 16L,
        "11st.co.kr" to 17L,
        "instagram.com" to 18L,
        "twitter.com" to 19L,
        "facebook.com" to 20L,
        "naver.com" to 21L,
    )

    // 서버가 내려주는 domain(예: "blog.naver") → id
    private val nameToId: Map<String, Long> = mapOf(
        "blog.naver" to 2L,
        "cafe.naver" to 3L,
        "kin.naver" to 4L,
        "shopping.naver" to 5L,
        "github" to 6L,
        "linkedin" to 7L,
        "tistory" to 8L,
        "google" to 9L,
        "nytimes" to 10L,
        "brunch" to 11L,
        "velog" to 12L,
        "daum" to 13L,
        "jobkorea" to 14L,
        "wanted" to 15L,
        "musinsa" to 16L,
        "11st" to 17L,
        "instagram" to 18L,
        "twitter" to 19L,
        "facebook" to 20L,
        "naver" to 21L,
    )

    private const val INVALID = 1L

    /** URL에서 host를 뽑아 tail 매칭 (서브도메인 처리, www 제거) */
    fun fromUrl(url: String?): Long {
        val host = try {
            val u = url?.trim()
            if (u.isNullOrEmpty()) null
            else URI(if (u.startsWith("http")) u else "https://$u").host
        } catch (_: Exception) { null }

        return fromHost(host)
    }

    /** 서버 domain 문자열(예: "blog.naver")을 id로 */
    fun fromDomainString(domain: String?): Long {
        val key = domain?.lowercase()?.trim() ?: return INVALID
        // 혹시 tail이 들어오면 tail 매칭도 시도
        return nameToId[key] ?: fromHost(key)
    }

    /** URL이 우선, 실패하면 domain 문자열로 보완 */
    fun resolve(url: String?, domain: String?): Long {
        val byUrl = fromUrl(url)
        return if (byUrl != INVALID) byUrl else fromDomainString(domain)
    }

    private fun fromHost(host: String?): Long {
        val h = host?.lowercase()?.removePrefix("www.") ?: return INVALID
        // blog.naver.com 과 같은 정확 매칭 + *.blog.naver.com 같은 하위도메인 대응
        for ((tail, id) in tailToId) {
            if (h == tail || h.endsWith(".$tail")) return id
        }
        return INVALID
    }
}