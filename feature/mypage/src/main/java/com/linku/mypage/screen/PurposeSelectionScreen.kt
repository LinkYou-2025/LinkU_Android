package com.linku.mypage.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.BrushText
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.component.CustomInfoSelectionContent
import com.linku.mypage.component.CustomInfoSelectionItem

private val purposeItems = listOf(
    "취업·커리어 준비",
    "사이드 프로젝트/창업 준비",
    "자기계발/정보 수집",
    "인사이트 모으기",
    "업무자료 아카이빙",
    "학업/리포트 정리",
    "블로그/콘텐츠 작성 참고용",
    "그냥 나중에 읽고 싶은 글 저장",
    "기타"
)

@Composable
fun PurposeSelectionScreen(
    navController: NavController,
    onNextClick: () -> Unit  // TODO: 목적 저장 API 연결
) {
    val colors = MaterialTheme.linkuColors

    val selectedItems = remember { mutableStateListOf<String>() }

    CustomInfoSelectionContent(
        questionContent = {
            Column {
                BrushText(
                    text = "어떤 목적으로 링크를",
                    brush = colors.maincolor,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrushText(
                        text = "저장",
                        brush = colors.maincolor,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )

                    Text(
                        text = "하고 싶으신가요?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black
                    )
                }

                Text(
                    text = "모두 선택해주세요.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black
                )
            }
        },
        selectionContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(purposeItems) { item ->
                    val isSelected = item in selectedItems

                    CustomInfoSelectionItem(
                        text = item,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedItems.remove(item)
                            } else {
                                selectedItems.add(item)
                            }
                        }
                    )
                }
            }
        },
        buttonText = "다음",
        isButtonEnabled = selectedItems.isNotEmpty(),
        onBackClick = { navController.popBackStack() },
        onButtonClick = onNextClick
    )
}

@Preview(showBackground = true)
@Composable
fun PurposeSelectionScreenPreview() {
    val navController = rememberNavController()

    ThemeProvider {
        PurposeSelectionScreen(
            navController = navController,
            onNextClick = {}
        )
    }
}