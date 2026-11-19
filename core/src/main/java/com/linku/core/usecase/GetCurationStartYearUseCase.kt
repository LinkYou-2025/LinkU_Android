package com.linku.core.usecase

import com.linku.core.preference.AuthPreference
import com.linku.core.repository.CurationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 지난 큐레이션 연도 선택에서 볼 수 있는 가장 이른 연도(시작 연도)를 정합니다.
 *
 * 1. DataStore에 저장된 가입 연도가 있으면 그 값을 2025~[currentYear] 범위로 보정해 씁니다.
 * 2. 저장된 값이 없으면(서버 미배포, 이미 로그인된 사용자) 연도별 월간 큐레이션을 동시에 조회해,
 *    큐레이션이 처음 있는 해를 시작 연도로 추정합니다.
 * 3. 추정은 찾은 해보다 앞선 모든 연도의 조회가 성공했을 때만 가입 연도로 저장합니다.
 *    앞선 연도 조회가 네트워크 오류로 실패했다면 실제로는 더 이른 해가 있을 수 있어서 저장하지 않습니다.
 *    저장된 추정값은 다음 로그인 때 서버가 준 가입 연도로 덮어써집니다.
 * 4. 어느 해에도 큐레이션이 없거나 조회가 모두 실패하면 [currentYear]를 반환하고 저장하지 않습니다.
 *
 * @param currentYear 올해. 연도별 조회와 값 보정의 상한. ex) 2026
 * @return 시작 연도. 연도 선택 화살표는 이 값부터 [currentYear]까지만 이동합니다.
 */
class GetCurationStartYearUseCase @Inject constructor(
    private val curationRepository: CurationRepository,
    private val authPreference: AuthPreference
) {
    suspend operator fun invoke(currentYear: Int): Int {
        // ① 저장된 가입 연도가 있으면 그대로 사용. 범위를 벗어난 값은 coerceIn으로 [2025, 올해] 안으로 맞춤
        authPreference.getJoinedYear()?.let {
            return it.coerceIn(MIN_YEAR, currentYear)
        }

        // ② 저장된 값이 없으면 2025년부터 올해까지 연도별 큐레이션을 조회해 시작 연도를 추정
        val years = (MIN_YEAR..currentYear).toList()

        // years.map { async { ... } }: 연도마다 요청을 한꺼번에 출발시킴 : 비동기적으로 할 수 있도록 함.(for문이면 동기적이라 시간 소요 큼)
        // awaitAll(): 모든 요청이 끝날 때까지 기다린 뒤 결과를 입력 순서(2025, 2026 ...) 그대로 돌려줌
        // `year to 결과`: 어느 연도의 응답인지 알 수 있도록 (연도, 결과) 한 쌍으로 묶음
        val results = coroutineScope {
            years.map { year -> async { year to curationRepository.getHistory(year) } }.awaitAll()
        }

        // 서버는 큐레이션이 없는 해에도 12개월 목록을 주고 curationId만 null로 내려줌.
        // 그래서 "목록이 비었나"가 아니라 "curationId가 있는 달이 하나라도 있나"로 판단함
        // indexOfFirst: 조건에 맞는 첫 번째 칸의 번호를 반환 (없으면 -1)
        // { (_, result) -> }: 한 쌍 (연도, 결과)을 풀어서 받음. 연도는 쓰지 않으므로 _ 로 버림
        // any: 12개월 중 큐레이션이 있는 달(curationId != null)이 "하나라도" 있으면 true
        // getOrNull(): 조회가 실패했으면 null, 그래서 `== true`로 실패한 해는 "없음"으로 취급
        val foundIndex = results.indexOfFirst { (_, result) ->
            result.getOrNull()?.any { it.curationId != null } == true
        }
        // 어느 해에도 큐레이션이 없으면 올해를 반환 (믿을 만한 추정이 아니므로 저장하지 않음)
        if (foundIndex == -1) return currentYear

        // (foundYear, _): 찾은 칸의 (연도, 결과) 쌍에서 연도만 꺼냄. 결과는 필요 없어서 _ 로 버림
        val (foundYear, _) = results[foundIndex]

        // take(foundIndex): 찾은 해보다 "앞에 있는" 칸들만 가져옴
        // all: 그 칸들이 "전부" 성공(isSuccess)이어야 true. 한 해라도 실패했으면 false
        // 앞선 해가 실패했다면 더 이른 해가 있었을지도 모르므로, 그 경우엔 추정값을 저장하지 않음
        val isReliable = results.take(foundIndex).all { (_, result) -> result.isSuccess }
        if (isReliable) authPreference.saveJoinedYear(foundYear)
        return foundYear
    }

    private companion object {
        const val MIN_YEAR = 2025
    }
}