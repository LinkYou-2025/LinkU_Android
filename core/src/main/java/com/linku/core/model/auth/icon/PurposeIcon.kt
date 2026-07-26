package com.linku.core.model.auth.icon

import androidx.annotation.DrawableRes
import com.linku.core.model.auth.Purpose
import com.linku.design.R

val Purpose.iconRes: Int
    @DrawableRes get() = when (this) {
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
