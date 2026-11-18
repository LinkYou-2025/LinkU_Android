package com.linku.core.usecase

import com.linku.core.preference.AuthPreference
import com.linku.core.repository.CurationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetCurationStartYearUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val authPreference: AuthPreference

) {
    suspend operator fun invoke(currentYear: Int): Int {
        authPreference.getJoinedYear()?.let {
            return it.coerceIn(MIN_YEAR, currentYear)
        }

        // 만약 그럴 일은 없겠지만, 지정된 값이 없다면? 연도별 조회로 추정하게...
        val years = (MIN_YEAR..currentYear).toList()
        val results = coroutineScope {
            years.map { year -> async { year to curationRepository.getHistory(year) } }.awaitAll()
        }

        return results.firstOrNull { (_, result) -> result.isSuccess && result.getOrNull()?.any { it.curationId != null } == true }?.first
            ?: currentYear
    }

    private companion object {
        const val MIN_YEAR = 2025
    }
}