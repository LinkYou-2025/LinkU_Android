package com.linku.file.ui.link

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.linku.core.model.AiArticle
import com.linku.core.model.LinkResultInfo
import com.linku.design.BrushText
import com.linku.design.R
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import java.time.OffsetDateTime

private fun emotionDisplayName(id: Long?): String? = when (id) {
    1L -> "즐거움"
    2L -> "평온"
    3L -> "설렘"
    4L -> "슬픔"
    5L -> "짜증"
    6L -> "분노"
    else -> null
}

private val CATEGORY_NAMES: Map<Long, String> = mapOf(
    1L to "어학",
    2L to "뉴스",
    3L to "공부법",
    4L to "IT·개발",
    5L to "자기계발",
    6L to "취업·이직",
    7L to "비즈니스 인사이트",
    8L to "생산성·툴",
    9L to "라이프스타일",
    10L to "심리·자기이해",
    11L to "에세이·칼럼",
    12L to "트렌드",
    13L to "디자인·예술",
    14L to "영상·뮤직",
    15L to "맛집·여행",
    16L to "기타"
)

private fun categoryDisplayName(id: Long?): String? = id?.let { CATEGORY_NAMES[it] }

// label -> id
private fun categoryIdOf(label: String): Long? =
    CATEGORY_NAMES.entries.firstOrNull { it.value == label }?.key

private fun emotionIdOf(label: String): Long? = when (label) {
    "즐거움" -> 1L; "평온" -> 2L; "설렘" -> 3L
    "슬픔" -> 4L; "짜증" -> 5L; "분노" -> 6L
    else -> null
}

// 변경: 벡터 리소스 사용
private data class EmotionOpt(
    val id: Long,
    val label: String,
    @DrawableRes val iconRes: Int
)

private val EMOTION_OPTIONS = listOf(
    EmotionOpt(1, "즐거움", R.drawable.ic_joy),
    EmotionOpt(2, "평온", R.drawable.ic_calm),
    EmotionOpt(3, "설렘", R.drawable.ic_excite),
    EmotionOpt(4, "슬픔", R.drawable.ic_sad),
    EmotionOpt(5, "짜증", R.drawable.ic_irritation),
    EmotionOpt(6, "분노", R.drawable.ic_anger),
)

@Composable
fun SaveLinkResultScreen(
    selectedImageUri: String? = null, // 외부에서 전달받은 URI
    link: LinkResultInfo?,
    aiArticle: AiArticle? = null,
    isLoading: Boolean = false,
    isAiLoading: Boolean = false,
    onBack: () -> Unit = {},
    onOpenLink: (String) -> Unit,
    categoryColorMap: Map<String, CategoryColorStyle> = emptyMap(),
    onSubmitEdit: (title: String, memo: String?, categoryId: Long?, emotionId: Long?) -> Unit = { _,_,_,_ -> },
    onRequestAiSummary: () -> Unit,
    aiProgress: Float = 0f,
    onCancelAi: () -> Unit = {}
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    // 서버 데이터 바인딩 (널/로딩 방어)
    val titleFromServer = link?.title.orEmpty()
    val memoFromServer = link?.memo.orEmpty()
    val imageUrl = link?.linkuImageUrl
    val linku = link?.linku.orEmpty()

    // 항상 최신 콜백을 보관
    val currentOnRequestAi = rememberUpdatedState(onRequestAiSummary)
    val currentOnOpenLink = rememberUpdatedState(onOpenLink)

    // 키워드/요약 실제 표시값 결정 (우선 aiArticle, 없으면 link 값)
    val aiExists = link?.aiArticleExists == true

    val displayKeyword = when {
//        !aiArticle?.keyword.isNullOrBlank() -> aiArticle!!.keyword!!.trim()
        aiExists -> link?.keyword.orEmpty()
        else -> ""
    }

    val displaySummary = when {
        !aiArticle?.summary.isNullOrBlank() -> aiArticle!!.summary!!.trim()
        aiExists -> link?.summary.orEmpty()
        else -> ""
    }

    val isKeywordEmpty = displayKeyword.isBlank()
    val isSummaryEmpty = displaySummary.isBlank()

    val keywordPlaceholder = "AI가 키워드를 추출하지 못하였습니다."
    val summaryPlaceholder = "AI가 본문을 요약하지 못하였습니다."

    // 요약이 "없을 때" 요청 가능하도록
    val hasContent = (aiArticle != null) || aiExists
    val canRequestAi = !isAiLoading  // 콘텐츠가 없고, 로딩 중이 아닐 때만 생성 가능

    var showAISummary by remember { mutableStateOf(false) }
    var showAIArticleModal by remember { mutableStateOf(false) }

    // 요약이 이미 있거나(aiArticle or link.summary/keyword) 로딩 완료되면 자동 펼치기
    // (자동 펼침은 “이미 존재하는 요약/키워드가 있을 때만” 유지. 새 호출은 하지 않음)
    LaunchedEffect(aiArticle, link?.aiArticleExists, isLoading) {
        if (!isLoading) {
            val hasExisting = (aiArticle != null) || (link?.aiArticleExists == true)
            if (hasExisting) showAISummary = true
        }
    }

    // isAiLoading 값이 바뀔 때마다 로그 찍기
    LaunchedEffect(isAiLoading) {
        Log.d("SaveLinkResultScreen", "isAiLoading changed -> $isAiLoading")
    }

    // 서버에서 내려온 최신 카테고리 이름(순서 보장)을 사용
    val categoryLabels = remember(categoryColorMap) { categoryColorMap.keys.toList() }

    // 선택 라벨/ID 계산은 동적 라벨 리스트 기반
    // 선택된 카테고리/감정 ID (초기값은 서버 응답)
    var selectedEmotionId  by remember(link?.emotionId)  { mutableStateOf(link?.emotionId) }
    var selectedCategoryId by remember(link?.categoryId) { mutableStateOf(link?.categoryId) }

    var isEditMode by remember { mutableStateOf(false) }

    // 메모/제목은 서버값으로 초기화
    var memoText by remember { mutableStateOf(memoFromServer) }
    var isMemoEditing by remember { mutableStateOf(false) }


    // 매 렌더마다 계산
    val effectiveCategoryId = selectedCategoryId ?: link?.categoryId
    val effectiveEmotionId  = selectedEmotionId  ?: link?.emotionId

    val categoryLabel = categoryDisplayName(effectiveCategoryId) ?: "카테고리"
    val emotionLabel  = emotionDisplayName(effectiveEmotionId)  ?: "감정"

    LaunchedEffect(link) {
        // 상세가 갱신되면 UI 상태도 동기화(사용자가 수정 중이 아닐 때만)
        if (!isMemoEditing) memoText = memoFromServer
    }

    // 수정 완료 시 텍스트 입력 종료
    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            isMemoEditing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val readModeTags = listOfNotNull(
                categoryDisplayName(effectiveCategoryId),
                emotionDisplayName(effectiveEmotionId)
            )

            TopBar(
                isEditMode = isEditMode,
                showAISummary = showAISummary,
                onEditClick = {latestTitle ->
                    if (isEditMode) {
                        // 편집 종료 시 저장 호출
                        onSubmitEdit(
                            latestTitle,
                            memoText.ifBlank { null },
                            selectedCategoryId,
                            selectedEmotionId
                        )
                    }
                    isEditMode = !isEditMode
                },
                onBack = onBack,
                titleText = titleFromServer,
                tags = readModeTags,
                initialCategoryLabel = categoryLabel,
                initialEmotionLabel = emotionLabel,
                categoryLabels = categoryLabels,
                dotColorOf = { label ->
                    categoryColorMap[label]?.color4 ?: colors.gray[300]
                },
                onCategorySelected = { label ->
                    selectedCategoryId = categoryIdOf(label)
                },
                onEmotionSelected = { label ->
                    selectedEmotionId = emotionIdOf(label)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "링크",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = font),
                    color = colors.gray[800],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 51.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp))
                        .padding(horizontal = 22.dp, vertical = 15.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = linku,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = font, lineHeight = 20.sp),
                        color = colors.black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(209.3.dp)
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.gray[100])
                    .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "선택된 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // 박스에 꽉 차도록
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_transparent_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .height(120.dp)
                                .padding(top = 50.dp)
                        )
                        Text(
                            text = "이미지 업로드하기",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = font),
                            color = colors.gray[500],
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            if (showAISummary) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 29.7.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "키워드 (AI추출 태그)",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = font),
                            color = colors.gray[800]
                        )

                        if (isEditMode) {
                            Text(
                                text = "수정 불가",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                                color = colors.blue[200],
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 51.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.gray[100])
                            .padding(horizontal = 22.dp, vertical = 15.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        when {
                            isAiLoading -> {
                                Text(
                                    "키워드 추출 중...",
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                                    color = colors.gray[400]
                                )
                            }
                            isKeywordEmpty -> {
                                Text(
                                    text = keywordPlaceholder,
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                                    color = colors.gray[400]
                                )
                            }
                            else -> {
//                                Canvas(modifier = Modifier.fillMaxSize()) {
//                                    drawIntoCanvas {
//                                        val text = displayKeyword
//                                        val textSizePx = 14.sp.toPx()
//
//                                        val paintForWidth = android.graphics.Paint().apply {
//                                            textSize = textSizePx
//                                        }
//                                        val textWidth = paintForWidth.measureText(text)
//
//                                        val gradient = android.graphics.LinearGradient(
//                                            0f, 0f, textWidth, 0f,  // 텍스트 길이에 맞춰 그라데이션
//                                            intArrayOf(
//                                                0xFF2C6FFF.toInt(),
//                                                0xFFCB59EB.toInt()
//                                            ),
//                                            null,
//                                            android.graphics.Shader.TileMode.CLAMP
//                                        )
//
//                                        val paint = android.graphics.Paint().apply {
//                                            isAntiAlias = true
//                                            textSize = textSizePx
//                                            shader = gradient
//                                        }
//
//                                        it.nativeCanvas.drawText(
//                                            text,
//                                            0f,
//                                            size.height / 2 + textSizePx / 2.5f,
//                                            paint
//                                        )
//                                    }
//                                }
                                BrushText(
                                    text = displayKeyword,
                                    brush = colors.maincolor, // 이미 쓰는 메인 그라데이션
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = font // Paperlogy 적용
                                    ),
                                    modifier = Modifier.fillMaxWidth() // 필요시 정렬/패딩 추가
                                )

                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(top = 30.dp, start = 20.dp, end = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        Canvas(
//                            modifier = Modifier
//                                .height(11.dp)
//                                .padding(start = 4.dp)
//                        ) {
//                            drawIntoCanvas {
//                                val text = "AI 본문 요약"
//                                val textSizePx = 14.sp.toPx()
//
//                                val paintForWidth = android.graphics.Paint().apply {
//                                    textSize = textSizePx
//                                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
//                                }
//                                val textWidth = paintForWidth.measureText(text)
//
//                                val gradient = android.graphics.LinearGradient(
//                                    0f, 0f, textWidth, 0f,
//                                    intArrayOf(
//                                        0xFF2C6FFF.toInt(),
//                                        0xFFCB59EB.toInt()
//                                    ),
//                                    null,
//                                    android.graphics.Shader.TileMode.CLAMP
//                                )
//
//                                val paint = android.graphics.Paint().apply {
//                                    isAntiAlias = true
//                                    textSize = textSizePx
//                                    shader = gradient
//                                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
//                                }
//
//                                it.nativeCanvas.drawText(
//                                    text,
//                                    0f,
//                                    size.height / 2 + textSizePx / 2.5f,
//                                    paint
//                                )
//                            }
//                        }
                        BrushText(
                            text = "AI 본문 요약",
                            brush = colors.maincolor,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = font // Paperlogy 적용
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )


                        if (isEditMode) {
                            Text(
                                text = "수정 불가",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                                color = colors.blue[200],
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 51.dp)
                            .padding(top = 15.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSummaryEmpty) {
                                    SolidColor(colors.gray[100])
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(
                                            colors.blue[200].copy(alpha = 0.1f),
                                            colors.purple[200].copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            )
                            .padding(horizontal = 22.dp, vertical = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = when {
                                isAiLoading -> "요약 생성 중..."
                                isSummaryEmpty -> summaryPlaceholder
                                else -> displaySummary
                            },
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, fontFamily = font, lineHeight = 20.sp),
                            color = when {
                                isAiLoading || isSummaryEmpty -> colors.gray[400]
                                else -> colors.black
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 29.7.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "메모",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = font),
                    color = colors.gray[800],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 포커스 요청자 준비
                val memoFocusRequester = remember { FocusRequester() }

                // 편집 진입하면 자동 포커스 (다음 프레임에 요청)
                LaunchedEffect(isMemoEditing) {
                    if (isMemoEditing) {
                        kotlinx.coroutines.yield()
                        memoFocusRequester.requestFocus()
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 51.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.gray[100])
                        .padding(horizontal = 22.dp, vertical = 15.dp)
                        .then(
                            if (isEditMode) Modifier.clickable { isMemoEditing = true } else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isMemoEditing) {
                        BasicTextField(
                            value = memoText,
                            onValueChange = { memoText = it },
                            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light, color = colors.black, fontFamily = font, lineHeight = 20.sp),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(memoFocusRequester)
                        )
                    } else {
                        Text(
                            text = memoText,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = font, lineHeight = 20.sp),
                            color = colors.black,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isEditMode) {
                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            painter = painterResource(R.drawable.ic_delete_gray),
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    memoText = ""
                                    isMemoEditing = true
                                }
                        )
                    }
                }
            }

            if (showAISummary) {
                Spacer(modifier = Modifier.height(80.dp))
            }

            if (!showAISummary) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.blue[200])
                        .clickable {
//                            if (hasContent) {
//                                // 이미 요약/키워드가 있으면 네트워크 호출 없이 즉시 펼치기
//                                showAISummary = true
//                            } else {
//                                // 없으면 API 요청 → isAiLoading=true 동안 모달 표시
//                                onRequestAiSummary()
//                            }
                            Log.d("SaveLinkResultScreen", "AI 버튼 클릭! hasContent=$hasContent, isAiLoading=$isAiLoading")
                            if (isAiLoading) return@clickable
                            if (hasContent) {
                                // 이미 키워드/요약 있으면 네트워크 없이 즉시 펼치기
                                showAISummary = true
                            } else {
                                // 없으면 API 요청 → ViewModel이 isLoadingAiArticle=true로 만들고
                                // 화면은 로딩 오버레이를 띄워줌
                                android.util.Log.d("SaveLinkResultScreen", "AI 요청 트리거")
                                currentOnRequestAi.value()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI 요약 보기",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                        color = colors.white
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI가 링크 내용을 바탕으로 요약해드려요! 이용해보시겠어요?",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                        color = colors.gray[400]
                    )
                }

                Spacer(modifier = Modifier.height(41.dp))
            }
        }

        // 모달은 로딩 동안 항상 표시
        if (isAiLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .zIndex(1f)
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AIArticleModal(
                        progress = aiProgress,
                        onCancel = onCancelAi,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .zIndex(2f)
                .padding(bottom = 14.dp, end = 19.71.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = colors.maincolor)
                    .padding(horizontal = 16.dp)
                    .clickable {
//                        Log.d("SaveLinkResultScreen", "링크 버튼 클릭! linku='$linku'")
//                        val target = linku
//                        if (target.isNotBlank()) {
//                            onOpenLink(target)
//                        } else {
//                            Log.w("SaveLinkResultScreen", "linku 비어있음(상세 아직 로드 전) -> 무시")
//                        }
                        Log.d("SaveLinkResultScreen", "링크 버튼 클릭! linku='$linku'")
                        // 최신 콜백 호출
                        currentOnOpenLink.value(linku)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "링크 바로 가기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                    color = colors.white
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(R.drawable.ic_link_go),
                    contentDescription = null,
                    modifier = Modifier.height(14.dp)
                )
            }
        }

//        if (showAIArticleModal) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color(0x66000000)) // 40% 투명한 검정색 배경
//                    .zIndex(1f)
//                    .clickable(enabled = false) {}, // 외부 클릭 막기
//                contentAlignment = Alignment.Center
//            ) {
//                Box(
//                    modifier = Modifier.padding(horizontal = 20.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    AIArticleModal(
//                        modifier = Modifier
////                    .align(Alignment.Center)
//                            .padding(horizontal = 20.dp)
//                    )
//                }
//            }
//        }
    }
}

@Composable
private fun TopBar(
    isEditMode: Boolean = false,
    showAISummary: Boolean = false,
    onEditClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    titleText: String = "",
    tags: List<String> = emptyList(),
    initialCategoryLabel: String = "카테고리",
    initialEmotionLabel: String = "감정",
    onCategorySelected: (String) -> Unit = {},
    onEmotionSelected: (String) -> Unit = {},
    categoryLabels: List<String> = emptyList(),
    dotColorOf: @Composable (String) -> Color = { MaterialTheme.linkuColors.gray[300] },
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    // 태그 샘플
//    val tags = listOf("카테고리", "감정")

    var title by remember { mutableStateOf(titleText) }
    var isTitleEditing by remember { mutableStateOf(false) }

    LaunchedEffect(titleText) { if (!isTitleEditing) title = titleText }
    LaunchedEffect(isEditMode) { if (!isEditMode) isTitleEditing = false }

    LaunchedEffect(titleText) {
        if (!isTitleEditing) title = titleText
    }

    // 수정 완료 시 텍스트 입력 종료
    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            isTitleEditing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
            )
            .background(colors.blue[200])
    ) {
        Image(
            painter = painterResource(R.drawable.ic_transparent_logo_background),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 9.dp)
        )

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 59.dp, start = 20.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                    Image(
                        painter = painterResource(R.drawable.ic_back_white),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 10.dp, height = 16.25.dp)
                            .clickable { onBack() }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "저장된 링크",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = font),
                    color = colors.white
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = if (isEditMode) "완료" else "수정",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, fontFamily = font),
                        color = colors.blue[50],
                        modifier = Modifier
                            .clickable {
                                onEditClick(title)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.75.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showAISummary) {
                    Image(
                        painter = painterResource(R.drawable.ic_ai_summarize_save),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isEditMode) {
                                Modifier
                                    .clip(RoundedCornerShape(13.dp))
                                    .border(1.dp, colors.blue[100], RoundedCornerShape(13.dp))
                                    .padding(top = 4.dp, start = 15.dp, end = 15.dp, bottom = 4.dp)
                                    .clickable { isTitleEditing = true }
                            } else {
                                Modifier
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTitleEditing) {
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.white, fontFamily = font),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = title,
//                            overflow = TextOverflow.Ellipsis,  // 말 줄임
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                            color = colors.white,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isEditMode) {
                        Spacer(modifier = Modifier.width(10.dp))

                        Image(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    title = ""
                                    isTitleEditing = true
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            // tags
//            if (tags.isNotEmpty()) {
//                Row(
//                    modifier = Modifier
//                        .padding(start = 24.dp)
//                ) {
//                    tags.forEach { tag ->
//                        Row(
//                            modifier = Modifier
//                                .background(
//                                    colors.blue[50],
//                                    RoundedCornerShape(10.dp)
//                                )
//                                .padding(horizontal = 10.dp, vertical = 8.5.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = tag,
//                                style = TextStyle(
//                                    fontSize = 12.sp,
//                                    fontWeight = FontWeight.Normal,
//                                    color = colors.blue[300]
//                                )
//                            )
//
//                            if (isEditMode) {
//                                Spacer(modifier = Modifier.width(6.dp))
//
//                                Image(
//                                    painter = painterResource(R.drawable.ic_toggle),
//                                    contentDescription = null,
//                                    modifier = Modifier.height(6.dp)
//                                )
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.width(6.dp))
//                    }
//                }
//            }
            // ▼ 편집 모드면: 드롭다운 2개 노출
            if (isEditMode) {
                Row(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChipEditable(
                        label = initialCategoryLabel,
                        labels = categoryLabels,
                        dotColorOf = dotColorOf,
                        onSelected = onCategorySelected
                    )

                    Spacer(Modifier.width(8.dp))

                    EmotionChipEditable(
                        label = initialEmotionLabel,
                        onSelected = onEmotionSelected
                    )
                }
                Spacer(Modifier.height(12.dp))
            } else if (tags.isNotEmpty()) {
                // 읽기 모드: 기존 태그 보여주기
                Row(modifier = Modifier.padding(start = 24.dp)) {
                    tags.forEach { tag ->
                        Row(
                            modifier = Modifier
                                .background(colors.blue[50], RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tag,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = colors.blue[300],
                                    fontFamily = font
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategoryChipEditable(
    label: String,
    labels: List<String>,
    dotColorOf: @Composable (String) -> Color,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .heightIn(min = 26.dp)
                .background(colors.blue[50], RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = colors.blue[300],
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = font
            )
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.ic_toggle),
                contentDescription = null,
                modifier = Modifier.height(6.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = colors.white,
            offset = DpOffset(0.dp, 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .width(205.dp)
                    .heightIn(max = 264.dp)
                    .padding(vertical = 12.5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                labels.forEach { name ->
                    val selected = (name == label)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(name)
                                expanded = false
                            }
                            .padding(horizontal = 18.dp, vertical = 7.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dot = dotColorOf(name)
                        Canvas(Modifier.size(25.dp)) { drawCircle(dot) }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = name,
                            color = if (selected) colors.blue[300] else colors.gray[800],
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            fontSize = 14.sp,
                            fontFamily = font
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionChipEditable(
    label: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopCenter)) {
        Row(
            modifier = Modifier
                .heightIn(min = 26.dp)
                .background(colors.blue[50], RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = colors.blue[300],
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = font
            )
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.ic_toggle),
                contentDescription = null,
                modifier = Modifier.height(6.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = colors.white,
            offset = DpOffset(0.dp, 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .width(151.dp)
                    .heightIn(max = 264.dp)
                    .padding(top = 12.dp, start = 16.dp, end = 56.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EMOTION_OPTIONS.forEach { emo ->
                    val selected = emo.label == label
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(emo.label)
                                expanded = false
                            }
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 이모지 원형 배지
                        Box(
                            modifier = Modifier
                                .size(29.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.gray[100]),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(emo.iconRes),
                                contentDescription = emo.label,
                                modifier = Modifier.size(29.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = emo.label,
                            color = if (selected) colors.blue[300]
                            else colors.gray[800],
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            fontSize = 14.sp,
                            fontFamily = font
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSaveLinkResultScreen() {
    SaveLinkResultScreen(
        link = LinkResultInfo(
            userId = 1L,
            userLinkuId = 1L,
            linkuFolderId = 2L,
            categoryId = 16L,
            linku = "https://blog.naver.com/s2ethan/223941554164",
            memo = "프리뷰 메모",
            emotionId = 3L,
            situationId = 9L,
            isEmotionAi = false,
            isSituationAi = false,
            domain = "blog.naver",
            title = "프리뷰 제목",
            domainImageUrl = null,
            linkuImageUrl = null,
            aiArticleExists = true,
            keyword = null,
            summary = null,
            createdAt = OffsetDateTime.parse("2025-07-21T23:13:41.354053+09:00"),
            updatedAt = OffsetDateTime.parse("2025-07-21T23:13:41.354053+09:00")
        ),
        isLoading = false,
        onOpenLink = { /* no-op for preview */ },
        onRequestAiSummary = { /* no-op for preview */ }
    )
}
