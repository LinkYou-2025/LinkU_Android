package com.example.mypage.screen

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.mypage.R

@Composable
fun AccountSettingScreen() {
    val username = "세나"  // 외부에서 정의한 초기 이름
    val userjob = "대학생"
    var name by remember { mutableStateOf("") }
    var job by remember { mutableStateOf(userjob) }

    val purposeTagRows = listOf(
        listOf("업무자료 아카이빙", "인사이트 모으기"),
        listOf("학업/리포트 정리", "그냥 나중에 읽고싶은 글 저장"),
        listOf("취업·커리어 준비", "사이드 프로젝트/창업 준비"),
        listOf("블로그/콘텐츠 작성 참고용", "자기계발/정보 수집"),
        listOf("기타")
    )

    val contentTagRows = listOf(
        listOf("비즈니스/마케팅", "디자인/크리에이티브"),
        listOf("커리어/채용", "학업/리포트 참고", "시사/트렌드"),
        listOf("심리/자기계발", "글쓰기/콘텐츠 작성", "IT/개발"),
        listOf("스타트업/창업", "그냥 모아두고 싶은 글들"),
        listOf("사회/문화/환경", "책/인사이트 요약")
    )

    var selectedPurposeTags by remember { mutableStateOf(setOf<String>()) }
    var selectedContentTags by remember { mutableStateOf(setOf<String>()) }

    // 드롭다운 옵션 (직업 목록)
    val jobOptions = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    var selectedJob by remember { mutableStateOf(jobOptions[1]) } // "대학생"으로 초기화

    // 변경 사항이 있는지 여부 확인
    val isModified = name != "" && name != username || job != "" && job != userjob

    // 비활성화용 그라데이션 브러시
    val inactiveBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFFD4E1FF),
            Color(0xFFF2CCFF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.width(10.dp)
                )

                Spacer(modifier = Modifier.width(135.dp))

                Text(
                    text = "계정 설정",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black
                )
            }

            Spacer(modifier = Modifier.height(41.75.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "닉네임",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(LocalColorTheme.current.white)
                        .clip(RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 텍스트 입력 필드
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = LocalColorTheme.current.black,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (name.isEmpty()) {
                                    Text(
                                        text = username,
                                        style = TextStyle(
                                            color = LocalColorTheme.current.gray[400],
                                            fontSize = 14.sp
                                        ),
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_delete_gray),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(18.dp)
                                .then(
                                    if (name.isNotEmpty()) Modifier.clickable { name = "" }
                                    else Modifier // 클릭 불가 상태
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                    ) {
                Text(
                    text = "현재 하고 계신 일이나 활동",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                CustomDropdownMenu(
                    options = jobOptions,
                    selectedOption = selectedJob,
                    onOptionSelected = { selectedJob = it },
                    defaultOption = userjob
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "링큐 활용 목적",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = LocalColorTheme.current.black
                    )

                    Text(
                        text = "복수선택",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.blue[200],
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // 선택
            SelectableTag(
                tagRows = purposeTagRows,
                selectedTags = selectedPurposeTags,
                onTagClick = {
                    selectedPurposeTags = if (it in selectedPurposeTags) selectedPurposeTags - it else selectedPurposeTags + it
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "관심 콘텐츠",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        color = LocalColorTheme.current.black
                    )

                    Text(
                        text = "복수선택",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.blue[200],
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // 선택
            SelectableTag(
                tagRows = contentTagRows,
                selectedTags = selectedContentTags,
                onTagClick = {
                    selectedContentTags = if (it in selectedContentTags) selectedContentTags - it else selectedContentTags + it
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isModified) Basic.maincolor else inactiveBrush
                    )
                    .clickable(enabled = isModified) {
                        // TODO: 변경 로직
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "변경하기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun CustomDropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    menuMaxHeight: Dp = 230.dp,
    defaultOption: String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.background(Color.Transparent)) {
        // 드롭다운 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(LocalColorTheme.current.white)
                .border(
                    width = 1.dp,
                    color = LocalColorTheme.current.gray[200],
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 21.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption,
                    color = if (selectedOption == defaultOption)
                        LocalColorTheme.current.gray[400]
                    else
                        LocalColorTheme.current.black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 1.dp)
                )
                Icon(
                    painter = painterResource(
                        if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                    ),
                    contentDescription = "드롭다운"
                )
            }
        }

        // 드롭다운 메뉴(아래로 뜨는 메뉴)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(352.dp)
                .background(LocalColorTheme.current.white, RoundedCornerShape(12.dp))
                .heightIn(max = menuMaxHeight),
            offset = DpOffset(x = 0.dp, y = 10.dp)
        ) {
            options.forEach { option ->
                val isSelected = (option == selectedOption)
                // 직접 커스텀
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOptionSelected(option)
                            expanded = false
                        }
                        .padding(horizontal = 21.dp, vertical = 6.dp), // 👉 원하는 만큼!
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Image(
                            painter = painterResource(R.drawable.ic_checked),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                    } else {
                        Spacer(Modifier.width(25.dp))
                    }
                    BrushText(
                        text = option,
                        brush = if (isSelected) Basic.maincolor else null,
                        color = if (!isSelected) LocalColorTheme.current.gray[800] else Color.Unspecified,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableTag(
    tagRows: List<List<String>>, // 줄마다 보여줄 태그들
    selectedTags: Set<String>,
    onTagClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        tagRows.forEach { rowTags ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowTags.forEach { tag ->
                    val isSelected = tag in selectedTags

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                width = 1.dp,
                                brush = if (isSelected) Basic.maincolor
                                    else
                                        Brush.horizontalGradient(
                                            listOf(LocalColorTheme.current.gray[200], LocalColorTheme.current.gray[200])
                                        ),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .background(LocalColorTheme.current.white)
                            .clickable { onTagClick(tag) }
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                    ) {
                        BrushText(
                            text = tag,
                            brush = if (isSelected) Basic.maincolor else null,
                            color = if (!isSelected) LocalColorTheme.current.gray[700] else Color.Unspecified,
                            style = TextStyle(
                                fontSize = 13.9.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrushText(
    text: String,
    brush: Brush? = null,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    modifier: Modifier = Modifier,
) {
    if (brush != null) {
        BasicText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(brush = brush, fontSize = style.fontSize, fontWeight = style.fontWeight)) {
                    append(text)
                }
            },
            style = style,
            modifier = modifier,
        )
    } else {
        BasicText(
            text = text,
            style = style.copy(color = color),
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSettingScreen() {
    AccountSettingScreen()
}