package com.linku.home.screen

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.linku.core.model.EmotionType
import com.linku.core.model.SituationOptions
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.home.R
import com.linku.home.component.AIArticleModal
import com.linku.home.component.DeleteLinkModal
import com.linku.home.component.LinkCategoryOption
import com.linku.home.component.LinkDetailCategoryDropdown
import com.linku.home.component.LinkDetailCustomDropdown
import com.linku.home.component.LinkDetailEmotionDropdown
import com.linku.home.component.LinkDetailOptionDropdown
import com.linku.home.ui.home.bar.LinkDetailTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class LinkDetailDropdownType {
    CATEGORY,
    EMOTION,
    SITUATION
}

@Composable
fun LinkDetailScreen(
    linkTitle: String,
    category: String,
    emotion: String,
    situation: String,
    linkUrl: String,
    memo: String,
    tags: List<String>,
    aiSummary: String,
    onBack: () -> Unit,
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }
    var isAiSummaryMode by remember { mutableStateOf(false) }

    var isDropdownVisible by remember { mutableStateOf(false) }
    var isDeleteModalVisible by remember { mutableStateOf(false) }
    var isAiArticleModalVisible by remember { mutableStateOf(false) }
    var isAiArticleProcessing by remember { mutableStateOf(false) }
    var aiArticleProgress by remember { mutableFloatStateOf(0f) }

    var selectedTitle by remember { mutableStateOf(linkTitle) }
    var selectedCategory by remember { mutableStateOf(category) }
    var selectedEmotion by remember { mutableStateOf(emotion) }
    var selectedSituation by remember { mutableStateOf(situation) }
    var selectedMemo by remember { mutableStateOf(memo) }

    var openedDropdownType by remember {
        mutableStateOf<LinkDetailDropdownType?>(null)
    }

    val emotionOptions = EmotionType.entries.toList()

    val situationOptions = SituationOptions.linkDetailSituations

    val visibleTags = tags
        .filter { it.isNotBlank() }
        .take(4)
        .map { tag ->
            if (tag.startsWith("#")) tag else "#$tag"
        }

    // 카테고리 더미데이터
    val categoryOptions = listOf(
        LinkCategoryOption(1L, "카테고리2", Color(0xFF55D6C2)),
        LinkCategoryOption(2L, "카테고리3", Color(0xFFFFBE3D)),
        LinkCategoryOption(3L, "카테고리4", Color(0xFF2FB4E9)),
        LinkCategoryOption(4L, "카테고리5", Color(0xFFFF5757)),
        LinkCategoryOption(5L, "카테고리6", Color(0xFF67D414)),
        LinkCategoryOption(6L, "카테고리7", Color(0xFFD9DEE6))
    )

    LaunchedEffect(isAiArticleProcessing) {
        if (isAiArticleProcessing) {
            aiArticleProgress = 0f

            while (aiArticleProgress < 1f) {
                delay(80)
                aiArticleProgress = (aiArticleProgress + 0.02f).coerceAtMost(1f)
            }

            delay(300)

            isAiArticleProcessing = false
            isAiArticleModalVisible = false
            isAiSummaryMode = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LinkDetailTopBar(
                linkTitle = selectedTitle,
                category = selectedCategory,
                emotion = selectedEmotion,
                situation = selectedSituation,
                isEditMode = isEditMode,
                isCategoryDropdownOpen = openedDropdownType == LinkDetailDropdownType.CATEGORY,
                isEmotionDropdownOpen = openedDropdownType == LinkDetailDropdownType.EMOTION,
                isSituationDropdownOpen = openedDropdownType == LinkDetailDropdownType.SITUATION,
                onBack = { onBack() },
                onMoreClick = {
                    isDropdownVisible = !isDropdownVisible
                },
                onLinkGoClick = { uriHandler.openUri(linkUrl) },
                onCategoryClick = {
                    openedDropdownType =
                        if (openedDropdownType == LinkDetailDropdownType.CATEGORY) null
                        else LinkDetailDropdownType.CATEGORY
                },
                onEmotionClick = {
                    openedDropdownType =
                        if (openedDropdownType == LinkDetailDropdownType.EMOTION) null
                        else LinkDetailDropdownType.EMOTION
                },
                onSituationClick = {
                    openedDropdownType =
                        if (openedDropdownType == LinkDetailDropdownType.SITUATION) null
                        else LinkDetailDropdownType.SITUATION
                },
                onTitleClearClick = {
                    selectedTitle = ""
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 25.dp, start = 20.dp, end = 20.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.img_default),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .alpha(if (isEditMode) 0.6f else 1f)
                            .border(
                                width = 1.dp,
                                color = LocalColorTheme.current.gray[200],
                                shape = RoundedCornerShape(18.dp)
                            )
                    )

                    if(isEditMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .noRippleClickable {},
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(LocalColorTheme.current.gray[700])
                                    .alpha(0.6f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_camera_white),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(24.dp)
                                        .padding(top = 5.dp)
                                )

                                Spacer(modifier = Modifier.height(7.dp))

                                Text(
                                    text = "사진 변경",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = LocalColorTheme.current.white
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.dp,
                            color = LocalColorTheme.current.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(LocalColorTheme.current.white)
                        .padding(top = 7.5.dp, start = 22.dp, end = 8.5.dp, bottom = 7.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = linkUrl,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        color = if (isEditMode) LocalColorTheme.current.gray[400] else LocalColorTheme.current.black,
                        modifier = Modifier
                            .then(
                                if (isEditMode) {
                                    Modifier.padding(vertical = 7.5.dp)
                                } else {
                                    Modifier.padding(0.dp)
                                }
                            )
                    )

                    if (!isEditMode) {
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "복사",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = LocalColorTheme.current.gray[600],
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(LocalColorTheme.current.gray[200])
                                .padding(horizontal = 13.5.dp, vertical = 7.dp)
                                .noRippleClickable {
                                    coroutineScope.launch {
                                        clipboard.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText("linkUrl", linkUrl)
                                            )
                                        )
                                    }
                                }
                        )
                    }
                }

                if (isAiSummaryMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 25.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_sparkles_colored),
                                contentDescription = null,
                                modifier = Modifier.height(15.dp)
                            )

                            Text(
                                text = "AI 태그",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.black
                            )
                        }

                        if (visibleTags.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                visibleTags.forEach { tag ->
                                    Text(
                                        text = tag,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = LocalColorTheme.current.black,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .border(1.dp, LocalColorTheme.current.inactiveColor, RoundedCornerShape(20.dp))
                                            .background(LocalColorTheme.current.white)
                                            .padding(horizontal = 15.dp, vertical = 9.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_sparkles_colored),
                                contentDescription = null,
                                modifier = Modifier.height(15.dp)
                            )

                            Text(
                                text = "AI 링크 요약",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LocalColorTheme.current.black
                            )
                        }

                        Text(
                            text = aiSummary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalColorTheme.current.black,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, LocalColorTheme.current.inactiveColor, RoundedCornerShape(18.dp))
                                .background(LocalColorTheme.current.white)
                                .padding(horizontal = 22.dp, vertical = 16.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    Text(
                        text = "메모",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = LocalColorTheme.current.black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditMode) {
                        BasicTextField(
                            value = selectedMemo,
                            onValueChange = {
                                selectedMemo = it
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 20.sp,
                                color = LocalColorTheme.current.black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(LocalColorTheme.current.gray[100])
                                .padding(horizontal = 22.dp, vertical = 15.5.dp),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (selectedMemo.isBlank()) {
                                        Text(
                                            text = "메모를 입력해 주세요.",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 20.sp,
                                            color = LocalColorTheme.current.gray[400]
                                        )
                                    }

                                    innerTextField()
                                }
                            }
                        )
                    } else {
                        Text(
                            text = selectedMemo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 20.sp,
                            color = LocalColorTheme.current.black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(LocalColorTheme.current.gray[100])
                                .padding(horizontal = 22.dp, vertical = 15.5.dp)
                        )
                    }

                    if (isAiSummaryMode) {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        if (isDropdownVisible) {
            LinkDetailCustomDropdown(
                onEditClick = {
                    isDropdownVisible = false
                    isEditMode = true
                },
                onDeleteClick = {
                    isDropdownVisible = false
                    openedDropdownType = null
                    isDeleteModalVisible = true
                },
                onShareClick = {
                    isDropdownVisible = false
                    openedDropdownType = null

                    val shareText = buildString {
                        appendLine(selectedTitle)
                        append(linkUrl)
                    }

                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"  // MIME 타입
                        putExtra(Intent.EXTRA_TEXT, shareText)  // 공유할 내용
                        putExtra(Intent.EXTRA_TITLE, selectedTitle)  // 미리보기 제목
                        putExtra(Intent.EXTRA_SUBJECT, selectedTitle)  // 이메일 앱용 제목
                    }

                    val shareIntent = Intent.createChooser(sendIntent, "링크 공유하기")  // ShareSheet 상단에 보이는 제목
                    context.startActivity(shareIntent)
                },
                onGoClick = {
                    isDropdownVisible = false
                    uriHandler.openUri(linkUrl)
                },
                onDismiss = {
                    isDropdownVisible = false
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 20.dp),
            )
        }

        if (isDeleteModalVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .zIndex(1f)
                    .noRippleClickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DeleteLinkModal(
                        onDismiss = {
                            isDeleteModalVisible = false
                        },
                        onConfirm = {
                            isDeleteModalVisible = false
                            // TODO: 삭제 API 호출 -> 삭제 성공 후 어디로 이동하는지 물어보기
                            onBack()
                        }
                    )
                }
            }
        }

        if (isAiArticleModalVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .zIndex(2f)
                    .noRippleClickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                AIArticleModal(
                    progress = aiArticleProgress,
                    onQuit = {
                        isAiArticleModalVisible = false
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        if (openedDropdownType != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        openedDropdownType = null
                    }
            )

            when (openedDropdownType) {
                LinkDetailDropdownType.CATEGORY -> {
                    LinkDetailCategoryDropdown(
                        categories = categoryOptions,
                        selectedCategory = selectedCategory,
                        onCategoryClick = {
                            selectedCategory = it.name
                            openedDropdownType = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 200.dp, start = 24.dp)
                    )
                }

                LinkDetailDropdownType.EMOTION -> {
                    LinkDetailEmotionDropdown(
                        emotions = emotionOptions,
                        selectedEmotion = selectedEmotion,
                        onEmotionClick = {
                            selectedEmotion = it.tagName
                            openedDropdownType = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 200.dp, start = 93.dp)
                    )
                }

                LinkDetailDropdownType.SITUATION -> {
                    LinkDetailOptionDropdown(
                        options = situationOptions.map { it.tagName },
                        selectedOption = selectedSituation,
                        onOptionClick = {
                            selectedSituation = it
                            openedDropdownType = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 200.dp, start = 186.dp)
                    )
                }

                null -> Unit
            }
        }

        if (!isAiSummaryMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.maincolor)
                    .padding(vertical = 15.dp)
                    .noRippleClickable {
                        if (isEditMode) {
                            isEditMode = false
                            openedDropdownType = null
                            // 수정 API 불러오기
                        } else {
                            isAiArticleModalVisible = true
                            openedDropdownType = null

                            if (!isAiArticleProcessing) {
                                aiArticleProgress = 0f
                                isAiArticleProcessing = true
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isEditMode) {
                    Text(
                        text = "완료",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalColorTheme.current.white
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_sparkles),
                        contentDescription = null,
                        modifier = Modifier.height(17.51.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "AI 요약",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalColorTheme.current.white
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinkDetailScreen() {
    ThemeProvider {
        LinkDetailScreen(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            category = "카테고리2",
            emotion = "평온",
            situation = "통학 중",
            linkUrl = "https://blog.naver.com/linkU/1234",
            memo = "오픽 시험 준비시 도움이 되는 내용 정리, AI 활용한 공부법 정리 및 다양한 내용이 포함된 링크!!",
            tags = listOf("오픽", "AL", "영어회화", "자격증"),
            aiSummary = "오픽 시험에서는 인터뷰어 Ava와의 대화를 친구처럼 자연스럽게 임하며, 목표 점수에 맞춰 답변량과 유창성을 조절하고, MBC 구조와 콤보 유형 연습을 통해 고득점을 노리는 전략적 접근이 중요하다.",
            onBack = { },
        )
    }
}