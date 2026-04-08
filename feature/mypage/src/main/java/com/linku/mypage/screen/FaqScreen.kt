package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R
import com.linku.mypage.component.FaqItem

data class Faq(
    val id: Int,
    val question: String,
    val answer: String,
    val category: String
)

@Composable
fun FaqScreen(
    navController: NavController
) {
    var keyword by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("전체") }
    var expandedFaqId by remember { mutableStateOf<Int?>(null) }

    val faqList = listOf(
        Faq(
            id = 1,
            question = "링크에 붙은 별 표시는 무엇인가요?",
            answer = "AI 링크 요약이 되어있는 링크임을 표시하는 마크입니다.",
            category = "링크"
        ),
        Faq(
            id = 2,
            question = "폴더는 어떻게 추가하나요?",
            answer = "메인 화면에서 폴더 추가 버튼을 눌러 새 폴더를 만들 수 있습니다.",
            category = "폴더"
        ),
        Faq(
            id = 3,
            question = "카테고리는 수정할 수 있나요?",
            answer = "카테고리 관리 화면에서 이름 변경 및 삭제가 가능합니다.",
            category = "카테고리"
        ),
        Faq(
            id = 4,
            question = "링크에 붙은 별 표시는 무엇인가요?",
            answer = "AI 링크 요약이 되어있는 링크임을 표시하는 마크입니다.",
            category = "링크"
        ),
        Faq(
            id = 5,
            question = "폴더는 어떻게 추가하나요?",
            answer = "메인 화면에서 폴더 추가 버튼을 눌러 새 폴더를 만들 수 있습니다.",
            category = "폴더"
        ),
        Faq(
            id = 6,
            question = "카테고리는 수정할 수 있나요?",
            answer = "카테고리 관리 화면에서 이름 변경 및 삭제가 가능합니다.",
            category = "카테고리"
        ),
        Faq(
            id = 7,
            question = "카테고리는 수정할 수 있나요?",
            answer = "카테고리 관리 화면에서 이름 변경 및 삭제가 가능합니다.",
            category = "카테고리"
        ),
        Faq(
            id = 8,
            question = "링크에 붙은 별 표시는 무엇인가요?",
            answer = "AI 링크 요약이 되어있는 링크임을 표시하는 마크입니다.",
            category = "링크"
        ),
        Faq(
            id = 9,
            question = "폴더는 어떻게 추가하나요?",
            answer = "메인 화면에서 폴더 추가 버튼을 눌러 새 폴더를 만들 수 있습니다.",
            category = "폴더"
        ),
        Faq(
            id = 10,
            question = "카테고리는 수정할 수 있나요?",
            answer = "카테고리 관리 화면에서 이름 변경 및 삭제가 가능합니다.",
            category = "카테고리"
        )
    )

    val filteredFaqList = faqList.filter { faq ->
        val matchesFilter = selectedFilter == "전체" || faq.category == selectedFilter
        val matchesKeyword =
            keyword.isBlank() ||
                    faq.question.contains(keyword, ignoreCase = true) ||
                    faq.answer.contains(keyword, ignoreCase = true)

        matchesFilter && matchesKeyword
    }

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
                text = "FAQ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(LocalColorTheme.current.white)
                .then(
                    if (isFocused) {
                        Modifier.border(
                            width = 1.dp,
                            brush = LocalColorTheme.current.maincolor,
                            shape = RoundedCornerShape(18.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo_gray),
                contentDescription = null,
                modifier = Modifier.size(width = 24.dp, height = 17.dp)
            )

            Spacer(modifier = Modifier.width(13.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (keyword.isBlank()) {
                    Text(
                        text = "궁금한 내용을 검색해보세요.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.gray[400],
                    )
                }

                BasicTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.black,
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.75.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("전체", "카테고리", "폴더", "링크", "기타").forEach { filter ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = if (selectedFilter == filter) {
                                LocalColorTheme.current.gray[800]
                            } else {
                                LocalColorTheme.current.white
                            }
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedFilter == filter) {
                            LocalColorTheme.current.white
                        } else {
                            LocalColorTheme.current.gray[800]
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // FAQ 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            )
        ) {
            items(
                items = filteredFaqList,
                key = { it.id }
            ) { faq ->
                FaqItem(
                    question = faq.question,
                    answer = faq.answer,
                    expanded = expandedFaqId == faq.id,
                    onToggle = {
                        expandedFaqId = if (expandedFaqId == faq.id) null else faq.id
                    }
                )
            }
        }

        // 하단 피드백 보내기
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LocalColorTheme.current.white
                        )
                    )
                )
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "찾으시는 질문이 없으신가요?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = LocalColorTheme.current.gray[700]

            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.maincolor)
                    .padding(top = 15.dp, start = 23.5.dp, end = 28.5.dp, bottom = 15.dp)
            ) {
                Text(
                    text = "피드백 보내기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.white
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFaqScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        FaqScreen(navController = navController)
    }
}