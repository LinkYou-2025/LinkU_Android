package com.linku.core.model.auth.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.linku.core.model.auth.Interest
import com.linku.design.R

/** 관심사에 대응하는 Compose 아이콘 Painter를 반환합니다. */
val Interest.iconPainter: Painter
    @Composable
    get() = painterResource(
        id = when (this) {
            Interest.BUSINESS -> R.drawable.ic_interest_business
            Interest.STARTUP -> R.drawable.ic_interest_startup
            Interest.INSIGHTS -> R.drawable.ic_interest_insights
            Interest.DESIGN -> R.drawable.ic_interest_design
            Interest.CURRENT_EVENTS -> R.drawable.ic_interest_current_events
            Interest.STUDY -> R.drawable.ic_interest_study
            Interest.IT -> R.drawable.ic_interest_it
            Interest.CAREER -> R.drawable.ic_interest_career
            Interest.COLLECT -> R.drawable.ic_interest_collect
            Interest.SOCIETY -> R.drawable.ic_interest_society
            Interest.WRITING -> R.drawable.ic_interest_writing
            Interest.PSYCHOLOGY -> R.drawable.ic_interest_psychology
        }
    )
