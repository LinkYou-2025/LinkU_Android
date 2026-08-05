package com.linku.design.top.search

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
import androidx.compose.ui.platform.LocalUriHandler
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
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic
import com.linku.design.theme.domain.domainLogoPainterOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

data class FastSearchItem(
    val id: Long,
    val title: String,
    val url: String,
)

/**
 * 상단에서 내려오는 형태의 빠른 링크 검색 탑 시트 컴포넌트입니다.
 * 사용자가 검색어를 입력하여 링크를 빠르게 찾거나, 최근 검색 기록을 관리할 수 있는 기능을 제공합니다.
 *
 * @param visible 탑 시트의 표시 여부. true일 때 상단에서 아래로 애니메이션과 함께 나타납니다.
 * @param onDismiss 탑 시트를 닫아야 할 때 호출되는 콜백 (배경 클릭, 뒤로가기 버튼 등).
 * @param onQueryChange 검색어가 변경될 때 호출되는 콜백. 2자 이상의 입력에 대해 데바운스(350ms) 처리 후 실행됩니다.
 * @param onQuerySave 현재 검색어를 최근 검색 기록에 저장하고자 할 때(예: 키보드 완료 버튼 클릭) 호출되는 콜백.
 * @param onQueryDelete 최근 검색 기록에서 특정 검색어를 삭제할 때 호출되는 콜백.
 * @param onQueryClear 최근 검색 기록의 모든 항목을 삭제할 때 호출되는 콜백.
 * @param onLinkClick 검색 결과 중 특정 링크 아이템을 클릭했을 때 호출되는 콜백. 선택된 아이템의 고유 ID를 전달합니다.
 * @param fastSearchItems 검색어에 따라 필터링되어 화면에 표시될 빠른 링크 검색 결과 리스트.
 * @param recentQueries 사용자에게 보여줄 최근 검색 기록 문자열 리스트.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Composable
fun SearchBarTopSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onQuerySave: (String) -> Unit,
    onQueryDelete: (String) -> Unit,
    onQueryClear: () -> Unit,
    onLinkClick: (Long) -> Unit,
    fastSearchItems: List<FastSearchItem> = emptyList(),
    recentQueries: List<String> = emptyList(),
) {
    // 테마 및 리소스
    val colors = LocalColorTheme.current
    val paperlogyFont = LocalFontTheme.current.font
    val keyboardController = LocalSoftwareKeyboardController.current

    // 상태 관리
    var text by remember { mutableStateOf("") }
    // 기본적으로 수정 모드는 꺼져있음
    var isEditMode by remember { mutableStateOf(false) }

    /**
     * 공통 초기화 + 닫기 처리
     * (뒤로가기 / 딤 클릭 / 닫기 버튼에서 중복 제거)
     */
    fun resetAndDismiss() {
        text = ""
        isEditMode = false
        keyboardController?.hide()
        onDismiss()
    }

    // 닫힐 때 상태 초기화 로직 (visible이 false가 될 때)
    LaunchedEffect(visible) {
        if (!visible) {
            resetAndDismiss()
        }
    }

/*
    LaunchedEffect(Unit) {
        snapshotFlow { text }
            .map { it.trim() }
            .filter { it.length >= 2 }    // 2자 이상일 때만
            .debounce(350)                // 350ms 주기 탐색
            .distinctUntilChanged()
            .mapLatest(onQueryChange)
            .collect { *//* no-op *//* }
    }*/

    /**
     * 검색어 변경 디바운스 처리
     *
     * - trim 적용
     * - 2자 이상만 검색
     * - 350ms 디바운스
     * - 동일 값 중복 호출 방지
     */
    LaunchedEffect(Unit) {
        snapshotFlow { text }
            .map { it.trim() }
            .filter { it.length >= 2 }
            .debounce(350)
            .distinctUntilChanged()
            .collectLatest(onQueryChange)
    }

    /**
     * 최근 검색어 목록의 개별 아이템을 표시하는 컴포저블입니다.
     *
     * 검색어를 칩(Chip) 형태로 표시하며, [isEditMode] 상태에 따라 두 가지 동작을 수행합니다:
     * 1. 일반 모드: 텍스트 클릭 시 해당 검색어를 입력창에 반영합니다.
     * 2. 수정 모드: 검색어 옆에 삭제 버튼을 표시하여 개별 기록을 삭제할 수 있게 합니다.
     *
     * @param query 표시할 최근 검색어 문자열입니다.
     */
    @Composable
    fun RecentQueryChip(query: String){

        // 가로 캡슐 모양의 아이템
        Row (
            modifier = Modifier

                // 검색어 길이에 따라 크기가 가변적
                .wrapContentSize()

                .background(
                    // 양옆을 둥글게
                    shape = RoundedCornerShape(size = 10.dp),

                    // Gray 100 배경색
                    color = colors.gray[100]
                )
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 태그 텍스트
            Text(
                modifier = Modifier
                    .noRippleClickable {
                        if (!isEditMode) {
                            text = query
                            keyboardController?.hide()
                        }
                    },
                text = query,
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
                            onQueryDelete(query)
                        },
                    painter = painterResource(id = R.drawable.ic_recent_search_x),
                    tint = colors.gray[500],
                    contentDescription = "수정 상태에서만 보이는 삭제 버튼",
                )
            }
        }
    }

    /**
     * 검색어와 일치하는 텍스트 부분을 굵게(Bold) 강조하여 표시하는 컴포저블 함수입니다.
     *
     * 전체 문자열([fullText]) 내에서 사용자가 입력한 검색어([searchTerm])를 찾아
     * 해당 부분에만 [FontWeight.Bold] 스타일을 적용한 [AnnotatedString]을 생성하여 출력합니다.
     * 정규식을 사용하여 대소문자 구분 없이 일치하는 모든 구간을 처리하며,
     * 텍스트가 길어질 경우 끝부분을 생략(...) 처리합니다.
     *
     * @param fullText 표시할 전체 원본 문자열입니다.
     * @param searchTerm 강조 스타일을 적용할 검색어 문자열입니다.
     */
    @Composable
    fun HighlightedText(
        fullText: String,
        searchTerm: String
    ) {

        // 검색어가 검색 결과에 강조된 문자열
        val highlightedResult = buildAnnotatedString {

            // 검색어가 없다면, 강조 없이 반환
            if (searchTerm.isEmpty()) {
                append(fullText)
                return@buildAnnotatedString
            }

            // 검색어를 모두 찾아내는 정규식
            val regex = Regex("(?i)${Regex.escape(searchTerm)}")
            var lastIndex = 0

            // 찾아낸 검색어들에 하이라이트 처리
            regex.findAll(fullText).forEach { matchResult ->
                val matchStart = matchResult.range.first
                val matchEnd = matchResult.range.last + 1

                // 검색어 앞부분 그대로 추가
                append(fullText.substring(lastIndex, matchStart))

                // 검색어 부분 Bold 처리
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(fullText.substring(matchStart, matchEnd))
                }

                lastIndex = matchEnd
            }

            // 마지막 남은 부분 추가
            if (lastIndex < fullText.length) {
                append(fullText.substring(lastIndex))
            }
        }

        // 강조된 텍스트
        Text(
            text = highlightedResult,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = paperlogyFont,
            fontWeight = FontWeight.Normal,
            color = colors.black,
            maxLines = 1, // 최대 1줄
            overflow = TextOverflow.Ellipsis // 잘리면 ... 표시
        )
    }

    /**
     * 빠른 링크 검색 결과를 표시하는 개별 아이템 컴포저블입니다.
     *
     * 해당 아이템은 링크의 도메인 로고 이미지와 함께 제목을 표시하며,
     * 현재 검색어와 일치하는 제목의 텍스트 부분을 강조(Bold)하여 보여줍니다.
     *
     * @param fastSearchItem 표시할 검색 결과 아이템 데이터 ([FastSearchItem])
     */
    @Composable
    fun FastSearchItemRow(fastSearchItem: FastSearchItem){

        // 도메인 로고
        val logoPainter = domainLogoPainterOrNull(fastSearchItem.url)

        // uri 핸들러
        val uriHandler = LocalUriHandler.current

        // 링크 내용
        Row(
            modifier = Modifier
                .noRippleClickable{
                    runCatching { onLinkClick(fastSearchItem.id) }
                        .onFailure { /* 링크 클릭 에러 시 처리 */ }
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
                painter = logoPainter?:painterResource(R.drawable.logo_whiteback),
                contentDescription = null
            )

            // 링크 제목
            HighlightedText(
                fullText = fastSearchItem.title,
                searchTerm = text
            )
        }
    }

    /* ===================== UI ===================== */

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

                    // 뒷 배경 클릭 시 닫힘 및 검색어, 수정 모드 초기화
                    .noRippleClickable {
                        text = ""
                        isEditMode = false
                        onDismiss()
                    }
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

                        // 20dp 간격 가로 배치
                        horizontalArrangement = Arrangement.spacedBy(20.dp),

                        // 세로 중앙 정렬
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 뒤로 가기
                        Icon(
                            modifier = Modifier
                                // 아이콘 크기
                                .height(40.dp)

                                // 뒤로 가기 클릭 시 닫힘 및 검색어, 수정 모드 초기화
                                .noRippleClickable {
                                    text = ""
                                    isEditMode = false
                                    onDismiss()
                                },

                            // 아이콘 색상 Gray600
                            tint = colors.gray[600],

                            // compose 제공 왼쪽 화살표 이미지
                            painter = painterResource(id = R.drawable.ic_back),

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

                                // 검색어 입력 부분
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
                                                    text = "빠른 링크 검색",
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

                                // 검색어 삭제 버튼
                                if(text.isNotEmpty()){
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

                        // 최근 검색 or 검색 결과
                        Text(
                            text = if (text.isEmpty()) "최근 검색" else "검색 결과",
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = paperlogyFont,
                            fontWeight = FontWeight.Medium,
                            color = colors.gray[700]
                        )

                        // 입력된 검색어가 없을 때,
                        if(text.isEmpty()){

                            // 수정 상태이면,
                            if (isEditMode) {

                                // 모두 지우기 버튼, 완료 버튼
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // 모두 지우기 버튼
                                    Text(
                                        modifier = Modifier.noRippleClickable (onClick = onQueryClear),
                                        text = "모두 지우기",
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontFamily = paperlogyFont,
                                        fontWeight = FontWeight.Normal,
                                        color = colors.gray[400]
                                    )

                                    // 완료 버튼
                                    Text(

                                        // 클릭 시, 일반 상태로 변경
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
                            } else {    // 일반 상태일 때,

                                // 수정 버튼
                                Text(

                                    // 클릭 시, 수정 상태로 변경
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

                    // 입력 여부에 따라 최근 검색 기록 or 검색 결과 표시
                    if (text.isBlank()) {

                        // 최근 검색 기록
                        LazyRow(
                            modifier = Modifier.padding(top = 129.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(recentQueries) {
                                RecentQueryChip(it)
                            }
                        }
                    } else {

                        // 검색 결과가 있으면 검색 결과 표시,
                        // 검색 결과가 없으면 "찾으시는 검색어 결과가 없어요!" 표시
                        if(fastSearchItems.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 129.dp)
                                    .padding(horizontal = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                items(fastSearchItems) {
                                    FastSearchItemRow(it)
                                }
                            }
                        } else {

                            // "찾으시는 검색어 결과가 없어요!" 공간
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 22.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                // "찾으시는 검색어 결과가 없어요!" 아이콘
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(R.drawable.ic_search_bar_caution),
                                    contentDescription = "\"찾으시는 검색어 결과가 없어요!\" 아이콘",
                                    tint = Basic.negative
                                )

                                // "찾으시는 검색어 결과가 없어요!" 텍스트
                                Text(
                                    text = "찾으시는 검색어 결과가 없어요!",
                                    fontSize = 13.sp,
                                    color = Basic.negative
                                )
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
        onLinkClick = {},
        recentQueries = listOf("최근 검색 1", "최근 검색 2", "최근 검색 3")
    )
}