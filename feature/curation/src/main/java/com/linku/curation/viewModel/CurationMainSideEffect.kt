package com.linku.curation.viewModel

/**
 * 큐레이션 메인 화면의 단발성 부수 효과(Side Effect)를 정의하는 sealed interface.
 *
 * ViewModel에서 [kotlinx.coroutines.channels.Channel]을 통해 전송되며,
 * UI에서 한 번만 처리되어야 하는 이벤트(토스트, 화면 이동 등)를 표현한다.
 */
sealed interface CurationMainSideEffect {

    /**
     * 사용자에게 토스트 메시지를 표시한다.
     *
     * @property message 토스트에 표시할 문자열.
     */
    data class ShowToast(val message: String) : CurationMainSideEffect

    /**
     * 큐레이션 섹션 상세 화면으로 이동한다.
     *
     * @property curationId 이동할 큐레이션의 식별자.
     * @property month 해당 큐레이션의 월 정보 (`yyyy-MM` 형식).
     */
    data class NavigateToCurationSection(
        val curationId: Long,
        val month: String,
    ) : CurationMainSideEffect

    /**
     * 연도별 큐레이션 히스토리 화면으로 이동한다.
     *
     * @property month 기준이 되는 월 정보 (`yyyy-MM` 형식).
     */
    data class NavigateToYearHistory(
        val month: String,
    ) : CurationMainSideEffect

}