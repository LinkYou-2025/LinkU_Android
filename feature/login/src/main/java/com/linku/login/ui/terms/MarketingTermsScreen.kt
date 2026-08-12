package com.linku.login.ui.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.R
import com.linku.login.ui.item.AgreeFooterButton
import com.linku.core.R as CoreR

private val EXTRA_GAP = 20.dp
private val FOOTER_HEIGHT = 50.dp  // 본문의 마지막 내용이 하단 버튼


// 마케팅 수신 동의
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketingTermsScreenComposable(
    alreadyAgreed: Boolean = false,
    onAgreeClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val colorTheme = MaterialTheme.linkuColors
    val scrollState = rememberScrollState()
    val sectionTitles = stringArrayResource(CoreR.array.marketing_terms_section_titles)
    val sectionBodies = stringArrayResource(CoreR.array.marketing_terms_section_bodies)
    val isAtBottom by remember(alreadyAgreed) {
        derivedStateOf {
            if (alreadyAgreed) return@derivedStateOf true
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
                    .heightIn(min = 100.dp) // 상단바 여백 확보를 위한 최소 높이 (폰트 확대 시 자연스럽게 늘어남)
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
                    text = "마케팅 수신동의",
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
                    Text(
                        text = stringResource(CoreR.string.marketing_terms_main_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorTheme.black
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = stringResource(CoreR.string.marketing_terms_introduction),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )

                    sectionTitles.zip(sectionBodies).forEach { (title, body) ->
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorTheme.black
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = body,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = colorTheme.black
                        )
                    }
                }


            }
            Spacer(Modifier.height(EXTRA_GAP))
            //Spacer(Modifier.height(FOOTER_HEIGHT + EXTRA_GAP))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarketingTermsPreview() {
    LinkuPreview {
        MarketingTermsScreenComposable(onAgreeClicked = {}, onBackClicked = {})
    }
}
