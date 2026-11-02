package com.linku.core.deeplink

/**
 * 서버가 반환한 초대 토큰 또는 초대 링크를 공유할 수 있는 HTTPS 링크로 변환합니다.
 *
 * 외부 주소 형식을 해석하고 링크를 조립하는 구현을 호출자로부터 분리합니다.
 */
interface InvitationLinkFactory {

    /**
     * 초대 토큰 또는 링크에서 공유할 초대 링크를 생성합니다.
     *
     * @param tokenOrLink 원시 초대 토큰 또는 소문자 `http://`·`https://` 접두사와 `token` 쿼리를
     * 포함하는 기존 초대 링크
     * @return 현재 환경의 딥링크 호스트를 사용하며 토큰이 URL 인코딩된 HTTPS 초대 링크
     * @throws IllegalArgumentException 입력에서 추출한 초대 토큰이 비어 있거나 공백으로만 구성된 경우
     */
    fun create(tokenOrLink: String): String
}
