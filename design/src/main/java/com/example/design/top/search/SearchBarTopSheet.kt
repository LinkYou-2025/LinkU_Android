package com.example.design.top.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.domain.domainLogoPainterOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import androidx.compose.ui.platform.LocalUriHandler
import com.example.design.R

data class FastSearchItem(
    val id: Long,
    val title: String,
    val url: String,
)

// 빠른 링크 검색 탑 시트
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Composable
fun SearchBarTopSheet(
    /*
    * visible: 탑 시트가 보여지는지 여부
    * onDismiss: 탑 시트를 닫을 때 호출되는 콜백
    * onQueryChange: 검색어가 변경될 때 호출되는 콜백
    * onQuerySave: 검색어를 저장할 때 호출되는 콜백
    * onQueryDelete: 검색어를 삭제할 때 호출되는 콜백
    * onQueryClear: 모든 검색어를 삭제할 때 호출되는 콜백
    * fastSearchItems: 빠른 링크 검색 아이템 리스트
    * recentQuerys: 최근 검색 기록 리스트
    * */
    visible: Boolean,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onQuerySave: (String) -> Unit,
    onQueryDelete: (String) -> Unit,
    onQueryClear: () -> Unit,
    onLinkClick: (Long) -> Unit,
    fastSearchItems: List<FastSearchItem> = emptyList(),
    recentQuerys: List<String> = emptyList(),
) {
    // 링큐 색상 테마
    val colors = LocalColorTheme.current

    // 링큐 폰트(paperlogy)
    val paperlogyFont = LocalFontTheme.current.font

    // 키보드 컨트롤러 인스턴스
    val keyboardController = LocalSoftwareKeyboardController.current

    // 입력 텍스트
    var text by remember { mutableStateOf("") }

    // 수정 상태
    var isEditMode by remember { mutableStateOf(false) }

    // 검색창 입장 시 초기화
    LaunchedEffect(Unit) {
        text = ""
        isEditMode = false
    }

    // 입력 변화 디바운스 수집 (2자 이상 + 350ms)
    // - mapLatest: 새 입력이 오면 이전 요청(코루틴 Job) 자동 취소 → 레이스 방지
    LaunchedEffect(Unit) {
        snapshotFlow { text }
            .map { it.trim() }
            .filter { it.length >= 2 }    // 2자 이상일 때만
            .debounce(350)                // 300~400ms 권장, 여기선 350ms
            .distinctUntilChanged()
            .mapLatest { q ->
                onQueryChange(q)          // 최신 입력으로만 호출됨
            }
            .collect { /* no-op */ }
    }


    // 최근 검색 기록 아이템
    @Composable
    fun RecentQueryItem(recentText: String){
        Row (
            modifier = Modifier
                .wrapContentSize()
                .background(
                    shape = RoundedCornerShape(size = 10.dp),
                    color = colors.gray[100]
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 태그 텍스트
            Text(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .noRippleClickable {
                        if (!isEditMode) {
                            text = recentText
                            keyboardController?.hide()
                        }
                    },
                text = recentText,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = paperlogyFont,
                fontWeight = FontWeight.Normal,
                color = colors.gray[800],
                textAlign = TextAlign.Center,
            )

            // 수정 상태에서만 보이는 삭제 버튼
            if(isEditMode){
                Icon(
                    modifier = Modifier
                        .noRippleClickable{
                            onQueryDelete(recentText)
                        },
                    imageVector = Icons.Default.Close,
                    tint = colors.gray[800],
                    contentDescription = null,
                )
            }
        }
    }

    // 빠른 링크 제목 부분 강조 텍스트
    @Composable
    fun HighlightedText(
        suggestion: String,
        query: String
    ) {
        val annotatedString = buildAnnotatedString {
            if (query.isEmpty()) {
                append(suggestion)
                return@buildAnnotatedString
            }

            // query를 모두 찾아내는 정규식
            val regex = Regex("(?i)${Regex.escape(query)}")
            var lastIndex = 0

            regex.findAll(suggestion).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1

                // 검색어 앞부분 그대로 추가
                append(suggestion.substring(lastIndex, start))

                // 검색어 부분 Bold 처리
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(suggestion.substring(start, end))
                }

                lastIndex = end
            }

            // 마지막 남은 부분 추가
            if (lastIndex < suggestion.length) {
                append(suggestion.substring(lastIndex))
            }
        }

        Text(
            text = annotatedString,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = paperlogyFont,
            fontWeight = FontWeight.Normal,
            color = colors.black,
            maxLines = 1, // 최대 1줄
            overflow = TextOverflow.Ellipsis // 잘리면 ... 표시
        )
    }

    // 빠른 링크 검색 아이템
    @Composable
    fun FastSearchItem(fastSearchItem: FastSearchItem){

        val domainImg = domainLogoPainterOrNull(fastSearchItem.url)

        val uri = LocalUriHandler.current

        // 링크 내용
        Row(
            modifier = Modifier
                .noRippleClickable{
                    runCatching { onLinkClick(fastSearchItem.id) }
                },
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            // 도메인 이미지
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        width = 5.dp,
                        color = colors.gray[100],
                        shape = CircleShape
                    )
                    .background(colors.white),
                painter = domainImg?:painterResource(R.drawable.logo_whiteback),
                contentDescription = null
            )

            // 링크 제목
            HighlightedText(
                suggestion = fastSearchItem.title,
                query = text
            )
        }
    }

    // 바탕 Box
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 활성화 시, 뒷 배경 딤 효과
        if (visible) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    // 뒷 배경 딤 효과
                    .background(Color.Black.copy(alpha = 0.3f))
                    // 클릭 시, 닫힘
                    .noRippleClickable { onDismiss() }
            )
        }

        // 위에서 내려오는 Top Sheet 애니메이션
        AnimatedVisibility(
            // 활성화 상태로 애니메이션 입장, 퇴장
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it } // 위에서 시작
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it } // 위로 사라짐
            )
        ) {
            // 검색창 배경
            Surface(
                // 밑 부분만 둥글게
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),

                // 낮은 톤 보정으로 입체적인 시각 효과
                tonalElevation = 8.dp,

                // 흰색 배경색
                color = colors.white,

                modifier = Modifier
                    // 가로 너비 화면 최대
                    .fillMaxWidth()

                    // 세로 길이 최대 360, 최소 310
                    .heightIn(max = 360.dp, min = 310.dp)
            ) {
                // 검색창 바탕 Box
                Box(
                    // 양 옆 여백 20dp
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    // 검색창 입력, 뒤로 가기
                    Row(
                        modifier = Modifier
                            // 위에서 46dp 떨어지게 위치
                            .padding(top = 46.dp),

                        // 21dp 간격 가로 배치
                        horizontalArrangement = Arrangement.spacedBy(16.dp),

                        // 세로 중앙 정렬
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 뒤로 가기
                        Icon(
                            modifier = Modifier
                                // 아이콘 크기
                                .height(40.dp)

                                // 클릭 시 닫힘
                                .noRippleClickable { onDismiss() },

                            // 아이콘 색상 Gray600
                            tint = colors.gray[600],

                            // compose 제공 왼쪽 화살표 이미지
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,

                            contentDescription = null
                        )
                        // 검색창 전체 바탕(틀)
                        Surface(
                            modifier = Modifier
                                .size(width = 341.dp, height = 42.dp),

                            // 배경색 (Gray100)
                            color = colors.gray[100],

                            // 모서리 둥글게 (18dp)
                            shape = RoundedCornerShape(18.dp),
                        ) {

                            // 검색창 요소 가로 정렬
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(13.dp)
                            ) {

                                // 왼쪽 링크 흑백 로고 아이콘
                                Icon(
                                    modifier = Modifier.padding(start = 18.51.dp),
                                    tint = colors.gray[500],
                                    painter = painterResource(id = R.drawable.ic_logo_white),
                                    contentDescription = "링큐 로고"
                                )
                                BasicTextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    modifier = Modifier
                                        .weight(1F),
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontFamily = paperlogyFont,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.black,
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        val q = text.trim()
                                        if (q.length >= 2) {
                                            onQueryChange(q)
                                            onQuerySave(q)
                                        }
                                        keyboardController?.hide()
                                    }),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            // 입력값이 없을 때만 placeholder 보임
                                            if (text.isEmpty()) {
                                                Text(
                                                    text = " 빠른 링크 검색",
                                                    fontSize = 15.sp,
                                                    lineHeight = 22.sp,
                                                    fontFamily = paperlogyFont,
                                                    fontWeight = FontWeight.Normal,
                                                    color = colors.black.copy(alpha = 0.4f) // placeholder는 살짝 연하게
                                                )
                                            }
                                            innerTextField() // 실제 입력창
                                        }
                                    }
                                )

                                Image(
                                    modifier = Modifier
                                        .padding(end = 18.dp)
                                        .size(18.dp)
                                        .noRippleClickable { text = "" },
                                    painter = painterResource(R.drawable.ic_text_clear),
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    // 최근 검색, 검색 결과, 모두 지우기, 수정, 완료 텍스트들
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 103.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (text.isEmpty()) "최근 검색" else "검색 결과",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = paperlogyFont,
                            fontWeight = FontWeight.Normal,
                            color = colors.black
                        )

                        if(text.isEmpty()){
                            when (isEditMode) {
                                true -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.noRippleClickable {
                                                onQueryClear()
                                            },
                                            text = "모두 지우기",
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            fontFamily = paperlogyFont,
                                            fontWeight = FontWeight.Normal,
                                            color = colors.gray[400]
                                        )
                                        Text(
                                            modifier = Modifier.noRippleClickable {
                                                isEditMode = false
                                            },
                                            text = "완료",
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            fontFamily = paperlogyFont,
                                            fontWeight = FontWeight.Normal,
                                            color = colors.black
                                        )
                                    }
                                }

                                false -> {
                                    Text(
                                        modifier = Modifier.noRippleClickable {
                                            isEditMode = true
                                        },
                                        text = "수정",
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = paperlogyFont,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.gray[400]
                                    )
                                }
                            }
                        }
                    }

                    // 입력 여부에 따라 최근 검색 기록 or 검색 결과 표시
                    when (text.isEmpty()) {
                        true -> {
                            LazyRow(
                                modifier = Modifier.padding(top = 129.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(recentQuerys) {
                                    RecentQueryItem(it)
                                }
                            }
                        }

                        false -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 129.dp)
                                    .padding(horizontal = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                items(fastSearchItems) {
                                    FastSearchItem(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun SearchBarTopSheetTest(){
    SearchBarTopSheet(
        visible = true,
        onDismiss = {},
        onQueryChange = {},
        onQuerySave = {},
        onQueryDelete = {},
        onQueryClear = {},
        onLinkClick = {}
    )
}