package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.ThemeProvider
import com.example.mypage.R
import com.example.mypage.component.NoticeItem

@Composable
fun NoticeScreen(
    navController: NavController
) {
    val notices = listOf(
        "개인정보 이용제공·내역 안내" to """
        안녕하세요. 링큐입니다.
        개인정보 이용 및 제공 내역을 앱 내에서 확인하실 수 있습니다.
        자세한 내용은 개인정보 처리방침을 참고해 주세요.
    """.trimIndent(),

        "서비스 점검 안내" to """
        보다 안정적인 서비스 제공을 위해 시스템 점검이 예정되어 있습니다.
        점검 시간 동안 일부 기능 이용이 제한될 수 있습니다.
    """.trimIndent(),

        "앱 업데이트 안내" to """
        최신 버전 업데이트가 배포되었습니다.
        원활한 이용을 위해 앱을 최신 버전으로 유지해 주세요.
    """.trimIndent(),

        "이벤트 참여 안내" to """
        현재 진행 중인 신규 이벤트가 있습니다.
        앱 내 이벤트 페이지에서 자세한 내용을 확인해 주세요.
    """.trimIndent(),

        "고객센터 운영시간 변경" to """
        고객센터 운영시간이 일부 변경되었습니다.
        문의 전 운영시간을 확인해 주세요.
    """.trimIndent(),

        "이용약관 개정 안내" to """
        더 나은 서비스 제공을 위해 이용약관 일부 내용이 개정되었습니다.
        변경된 내용은 공지사항 및 설정 화면에서 확인 가능합니다.
    """.trimIndent(),

        "계정 보안 안내" to """
        안전한 서비스 이용을 위해 비밀번호를 주기적으로 변경해 주세요.
        타인과 계정 정보를 공유하지 않도록 주의해 주세요.
    """.trimIndent()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(10.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "공지사항",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.5.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(notices) { (title, contents) ->
                NoticeItem(
                    title = title,
                    contents = contents
                )

                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoticeScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        NoticeScreen(navController = navController)
    }
}