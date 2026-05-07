package com.linku.home.ui.alarm.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.core.model.alarm.AlarmType
import com.linku.design.theme.linkuColors
import com.linku.home.R

/**
 * [AlarmType]에 대응하는 아이콘 드로어블 리소스 ID입니다.
 *
 * 각 알람 유형의 종류에 따라 그에 맞는 아이콘 리소스를 반환합니다.
 */

val AlarmType.iconRes: Painter
    @Composable
    get() = when(this) {
        AlarmType.CURATION -> painterResource( R.drawable.ic_curation_alarm)
        AlarmType.FOLDER -> painterResource(R.drawable.ic_folder)
        AlarmType.NOTICE -> painterResource(R.drawable.ic_notice)
        AlarmType.LINK -> painterResource(R.drawable.ic_link)
        else -> ColorPainter(MaterialTheme.linkuColors.white) // All은 해당하는 에셋이 없음
    }