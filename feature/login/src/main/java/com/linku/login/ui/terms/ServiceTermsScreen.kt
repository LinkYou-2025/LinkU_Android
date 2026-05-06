package com.linku.login.ui.terms


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.font.Paperlogy
import com.linku.design.theme.linkuColors
import com.linku.login.R
import com.linku.login.ui.item.AgreeFooterButton
import com.linku.login.ui.terms.data.ServiceTermsData

private val EXTRA_GAP = 20.dp
private val FOOTER_HEIGHT = 50.dp  // 본문의 마지막 내용이 하단 버튼


// 서비스 이용 약관
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceTermsScreen(
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val colorTheme = MaterialTheme.linkuColors
    val scrollState = rememberScrollState()
    val isAtBottom by remember {
        derivedStateOf {
            if (scrollState.maxValue > 0) {
                scrollState.value >= (scrollState.maxValue - 2) // 2px 정도 여유
            } else {
                // 콘텐츠가 너무 짧아 스크롤이 필요 없는 경우 (상황에 따라 true/false 선택)
                true
            }
        }
    }


    Scaffold(
        topBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // 상단바 여백 확보를 위한 높이
                    .background(colorTheme.white)
            ) {
                // 뒤로가기 버튼
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(start = 20.dp, top = 59.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(16.dp)
                    )
                }


                Text(
                    text = "서비스 이용약관",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 62.dp)
                )
            }
        },
        bottomBar = {
            AgreeFooterButton(
                text = "약관에 동의합니다",
                enabled = isAtBottom,
                onClick = onAgreeClicked
            )
        },
        containerColor = colorTheme.white,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorTheme.gray[100],
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    // 상단 메인 타이틀
                    Text(
                        text = ServiceTermsData.MAIN_TITLE,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorTheme.black
                    )

                    Spacer(Modifier.height(20.dp))

                    // 서문 내용
                    Text(
                        text = ServiceTermsData.INTRODUCTION,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = colorTheme.black
                    )

                    // 각 조항 반복 렌더링
                    ServiceTermsData.sections.forEach { (title, body) ->
                        // 조항과 조항 사이의 간격은 30.dp
                        Spacer(Modifier.height(30.dp))

                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Paperlogy.font,
                            color = colorTheme.black
                        )

                        Spacer(Modifier.height(10.dp)) // 제목과 본문 사이 간격

                        Text(
                            text = body,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            fontFamily = Paperlogy.font,
                            color = colorTheme.black
                        )
                    }
                }
            }
            Spacer(Modifier.height(EXTRA_GAP))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceTermsScreenPreview() {
    LinkuPreview {
        ServiceTermsScreen(
            onAgreeClicked = {},
            onBackClicked = {}
        )
    }
}