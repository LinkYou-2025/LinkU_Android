package com.linku.mypage.intent

import com.linku.core.model.alarm.AlarmSetting
import com.linku.core.model.alarm.AlarmType

// Intent 최상위 인터페이스
sealed interface NotificationIntent

/**
 * LinkU 서비스 내 각종 알림(전체, 링크, 폴더, 큐레이션, 공지사항)의 설정 상태를
 * 변경하기 위한 인텐트 인터페이스입니다.
 *
 * @property enabled 변경하고자 하는 알림의 활성화 상태값
 * @property alarmType 변경 대상이 되는 알림의 종류 ([AlarmType])
 */
sealed interface LinkuNotificationIntent : NotificationIntent {
    val enabled: Boolean
    val alarmType: AlarmType

    fun reduce(setting: AlarmSetting): AlarmSetting
}

data class ToggleAll(
    override val enabled: Boolean
) : LinkuNotificationIntent {
    override val alarmType = AlarmType.ALL

    override fun reduce(setting: AlarmSetting): AlarmSetting {
        return setting.copy(
            isAllEnabled = enabled,
            isLinkEnabled = enabled,
            isFolderEnabled = enabled,
            isCurationEnabled = enabled,
            isNoticeEnabled = enabled
        )
    }
}

data class ToggleLink(
    override val enabled: Boolean
) : LinkuNotificationIntent {
    override val alarmType = AlarmType.LINK

    override fun reduce(setting: AlarmSetting): AlarmSetting {
        return setting.copy(
            isLinkEnabled = enabled
        )
    }
}

data class ToggleFolder(
    override val enabled: Boolean
) : LinkuNotificationIntent {
    override val alarmType = AlarmType.FOLDER

    override fun reduce(setting: AlarmSetting): AlarmSetting {
        return setting.copy(
            isFolderEnabled = enabled
        )
    }
}

data class ToggleCuration(
    override val enabled: Boolean
) : LinkuNotificationIntent {
    override val alarmType = AlarmType.CURATION

    override fun reduce(setting: AlarmSetting): AlarmSetting {
        return setting.copy(
            isCurationEnabled = enabled
        )
    }
}

data class ToggleNotice(
    override val enabled: Boolean
) : LinkuNotificationIntent {
    override val alarmType = AlarmType.NOTICE

    override fun reduce(setting: AlarmSetting): AlarmSetting {
        return setting.copy(
            isNoticeEnabled = enabled
        )
    }
}

data object RefreshSystemAlarm : NotificationIntent

data object ConfirmFirstPushPermission : NotificationIntent