package com.example.linku_android.component

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Size
import com.example.linku_android.R

enum class LinkuNavigationItem(
    val title: String,
    @DrawableRes val icon: Int,
    val size: Size,
    val magnification: Float,
) {
    HOME(
        title = "홈",
        icon = R.drawable.ic_home,
        size = Size(26f, 26f),
        magnification = 1.2f,
    ),
    FILE(
        title = "파일",
        icon = R.drawable.ic_file,
        size = Size(27.85f, 26f),
        magnification = 1.2f,
    ),
    CURATION(
        title = "큐레이션",
        icon = R.drawable.ic_curation,
        size = Size(31f, 13.78f),
        magnification = 0.85f,
    ),
    MY_PAGE(
        title = "마이",
        icon = R.drawable.ic_mypage,
        size = Size(27.83f, 27.83f),
        magnification = 1.2f,
    )
}