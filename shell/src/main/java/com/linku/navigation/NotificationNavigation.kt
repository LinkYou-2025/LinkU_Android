package com.linku.navigation

import androidx.navigation.NavHostController
import com.linku.core.model.alarm.AlarmType
import com.linku.core.navigation.LinkuRoute
import com.linku.curation.navigation.CurationRoute
import com.linku.link.navigation.LinkRoute
import java.util.Calendar

/**
 * 알림 payload에 월 정보가 없으므로 로컬 현재 월을 `yyyy-MM` 형식으로 만듭니다.
 *
 * @return `yyyy-MM` 형식의 현재 월
 */
internal fun currentCurationMonth(): String {
    val cal = Calendar.getInstance()
    return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
}

/**
 * 알림 목록에서 큐레이션 상세 화면으로 이동합니다.
 *
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param targetId 이동할 큐레이션 ID
 */
internal fun navigateToCurationFromAlarm(navigator: NavHostController, targetId: Long) {
    navigator.navigate(CurationRoute.detail(currentCurationMonth(), targetId))
}

/**
 * 푸시 알림 종류에 맞는 화면으로 이동합니다.
 *
 * 홈이 백 스택에 있어야 뒤로가기 시 홈으로 돌아오므로 홈까지만 남기고 이동합니다.
 *
 * @param navigator 화면 전환에 사용할 내비게이션 컨트롤러
 * @param setShowNavBar 하단 내비게이션 바 표시 여부를 갱신하는 함수
 * @param type 알림 종류
 * @param targetId 알림 대상 ID
 */
internal fun navigateByNotification(
    navigator: NavHostController,
    setShowNavBar: (Boolean) -> Unit,
    type: AlarmType,
    targetId: Long,
) {
    when (type) {
        AlarmType.NOTICE -> {
            setShowNavBar(false)
            navigator.navigate(LinkuRoute.Notice.create(targetId)) {
                popUpTo(LinkuRoute.HOME) { inclusive = false }
            }
        }
        AlarmType.LINK -> {
            val userLinkuId = targetId
            navigator.navigate(LinkRoute.linkDetail(userLinkuId)) {
                popUpTo(LinkuRoute.HOME) { inclusive = false }
            }
        }
        AlarmType.FOLDER -> { /* TODO */ }
        AlarmType.CURATION -> {
            setShowNavBar(false)
            navigator.navigate(CurationRoute.detail(currentCurationMonth(), targetId)) {
                popUpTo(LinkuRoute.HOME) { inclusive = false }
            }
        }
        AlarmType.ALL -> Unit
    }
}
