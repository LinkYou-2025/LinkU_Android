package com.linku.file

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.linku.design.theme.ThemeProvider
import dagger.hilt.android.AndroidEntryPoint

/**
 * 파일 기능을 독립적으로 실행하며 화면 이동 콜백을 no-op으로 제공하는 테스트 호스트 Activity입니다.
 */
@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            ThemeProvider {
                FileApp(
                    onNavigateToLinkDetail = {},
                    onNavigateToSharedLinkDetail = {},
                )
            }
        }
    }
}
