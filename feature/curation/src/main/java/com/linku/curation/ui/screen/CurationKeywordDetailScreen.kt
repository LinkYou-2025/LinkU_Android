package com.linku.curation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linku.curation.CurationViewModel
import com.linku.curation.R
import com.linku.curation.ui.effect.highlight.RadialGradientCircle
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.LocalFigmaDimens
import com.linku.design.util.scaler

//큐레이션 두번째 카드 - 디테일 화면 제작중...
@Composable
fun CurationKeywordDetailScreen(
    viewModel: CurationViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val nickname by viewModel.nickname.collectAsState()
    val jobName by viewModel.jobName.collectAsState()

    CurationKeywordDetailContent(
        nickname = nickname,
        jobName = jobName,
        onBack = onBack,
        onHome = onHome
    )
}

@Composable
private fun CurationKeywordDetailContent(
    nickname: String,
    jobName: String,
    onBack: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 뒤로가기 버튼 - 위에서 59, 좌로 20
        Image(
            painter = painterResource(com.linku.design.R.drawable.ic_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .offset(x = 20.scaler, y = 59.scaler)
                .size(width = 10.scaler, height = 16.scaler)
                .clickable { onBack() }
        )

        // 홈 버튼 - 위에서 57, 좌로 55
        Image(
            painter = painterResource(com.linku.design.R.drawable.ic_home),
            contentDescription = "홈으로",
            modifier = Modifier
                .offset(x = 55.scaler, y = 57.scaler)
                .size(width = 20.scaler, height = 20.scaler)  // 피그마 사이즈 확인 후 조정
                .clickable { onHome() }
        )

        // 핑크 원
        RadialGradientCircle(
            color = Color(0xFFC800FF),
            alpha = 0.16f,
            size = 640.scaler,
            modifier = Modifier.offset(
                x = (-64).scaler, // TODO: 다현과 조정
                y = (59).scaler
            )
        )
        // 파란 원
        RadialGradientCircle(
            color = Color(0xFF2C6FFF),
            alpha = 0.16f,
            size = 661.scaler,
            modifier = Modifier.offset(
                x = (-36).scaler, // TODO: 다현과 조정
                y = (228).scaler
            )
        )

        // 텍스트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 168.scaler, start = 69.scaler, end = 69.scaler),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${nickname}님과 같은 ${jobName}들은\n이번 달, 이런 키워드를 많이 봤어요",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 25.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(600),
                    textAlign = TextAlign.Center,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2C6FFF),
                            Color(0xFF000208)
                        )
                    )
                )
            )

            Spacer(modifier = Modifier.height(18.scaler))

            Text(
                text = "나와 비슷한 사람들의 관심 키워드",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = Paperlogy.font,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF87898F),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun CurationKeywordDetailScreenPreview() {
    val fixedScale = 390f / 412f

    CompositionLocalProvider(
        LocalFigmaDimens provides { px: Float -> (px * fixedScale).dp }
    ) {
        CurationKeywordDetailContent(
            nickname = "세나",
            jobName = "직장인"  // 프리뷰용 고정값
        )
    }
}