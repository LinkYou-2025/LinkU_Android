package com.linku.link.screen

import android.content.ClipData
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil3.compose.AsyncImage
import com.linku.R
import com.linku.core.model.EmotionType
import com.linku.core.model.SituationOptions
import com.linku.core.util.caller.getCaller
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.core.util.logging.e
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.link.component.AIArticleModal
import com.linku.link.component.DeleteLinkModal
import com.linku.link.component.LinkCategoryOption
import com.linku.link.component.LinkDetailAction
import com.linku.link.component.LinkDetailCategoryDropdown
import com.linku.link.component.LinkDetailCustomDropdown
import com.linku.link.component.LinkDetailEmotionDropdown
import com.linku.link.component.LinkDetailSituationDropdown
import com.linku.link.component.LinkDetailTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private enum class LinkDetailDropdownType {
    CATEGORY,
    EMOTION,
    SITUATION
}

private const val MAX_MEMO_LENGTH = 200

@Composable
fun LinkDetailScreen(
    linkTitle: String,
    category: String,
    emotion: String,
    situationId: Long?,
    linkUrl: String,
    imageUrl: String? = "",
    selectedImageUri: Uri? = null,
    memo: String,
    tags: List<String>,
    aiSummary: String,
    categoryOptions: List<LinkCategoryOption>,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onSubmitEdit: (
        title: String,
        memo: String?,
        categoryId: Long?,
        emotionId: Long?,
        situationId: Long?,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
    ) -> Unit,
    onDeleteLink: (
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
    ) -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val caller = getCaller()

    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var isAiSummaryMode by rememberSaveable { mutableStateOf(false) }

    var isDropdownVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteModalVisible by rememberSaveable { mutableStateOf(false) }
    var isAiArticleModalVisible by rememberSaveable { mutableStateOf(false) }
    var isAiArticleProcessing by rememberSaveable { mutableStateOf(false) }
    var aiArticleProgress by rememberSaveable { mutableFloatStateOf(0f) }

    var editToastMessage by rememberSaveable { mutableStateOf("") }
    var isEditToastVisible by rememberSaveable { mutableStateOf(false) }

    val emotionOptions = EmotionType.entries
    val situationOptions = SituationOptions.allSituations

    var selectedTitle by rememberSaveable { mutableStateOf(linkTitle) }
    var selectedCategory by rememberSaveable { mutableStateOf(category) }
    var selectedCategoryId by rememberSaveable {
        mutableStateOf(
            categoryOptions.firstOrNull { it.name == category }?.id
        )
    }
    var selectedEmotion by rememberSaveable {
        mutableStateOf(
            EmotionType.entries.firstOrNull { it.tagName == emotion }
        )
    }
    var selectedSituationId by rememberSaveable { mutableStateOf(situationId) }
    var selectedMemo by rememberSaveable { mutableStateOf(memo) }

    val isTitleValid = selectedTitle.isNotBlank()
    val isSaveButtonEnabled = !isEditMode || isTitleValid

    val selectedSituation = situationOptions.firstOrNull {
        it.id.value == selectedSituationId
    }

    var openedDropdownType by rememberSaveable {
        mutableStateOf<LinkDetailDropdownType?>(null)
    }

    val visibleTags = tags
        .filter { it.isNotBlank() }
        .take(4)
        .map { tag ->
            if (tag.startsWith("#")) tag else "#$tag"
        }

    LaunchedEffect(linkTitle, category, emotion, situationId, memo, categoryOptions) {
        if (!isEditMode) {
            selectedTitle = linkTitle
            selectedCategory = category
            selectedCategoryId = categoryOptions.firstOrNull { it.name == category }?.id
            selectedEmotion = EmotionType.entries.firstOrNull { it.tagName == emotion }
            selectedSituationId = situationId
            selectedMemo = memo
        }
    }

    LaunchedEffect(isAiArticleProcessing) {
        if (isAiArticleProcessing) {
            aiArticleProgress = 0f

            while (aiArticleProgress < 1f) {
                delay(80.milliseconds)
                aiArticleProgress = (aiArticleProgress + 0.02f).coerceAtMost(1f)
            }

            delay(300.milliseconds)

            isAiArticleProcessing = false
            isAiArticleModalVisible = false
            isAiSummaryMode = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LinkDetailTopBar(
                linkTitle = selectedTitle,
                originalLinkTitle = linkTitle,
                category = selectedCategory,
                emotion = selectedEmotion?.tagName ?: "감정",
                situation = selectedSituation?.tagName ?: "상황",
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
                onTitleChange = { newTitle ->
                    selectedTitle = newTitle
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
                    AsyncImage(
                        model = selectedImageUri ?: imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.img_link_detail_default),
                        error = painterResource(R.drawable.img_link_detail_default),
                        onLoading = {
                            LinkuLog.d(caller) { "loading: ${selectedImageUri ?: imageUrl}" }
                        },
                        onSuccess = {
                            LinkuLog.d(caller) { "success: ${selectedImageUri ?: imageUrl}" }
                        },
                        onError = { state ->
                            LinkuLog.e(caller, state.result.throwable) { "error: ${selectedImageUri ?: imageUrl}" }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .alpha(if (isEditMode) 0.6f else 1f)
                            .border(
                                width = 1.dp,
                                color = colors.gray[200],
                                shape = RoundedCornerShape(18.dp)
                            )
                    )

                    if(isEditMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .noRippleClickable(onClick = onPickImage),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(colors.gray[700])
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
                                    color = colors.white
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
                            color = colors.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(colors.white)
                        .padding(top = 7.5.dp, start = 22.dp, end = 8.5.dp, bottom = 7.5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = linkUrl,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp,
                            color = if (isEditMode) colors.gray[400] else colors.black,
                            modifier = Modifier
                                .weight(1f)
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
                                color = colors.gray[600],
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.gray[200])
                                    .noRippleClickable {
                                        coroutineScope.launch {
                                            clipboard.setClipEntry(
                                                ClipEntry(
                                                    ClipData.newPlainText("linkUrl", linkUrl)
                                                )
                                            )
                                        }
                                    }
                                    .padding(horizontal = 13.5.dp, vertical = 7.dp)
                            )
                        }
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
                                color = colors.black
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
                                        color = colors.black,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .border(1.dp, colors.inactiveColor, RoundedCornerShape(20.dp))
                                            .background(colors.white)
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
                                color = colors.black
                            )
                        }

                        Text(
                            text = aiSummary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.black,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, colors.inactiveColor, RoundedCornerShape(18.dp))
                                .background(colors.white)
                                .padding(horizontal = 22.dp, vertical = 16.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp, bottom = 50.dp)
                ) {
                    Text(
                        text = "메모",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditMode) {
                        BasicTextField(
                            value = selectedMemo,
                            onValueChange = { newMemo ->
                                selectedMemo = newMemo.take(MAX_MEMO_LENGTH)
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 20.sp,
                                color = colors.black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(colors.gray[100])
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
                                            color = colors.gray[400]
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
                            color = colors.black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(colors.gray[100])
                                .padding(horizontal = 22.dp, vertical = 15.5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    if (!isAiSummaryMode) {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        if (isDropdownVisible) {
            LinkDetailCustomDropdown(
                onAction = { action ->
                    isDropdownVisible = false
                    openedDropdownType = null

                    when (action) {
                        LinkDetailAction.EDIT -> {
                            isEditMode = true
                        }

                        LinkDetailAction.DELETE -> {
                            isDeleteModalVisible = true
                        }

                        LinkDetailAction.SHARE -> {
                            val shareText = buildString {
                                appendLine(selectedTitle)
                                append(linkUrl)
                            }

                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                putExtra(Intent.EXTRA_TITLE, selectedTitle)
                                putExtra(Intent.EXTRA_SUBJECT, selectedTitle)
                            }

                            val shareIntent = Intent.createChooser(sendIntent, "링크 공유하기")
                            context.startActivity(shareIntent)
                        }

                        LinkDetailAction.GO -> {
                            uriHandler.openUri(linkUrl)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 20.dp)
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
                            onDeleteLink(
                                {
                                    isDeleteModalVisible = false
                                    onBack()
                                },
                                {
                                    isDeleteModalVisible = false
                                    editToastMessage = "링크를 삭제하지 못했어요. 다시 시도해 주세요."
                                    isEditToastVisible = true
                                }
                            )
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
                        selectedCategoryId = selectedCategoryId,
                        onCategoryClick = {
                            selectedCategoryId = it.id
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
                        selectedEmotion = selectedEmotion?.tagName.orEmpty(),
                        onEmotionClick = {
                            selectedEmotion = it
                            openedDropdownType = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 200.dp, start = 93.dp)
                    )
                }

                LinkDetailDropdownType.SITUATION -> {
                    LinkDetailSituationDropdown(
                        situations = situationOptions,
                        selectedSituation = selectedSituation,
                        onSituationClick = {
                            selectedSituationId = it.id.value
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
                    .background(
                        if (isEditMode && !isSaveButtonEnabled) {
                            colors.inactiveColor
                        } else {
                            colors.maincolor
                        }
                    )
                    .padding(vertical = 15.dp)
                    .noRippleClickable(enabled = isSaveButtonEnabled) {
                        if (isEditMode) {
                            onSubmitEdit(
                                selectedTitle.trim(),
                                selectedMemo,
                                selectedCategoryId,
                                selectedEmotion?.id?.value,
                                selectedSituationId,
                                {
                                    editToastMessage = "저장 완료!"
                                    isEditToastVisible = true

                                    isEditMode = false
                                    openedDropdownType = null
                                },
                                {
                                    // 실패 시 수정 모드 유지 + 입력값도 그대로 유지
                                    editToastMessage = "수정에 실패했어요. 다시 시도해 주세요."
                                    isEditToastVisible = true
                                },
                            )
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
                        color = colors.white
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
                        color = colors.white
                    )
                }
            }
        }

        TimedCustomToastMessage(
            visible = isEditToastVisible,
            toastMessage = editToastMessage,
            onDismiss = {
                isEditToastVisible = false
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 86.dp)
                .zIndex(3f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinkDetailScreen() {
    // 카테고리 더미데이터
    val categoryOptions = listOf(
        LinkCategoryOption(1L, "카테고리2", Color(0xFF55D6C2)),
        LinkCategoryOption(2L, "카테고리3", Color(0xFFFFBE3D)),
        LinkCategoryOption(3L, "카테고리4", Color(0xFF2FB4E9)),
        LinkCategoryOption(4L, "카테고리5", Color(0xFFFF5757)),
        LinkCategoryOption(5L, "카테고리6", Color(0xFF67D414)),
        LinkCategoryOption(6L, "카테고리7", Color(0xFFD9DEE6))
    )

    ThemeProvider {
        LinkDetailScreen(
            linkTitle = "3일만에 오픽 AL 꿀팁",
            category = "카테고리2",
            emotion = "평온",
            situationId = 10L,
            linkUrl = "https://blog.naver.com/linkU/1234567890",
            memo = "오픽 시험 준비시 도움이 되는 내용 정리, AI 활용한 공부법 정리 및 다양한 내용이 포함된 링크!!",
            tags = listOf("오픽", "AL", "영어회화", "자격증"),
            aiSummary = "오픽 시험에서는 인터뷰어 Ava와의 대화를 친구처럼 자연스럽게 임하며, 목표 점수에 맞춰 답변량과 유창성을 조절하고, MBC 구조와 콤보 유형 연습을 통해 고득점을 노리는 전략적 접근이 중요하다.",
            categoryOptions = categoryOptions,
            onBack = { },
            onPickImage = { },
            onSubmitEdit = { _, _, _, _, _, _, _ -> },
            onDeleteLink = { _, _ -> }
        )
    }
}