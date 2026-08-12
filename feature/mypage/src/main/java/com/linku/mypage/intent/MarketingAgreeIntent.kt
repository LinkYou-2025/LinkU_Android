package com.linku.mypage.intent

// 마케팅 수신 동의 화면의 사용자 행동/의도
sealed interface MarketingAgreeIntent {
    data object InitialLoad : MarketingAgreeIntent
    data object ToggleMarketingAgree : MarketingAgreeIntent
}
