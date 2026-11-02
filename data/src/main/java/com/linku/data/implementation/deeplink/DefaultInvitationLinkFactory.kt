package com.linku.data.implementation.deeplink

import android.net.Uri
import com.linku.core.deeplink.DeepLinkConfig
import com.linku.core.deeplink.InvitationLinkFactory
import javax.inject.Inject

/**
 * 초대 토큰 또는 기존 HTTP(S) 초대 링크를 현재 환경의 HTTPS 공유 링크로 변환합니다.
 *
 * URI 파싱과 쿼리 인코딩은 Android [Uri]에 맡기며, 호스트는 앱이 제공한 [DeepLinkConfig]를
 * 사용합니다. 토큰 발급이나 초대 수락 같은 네트워크 작업은 수행하지 않습니다.
 *
 * @property config 공유 링크에 사용할 환경별 호스트 설정
 */
class DefaultInvitationLinkFactory @Inject constructor(
    private val config: DeepLinkConfig,
) : InvitationLinkFactory {

    /**
     * 입력의 초대 토큰을 추출해 `https://{host}/open?token=...` 링크를 생성합니다.
     *
     * 소문자 `http://` 또는 `https://`로 시작하는 입력은 기존 링크로 해석하며,
     * 나머지 입력은 앞뒤 공백을 제거한 토큰으로 취급합니다.
     *
     * @param tokenOrLink 원시 초대 토큰 또는 `token` 쿼리를 포함한 기존 HTTP(S) 링크
     * @return 토큰이 URL 인코딩된 현재 환경의 HTTPS 초대 링크
     * @throws IllegalArgumentException 토큰이 비어 있거나 기존 링크에 토큰이 없는 경우
     */
    override fun create(tokenOrLink: String): String {
        val invitationToken = extractInvitationToken(tokenOrLink)
        require(invitationToken.isNotBlank()) {
            "Invitation token must not be blank."
        }

        return Uri.Builder()
            .scheme("https")
            .authority(config.host)
            .path("open")
            .appendQueryParameter("token", invitationToken)
            .build()
            .toString()
    }

    /** 기존 링크의 쿼리는 디코딩한 뒤 생성 단계에서 한 번만 인코딩합니다. */
    private fun extractInvitationToken(tokenOrLink: String): String {
        val trimmed = tokenOrLink.trim()

        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            Uri.parse(trimmed).getQueryParameter("token").orEmpty()
        } else {
            trimmed
        }
    }
}
