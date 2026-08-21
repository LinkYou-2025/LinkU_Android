package com.linku.login.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
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
import com.linku.core.model.auth.icon.iconPainter
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.SelectionCardItem
import com.linku.login.ui.item.StepIndicator


/**
 * 회원가입의 목적 또는 관심사 선택 단계를 공통 그리드 레이아웃으로 표시합니다.
 *
 * [iconPainter]는 Compose 리소스를 읽을 수 있도록 Composable 함수 타입으로 전달받습니다.
 */
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
    iconPainter: @Composable (T) -> Painter,

    // 버튼
    buttonText: String,
    canProceed: Boolean,
    onButtonClick: () -> Unit,

    // 카드 클릭
    onToggle: (T) -> Unit,
) {
    val colorTheme = MaterialTheme.linkuColors
    val captionTopGap = 42.scaler // 그리드 마지막 행과 캡션(복수 선택 안내 문구) 사이 여백

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
                    onClick = onButtonClick
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.scaler) // NOTE SignUpStepLayout(1,2단계)과 StepIndicator 위치 통일
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(colorTheme.white)
        ) {
            // Step 인디케이터
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                StepIndicator(
                    currentStep = currentStep
                )
            }

            Spacer(modifier = Modifier.height(20.scaler)) // NOTE SignUpStepLayout(1,2단계)과 동일 간격

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

            // 3행 고정 그리드: 카드 크기는 지정하지 않고, 가용 세로 공간 ÷ 3으로 자동 계산됨
            // → 캡션 텍스트는 Column의 다음 자식으로 배치해 실제 측정된 높이만큼만 공간을 차지하므로,
            //   그리드가 4행으로 넘어가거나 캡션/버튼 영역을 침범할 일이 없음
            // 열이 화면 폭을 넘치면 가로 스크롤(피크) 발생 — 참고: https://developer.android.com/develop/ui/compose/lists/lazy-grid-lists
            Column(modifier = Modifier.weight(1f)) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.scaler),
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { item ->
                        SelectionCardItem(
                            text = item.displayName,
                            isSelected = selectedItems.contains(item),
                            iconPainter = iconPainter(item),
                            onClick = { onToggle(item) },
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
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
                        .fillMaxWidth()
                        .padding(horizontal = 20.scaler)
                        .padding(top = captionTopGap, bottom = 15.scaler)
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
            iconPainter = { it.iconPainter },
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
            iconPainter = { it.iconPainter },
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
            iconPainter = { it.iconPainter },
            selectedItems = selectedItems,
            buttonText = "다음",
            canProceed = selectedItems.isNotEmpty(),
            onButtonClick = {},
            onToggle = {}
        )
    }
}
