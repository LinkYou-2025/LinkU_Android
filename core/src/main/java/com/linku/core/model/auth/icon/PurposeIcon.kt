package com.linku.core.model.auth.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.core.model.auth.Purpose
import com.linku.design.R

/** 링크 저장 목적에 대응하는 Compose 아이콘 Painter를 반환합니다. */
val Purpose.iconPainter: Painter
    @Composable
    get() = painterResource(
        id = when (this) {
            Purpose.CAREER -> R.drawable.ic_purpose_career
            Purpose.CREATION_REFERENCE -> R.drawable.ic_purpose_creation_reference
            Purpose.INSIGHTS -> R.drawable.ic_purpose_insights
            Purpose.SIDE_PROJECT -> R.drawable.ic_purpose_side_project
            Purpose.STUDY -> R.drawable.ic_purpose_study
            Purpose.LATER_READING -> R.drawable.ic_purpose_later_reading
            Purpose.SELF_DEVELOPMENT -> R.drawable.ic_purpose_self_development
            Purpose.WORK -> R.drawable.ic_purpose_work
            Purpose.OTHERS -> R.drawable.ic_purpose_others
        }
    )
