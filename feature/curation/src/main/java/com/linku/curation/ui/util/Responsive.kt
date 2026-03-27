package com.linku.curation.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

//반응형 스케일 헬퍼. TODO : 디자이너에게 반응형시 작은 기기, 보통 기기, 태블릿? 몇 배 확정시켜야 하는지 물어보기.
//임시로 작은 기기: 0.8배, 보통 기기: 1.0배, 태블릿: 최대 1.3배로 설정함.
@Composable
fun rememberScaleFactor(baseWidthDp: Float = 360f): Float {
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels /
            LocalContext.current.resources.displayMetrics.density
    return (screenWidth / baseWidthDp).coerceIn(0.8f, 1.3f)
}