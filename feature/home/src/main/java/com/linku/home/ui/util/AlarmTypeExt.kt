package com.linku.home.ui.util

import com.linku.core.model.alarm.AlarmType
import com.linku.home.R

/**
 * [AlarmType]에 대응하는 아이콘 드로어블 리소스 ID입니다.
 *
 * 각 알람 유형의 종류에 따라 그에 맞는 아이콘 리소스를 반환합니다.
 */
val AlarmType.iconRes: Int
    get() = when(this) {
        AlarmType.CURATION -> R.drawable.ic_quration
        AlarmType.FOLDER -> R.drawable.ic_folder
        AlarmType.NOTICE -> R.drawable.ic_notice
        AlarmType.LINK -> R.drawable.ic_link
        else -> 0 // All은 해당하는 에셋이 없음
    }