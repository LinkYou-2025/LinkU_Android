package com.linku.login.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.SelectionItem
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.icon.iconRes
import com.linku.login.ui.item.BottomGradientButton
import com.linku.login.ui.item.SelectionCardItem
import com.linku.login.ui.item.StepIndicator


@Composable
internal fun <T : SelectionItem> SignUpSelectionLayout(
    // 상단 영역
    currentStep: Int,
    titleText: AnnotatedString,
    subText: String,

    // 데이터
    items: List<T>,
    selectedItems: List<T>,

    // 아이콘
    iconRes: (T) -> Int,

    // 버튼
    buttonText: String,
    canProceed: Boolean,
    onButtonClick: () -> Unit,

    // 카드 클릭
    onToggle: (T) -> Unit,
) {
    val colorTheme = MaterialTheme.linkuColors

    Scaffold(
        containerColor = colorTheme.white,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorTheme.white)
                    .padding(bottom = 16.scaler)
            ) {
                BottomGradientButton(
                    text = buttonText,
                    enabled = canProceed,
                    activeGradient = colorTheme.maincolor,
                    inactiveGradient = colorTheme.inactiveColor,
                    onClick = onButtonClick
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.scaler)
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(colorTheme.white)
        ) {
            // Step 인디케이터
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                StepIndicator(
                    currentStep = currentStep
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 타이틀
            Text(
                text = titleText,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(15.scaler))

            // 서브텍스트
            Text(
                text = subText,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = colorTheme.gray[600],
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(28.scaler)) //TODO :프리뷰상 40인거 아는데 그러면 화면이 부족해요... 일단 28로 해놓기는 했습니다..

            // FlowColumn: 열당 3개 고정, 가로 스크롤
            // 참고 : https://developer.android.com/develop/ui/compose/layouts/flow?hl=ko
            // Box(weight(1f))로 감싸서 FlowColumn이 부모 높이 제약을 받도록 함
            // → maxItemsInEachColumn = 3 이 정상 작동하려면 높이 제약 필수
            Box(modifier = Modifier.weight(1f)) {
                FlowColumn(
                    maxItemsInEachColumn = 3,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxHeight()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.scaler)
                ) {
                    items.forEach { item ->
                        SelectionCardItem(
                            text = item.displayName,
                            isSelected = selectedItems.contains(item),
                            iconRes = iconRes(item),
                            onClick = { onToggle(item) }
                        )
                    }
                }
                Text(
                    text = "복수 선택 시, 더 정확한 맞춤 콘텐츠를 제공해요!",
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorTheme.gray[500],
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.scaler)
                        .padding(bottom = 15.scaler)
                )
            }

        }
    }
}


// 1. 미선택 (Purpose 9개)
@Preview
@Composable
private fun SignUpSelectionLayoutPreview() {
    val selectedItems = remember { mutableStateListOf<Purpose>() }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
            },
            subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
            items = Purpose.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedItems,
            buttonText = "다음",
            canProceed = selectedItems.isNotEmpty(),
            onButtonClick = {},
            onToggle = { purpose ->
                if (selectedItems.contains(purpose)) selectedItems.remove(purpose)
                else selectedItems.add(purpose)
            }
        )
    }
}

// 2. 선택됨 (Purpose 3개 선택)
@Preview
@Composable
private fun SignUpSelectionLayoutSelectedPreview() {
    val selectedItems = remember {
        mutableStateListOf(Purpose.CAREER, Purpose.SIDE_PROJECT, Purpose.INSIGHTS)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 목적으로 링크를\n저장하고 싶으신가요?")
            },
            subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
            items = Purpose.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedItems,
            buttonText = "다음",
            canProceed = selectedItems.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}

// 3. 관심사 12개 (4열 구성 확인)
@Preview(showBackground = true, device = Devices.PIXEL_6, name = "레이아웃 - 관심사(12개)")
@Composable
private fun SignUpSelectionLayoutInterestPreview() {
    val selectedItems = remember {
        mutableStateListOf(Interest.IT, Interest.DESIGN, Interest.STARTUP)
    }

    LinkuPreview {
        SignUpSelectionLayout(
            currentStep = 3,
            titleText = buildAnnotatedString {
                append("어떤 분야의 콘텐츠를\n관심 있으신가요?")
            },
            subText = "선택해주신 목적에 맞춰 콘텐츠를 추천해드려요",
            items = Interest.entries.toList(),
            iconRes = { it.iconRes },
            selectedItems = selectedItems,
            buttonText = "다음",
            canProceed = selectedItems.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}