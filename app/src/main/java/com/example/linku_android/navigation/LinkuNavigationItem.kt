package com.example.linku_android.navigation

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Size
import com.example.linku_android.R

/*
* 메인 레이아웃의 바텀 내비게이션 바에 사용되는 아이템.
* */
enum class LinkuNavigationItem(
    /*
    * title: 아이템 이름, 보일 화면.
    * icon: 아이템 아이콘
    * size: 아이콘의 크기
    * magnification: 아이콘의 배율
    * */
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