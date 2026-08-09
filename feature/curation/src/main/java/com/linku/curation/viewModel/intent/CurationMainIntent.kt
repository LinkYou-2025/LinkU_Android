package com.linku.curation.viewModel.intent

/**
 * 큐레이션 메인 화면에서 발생할 수 있는 사용자 인텐트(액션) 집합.
 *
 * [com.linku.curation.viewModel.CurationViewModel]이 이 인텐트를 처리해 상태를 갱신하거나
 * 화면 이동 및 사이드 이펙트를 수행한다.
 */
sealed interface CurationMainIntent{

    data class ClickCurationSection(
        val curationId: Long,
        val month: String,
    ): CurationMainIntent

    data class ClickLastMonthKeyWord(
        val month: String,
    ): CurationMainIntent

    data object ClickUnreadLink: CurationMainIntent

    data class ClickYearHistory(
        val month: String,
    ): CurationMainIntent

    data object Retry : CurationMainIntent
}