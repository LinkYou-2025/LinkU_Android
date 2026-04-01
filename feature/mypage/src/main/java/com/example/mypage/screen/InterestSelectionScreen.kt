package com.example.mypage.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.design.BrushText
import com.example.design.theme.LocalColorTheme
import com.example.mypage.component.CustomInfoSelectionContent
import com.example.design.theme.ThemeProvider
import com.example.mypage.component.CustomInfoSelectionItem

@Composable
fun InterestSelectionScreen(
    navController: NavController,
    onFinishClick: () -> Unit
) {
    val interestItems = listOf(
        "비즈니스/마케팅",
        "디자인/크리에이티브",
        "IT/개발",
        "학업/리포트 참고",
        "스타트업/창업",
        "글쓰기/콘텐츠 작성",
        "그냥 모아두고 싶은 글들",
        "커리어/채용",
        "책/인사이트 요약"
    )

    val selectedItems = remember { mutableStateListOf<String>() }

    CustomInfoSelectionContent(
        questionContent = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrushText(
                        text = "어떤 분야의 콘텐츠",
                        brush = LocalColorTheme.current.maincolor,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )

                    Text(
                        text = "에",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.black
                    )
                }

                Text(
                    text = "관심 있으신가요?\n모두 선택해주세요.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.black
                )
            }
        },
        selectionContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(interestItems) { item ->
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

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        },
        buttonText = "완료",
        isButtonEnabled = selectedItems.isNotEmpty(),
        onBackClick = { navController.popBackStack() },
        onButtonClick = onFinishClick
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestSelectionScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        InterestSelectionScreen(
            navController = navController,
            onFinishClick = {}
        )
    }
}