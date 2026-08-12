package com.linku.link.screen

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.link.component.AIArticleModal
import com.linku.link.component.DeleteLinkModal
import com.linku.link.component.DiscardLinkEditModal
import com.linku.link.component.LinkCategoryOption
import com.linku.link.component.LinkDetailAction
import com.linku.link.component.LinkDetailCategoryDropdown
import com.linku.link.component.LinkDetailCustomDropdown
import com.linku.link.component.LinkDetailEmotionDropdown
import com.linku.link.component.LinkDetailSituationDropdown
import com.linku.link.component.LinkDetailTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private enum class LinkDetailDropdownType {
    CATEGORY,
    EMOTION,
    SITUATION
}

private const val MAX_MEMO_LENGTH = 200

/**
 * 윈도우 기준 칩 영역을 링크 상세 화면 기준 드롭다운 시작 좌표로 변환합니다.
 *
 * @param screenBoundsInWindow 링크 상세 화면 컨테이너의 윈도우 기준 영역입니다.
 * @param chipBoundsInWindow 드롭다운과 연결된 칩의 윈도우 기준 영역입니다.
 * @param verticalGapPx 칩 하단과 드롭다운 상단 사이의 세로 간격입니다.
 * @return 화면 컨테이너 기준 드롭다운 좌표이며, 좌표 측정 전에는 `null`입니다.
 */
private fun calculateDropdownOffset(
    screenBoundsInWindow: Rect?,
    chipBoundsInWindow: Rect?,
    verticalGapPx: Float,
): IntOffset? {
    val screenBounds = screenBoundsInWindow ?: return null
    val chipBounds = chipBoundsInWindow ?: return null

    return IntOffset(
        x = (chipBounds.left - screenBounds.left).roundToInt(),
        y = (chipBounds.bottom - screenBounds.top + verticalGapPx).roundToInt(),
    )
}

/**
 * 상황 드롭다운의 오른쪽 끝을 상황 칩의 오른쪽 끝에 맞추는 좌표를 계산합니다.
 *
 * 드롭다운은 화면 컨테이너의 오른쪽 위를 기준으로 배치되므로, 가로 좌표는 화면 오른쪽과
 * 칩 오른쪽 사이의 차이를 사용합니다. 세로 좌표는 다른 드롭다운과 동일하게 칩 하단에서
 * 지정된 간격만큼 떨어진 위치를 사용합니다.
 *
 * @param screenBoundsInWindow 링크 상세 화면 컨테이너의 윈도우 기준 영역입니다.
 * @param chipBoundsInWindow 상황 칩의 윈도우 기준 영역입니다.
 * @param verticalGapPx 상황 칩 하단과 드롭다운 상단 사이의 세로 간격입니다.
 * @return 화면 오른쪽 위 기준 드롭다운 좌표이며, 좌표 측정 전에는 `null`입니다.
 */
private fun calculateEndAlignedDropdownOffset(
    screenBoundsInWindow: Rect?,
    chipBoundsInWindow: Rect?,
    verticalGapPx: Float,
): IntOffset? {
    val screenBounds = screenBoundsInWindow ?: return null
    val chipBounds = chipBoundsInWindow ?: return null

    return IntOffset(
        x = (chipBounds.right - screenBounds.right).roundToInt(),
        y = (chipBounds.bottom - screenBounds.top + verticalGapPx).roundToInt(),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinkDetailScreen(
    linkuId: Long,
    linkTitle: String,
    categoryId: Long?,
    emotion: String,
    situationId: Long?,
    linkUrl: String,
    imageUrl: String? = "",
    selectedImageUri: Uri? = null,
    memo: String,
    tags: List<String>,
    aiSummary: String,
    isAiArticleLoading: Boolean,
    aiArticleErrorMessage: String?,
    categoryOptions: List<LinkCategoryOption>,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onDiscardSelectedImage: () -> Unit,
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
    onRequestAiArticle: (Long) -> Unit,
    onClearAiArticleError: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    val caller = getCaller()

    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val detailScrollState = rememberScrollState()
    val imeInsets = WindowInsets.ime
    val imeAnimationTargetInsets = WindowInsets.imeAnimationTarget

    var isMemoFocused by remember { mutableStateOf(false) }

    // IME가 표시될 때 메모 하단에는 키보드 높이의 절반만 추가합니다.
    val imeBottom = imeInsets.getBottom(density)
    val memoImeBottomPadding = if (isMemoFocused && imeBottom > 0) {
        with(density) { (imeBottom * 0.5f).toDp() }
    } else {
        0.dp
    }

    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var isAiSummaryMode by rememberSaveable(linkuId) { mutableStateOf(aiSummary.isNotBlank()) }

    var isDropdownVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteModalVisible by rememberSaveable { mutableStateOf(false) }
    var isAiArticleModalVisible by rememberSaveable { mutableStateOf(false) }
    var isDiscardEditModalVisible by rememberSaveable(linkuId) { mutableStateOf(false) }
    var aiArticleProgress by rememberSaveable { mutableFloatStateOf(0f) }

    var editToastMessage by rememberSaveable { mutableStateOf("") }
    var isEditToastVisible by rememberSaveable { mutableStateOf(false) }

    val emotionOptions = EmotionType.entries
    val situationOptions = SituationOptions.allSituations

    var selectedTitle by rememberSaveable { mutableStateOf(linkTitle) }
    var selectedCategoryId by rememberSaveable(linkuId) { mutableStateOf(categoryId) }
    var selectedEmotion by rememberSaveable {
        mutableStateOf(
            EmotionType.entries.firstOrNull { it.tagName == emotion }
        )
    }
    var selectedSituationId by rememberSaveable { mutableStateOf(situationId) }
    var selectedMemo by rememberSaveable { mutableStateOf(memo) }

    // 편집 도중 상세 API가 갱신되어도 비교 기준이 움직이지 않도록 진입 시점의 값을 보관합니다.
    var editBaselineTitle by rememberSaveable(linkuId) { mutableStateOf(linkTitle) }
    var editBaselineMemo by rememberSaveable(linkuId) { mutableStateOf(memo) }
    var editBaselineCategoryId by rememberSaveable(linkuId) { mutableStateOf(categoryId) }
    var editBaselineEmotionId by rememberSaveable(linkuId) {
        mutableStateOf(
            EmotionType.entries.firstOrNull { option -> option.tagName == emotion }?.id?.value
        )
    }
    var editBaselineSituationId by rememberSaveable(linkuId) { mutableStateOf(situationId) }
    var isEditBaselineCaptured by rememberSaveable(linkuId) { mutableStateOf(false) }

    val isTitleValid = selectedTitle.isNotBlank()
    val isSaveButtonEnabled = !isEditMode || isTitleValid

    val selectedSituation = situationOptions.firstOrNull {
        it.id.value == selectedSituationId
    }

    // ID를 기준으로 현재 선택 항목을 찾으면 목록이 늦게 도착해도 이름과 색상이 자동으로 갱신됩니다.
    val selectedCategoryOption = categoryOptions.firstOrNull { option ->
        option.id == selectedCategoryId
    }
    val selectedCategoryName = selectedCategoryOption?.name ?: "카테고리"
    val selectedCategoryColorStyle =
        selectedCategoryOption?.colorStyle ?: CategoryColorStyle.DEFAULT

    var openedDropdownType by rememberSaveable {
        mutableStateOf<LinkDetailDropdownType?>(null)
    }

    // 서버에 실제로 전달되는 정규화 값과 ID를 기준으로 변경 여부를 판단합니다.
    val hasEditChanges = isEditMode && isEditBaselineCaptured && (
        selectedImageUri != null ||
            selectedTitle.trim() != editBaselineTitle.trim() ||
            selectedMemo.trim() != editBaselineMemo.trim() ||
            selectedCategoryId != editBaselineCategoryId ||
            selectedEmotion?.id?.value != editBaselineEmotionId ||
            selectedSituationId != editBaselineSituationId
        )

    // 수정 전 값으로 초안을 복원하고 링크 상세 화면을 유지한 채 수정 모드만 종료합니다.
    val discardEditChanges: () -> Unit = {
        selectedTitle = editBaselineTitle
        selectedMemo = editBaselineMemo
        selectedCategoryId = editBaselineCategoryId
        selectedEmotion = EmotionType.entries.firstOrNull { option ->
            option.id.value == editBaselineEmotionId
        }
        selectedSituationId = editBaselineSituationId
        onDiscardSelectedImage()
        openedDropdownType = null
        isEditMode = false
        isEditBaselineCaptured = false
        isDiscardEditModalVisible = false
    }

    // 상단 화살표와 시스템 뒤로가기가 동일한 변경 확인 흐름을 사용하도록 통합합니다.
    val requestBack: () -> Unit = {
        when {
            imeBottom > 0 -> {
                // 입력 중에는 화면 이동보다 키보드와 포커스를 먼저 정리합니다.
                softwareKeyboardController?.hide()
                focusManager.clearFocus(force = true)
            }
            isDiscardEditModalVisible -> isDiscardEditModalVisible = false
            openedDropdownType != null -> openedDropdownType = null
            hasEditChanges -> isDiscardEditModalVisible = true
            isEditMode -> discardEditChanges()
            else -> onBack()
        }
    }

    BackHandler(enabled = isEditMode) {
        requestBack()
    }

    // 칩과 오버레이가 서로 다른 레이아웃에 있으므로 윈도우 좌표를 공통 기준으로 사용합니다.
    var screenBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var bodyBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var categoryChipBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var emotionChipBoundsInWindow by remember { mutableStateOf<Rect?>(null) }
    var situationChipBoundsInWindow by remember { mutableStateOf<Rect?>(null) }

    val dropdownVerticalGapPx = with(density) { 12.dp.toPx() }
    val categoryDropdownOffset = calculateDropdownOffset(
        screenBoundsInWindow = screenBoundsInWindow,
        chipBoundsInWindow = categoryChipBoundsInWindow,
        verticalGapPx = dropdownVerticalGapPx,
    )
    val emotionDropdownOffset = calculateDropdownOffset(
        screenBoundsInWindow = screenBoundsInWindow,
        chipBoundsInWindow = emotionChipBoundsInWindow,
        verticalGapPx = dropdownVerticalGapPx,
    )
    val situationDropdownOffset = calculateEndAlignedDropdownOffset(
        screenBoundsInWindow = screenBoundsInWindow,
        chipBoundsInWindow = situationChipBoundsInWindow,
        verticalGapPx = dropdownVerticalGapPx,
    )

    // 본문 시작점을 화면 기준으로 변환해 동적인 헤더 높이만큼 딤 영역을 제외합니다.
    val dropdownDimTopPadding = screenBoundsInWindow?.let { screenBounds ->
        bodyBoundsInWindow?.let { bodyBounds ->
            with(density) {
                (bodyBounds.top - screenBounds.top).coerceAtLeast(0f).toDp()
            }
        }
    }

    val visibleTags = tags
        .filter { it.isNotBlank() }
        .take(4)
        .map { tag ->
            if (tag.startsWith("#")) tag else "#$tag"
        }

    LaunchedEffect(linkTitle, categoryId, emotion, situationId, memo) {
        if (!isEditMode) {
            selectedTitle = linkTitle
            selectedCategoryId = categoryId
            selectedEmotion = EmotionType.entries.firstOrNull { it.tagName == emotion }
            selectedSituationId = situationId
            selectedMemo = memo
        }
    }

    LaunchedEffect(isAiArticleLoading) {
        if (isAiArticleLoading) {
            aiArticleProgress = 0f

            while (isAiArticleLoading && aiArticleProgress < 0.9f) {
                delay(100.milliseconds)

                aiArticleProgress = when {
                    aiArticleProgress < 0.5f -> {
                        (aiArticleProgress +
                                0.025f).coerceAtMost(0.5f)
                    }

                    aiArticleProgress < 0.75f -> {
                        (aiArticleProgress +
                                0.01f).coerceAtMost(0.75f)
                    }

                    else -> {
                        (aiArticleProgress +
                                0.003f).coerceAtMost(0.9f)
                    }
                }
            }
        }
    }

    LaunchedEffect(aiSummary, isAiArticleLoading) {
        if (!isAiArticleLoading && aiSummary.isNotBlank()) {
            aiArticleProgress = 1f

            if (isAiArticleModalVisible) {
                delay(250.milliseconds)
            }

            isAiArticleModalVisible = false
            isAiSummaryMode = true
        }
    }

    LaunchedEffect(aiArticleErrorMessage) {
        if (aiArticleErrorMessage != null) {
            isAiArticleModalVisible = false
            aiArticleProgress = 0f

            editToastMessage = aiArticleErrorMessage
            isEditToastVisible = true

            onClearAiArticleError()
        }
    }

    LaunchedEffect(isEditMode, isMemoFocused, density) {
        if (!isEditMode || !isMemoFocused) return@LaunchedEffect

        snapshotFlow {
            Triple(
                imeInsets.getBottom(density),
                imeAnimationTargetInsets.getBottom(density),
                detailScrollState.maxValue,
            )
        }.collectLatest { (currentImeBottom, targetImeBottom, measuredMaxValue) ->
            val isImeShown = currentImeBottom > 0
            val isImeAnimationFinished =
                targetImeBottom <= 0 || currentImeBottom >= targetImeBottom
            val isScrollMeasured = measuredMaxValue != Int.MAX_VALUE

            if (!isImeShown || !isImeAnimationFinished || !isScrollMeasured) {
                return@collectLatest
            }

            // 최종 IME 여백과 늘어난 메모 높이가 측정된 다음 화면 최하단으로 이동합니다.
            withFrameNanos { }

            val latestMaxValue = detailScrollState.maxValue
            if (
                latestMaxValue != Int.MAX_VALUE &&
                latestMaxValue > detailScrollState.value
            ) {
                detailScrollState.animateScrollTo(latestMaxValue)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
            .onGloballyPositioned { coordinates ->
                screenBoundsInWindow = coordinates.boundsInWindow()
            }
            .pointerInput(focusManager) {
                // 자식이 소비하지 않은 배경 탭에서만 입력 포커스를 해제합니다.
                detectTapGestures {
                    focusManager.clearFocus(force = true)
                }
            }
    ) {
        // IME가 나타나도 상단바는 고정하고, 아래 본문 영역만 줄어들도록 전체 높이를 점유합니다.
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 헤더의 둥근 하단 모서리 뒤에도 본문과 동일한 딤 배경이 이어지도록 합니다.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (openedDropdownType != null) {
                            colors.black.copy(alpha = 0.5f)
                        } else {
                            colors.white
                        }
                    )
            ) {
                LinkDetailTopBar(
                    linkTitle = selectedTitle,
                    originalLinkTitle = linkTitle,
                    category = selectedCategoryName,
                    categoryColorStyle = selectedCategoryColorStyle,
                    emotion = selectedEmotion?.tagName ?: "감정",
                    situation = selectedSituation?.tagName ?: "상황",
                    isEditMode = isEditMode,
                    isCategoryDropdownOpen = openedDropdownType == LinkDetailDropdownType.CATEGORY,
                    isEmotionDropdownOpen = openedDropdownType == LinkDetailDropdownType.EMOTION,
                    isSituationDropdownOpen = openedDropdownType == LinkDetailDropdownType.SITUATION,
                    onBack = requestBack,
                    onMoreClick = {
                        isDropdownVisible = !isDropdownVisible
                    },
                    onLinkGoClick = { uriHandler.openUri(linkUrl) },
                    onCategoryClick = {
                        if (categoryOptions.isNotEmpty()) {
                            openedDropdownType =
                                if (openedDropdownType == LinkDetailDropdownType.CATEGORY) null
                                else LinkDetailDropdownType.CATEGORY
                        }
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
                    onCategoryChipBoundsChanged = { bounds ->
                        categoryChipBoundsInWindow = bounds
                    },
                    onEmotionChipBoundsChanged = { bounds ->
                        emotionChipBoundsInWindow = bounds
                    },
                    onSituationChipBoundsChanged = { bounds ->
                        situationChipBoundsInWindow = bounds
                    },
                    onTitleChange = { newTitle ->
                        selectedTitle = newTitle
                    },
                    onTitleClearClick = {
                        selectedTitle = ""
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        bodyBoundsInWindow = coordinates.boundsInWindow()
                    }
                    .verticalScroll(detailScrollState)
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
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
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

                // 기존 메모 여백에 IME 높이의 절반만 동적으로 더해 과도한 빈 공간을 방지합니다.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp, bottom = 50.dp)
                        .padding(bottom = memoImeBottomPadding)
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
                                fontFamily = font,
                                lineHeight = 20.sp,
                                color = colors.black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { isMemoFocused = it.isFocused }
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
                                            fontFamily = font,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .noRippleClickable {
                        isDropdownVisible = false
                    }
            )

            LinkDetailCustomDropdown(
                onAction = { action ->
                    isDropdownVisible = false
                    openedDropdownType = null

                    when (action) {
                        LinkDetailAction.EDIT -> {
                            // 변경 여부는 편집 진입 당시 화면에 표시된 값을 고정 기준으로 비교합니다.
                            editBaselineTitle = selectedTitle
                            editBaselineMemo = selectedMemo
                            editBaselineCategoryId = selectedCategoryId
                            editBaselineEmotionId = selectedEmotion?.id?.value
                            editBaselineSituationId = selectedSituationId
                            isEditBaselineCaptured = true
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
                    .zIndex(2f)
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
                    .zIndex(1f)
                    .noRippleClickable {
                        openedDropdownType = null
                    }
            ) {
                dropdownDimTopPadding?.let { dimTopPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = dimTopPadding)
                            .background(colors.black.copy(alpha = 0.5f))
                    )
                }
            }

            when (openedDropdownType) {
                LinkDetailDropdownType.CATEGORY -> {
                    categoryDropdownOffset?.let { dropdownOffset ->
                        LinkDetailCategoryDropdown(
                            categories = categoryOptions,
                            selectedCategoryId = selectedCategoryId,
                            onCategoryClick = {
                                selectedCategoryId = it.id
                                openedDropdownType = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset { dropdownOffset }
                                .zIndex(2f)
                        )
                    }
                }

                LinkDetailDropdownType.EMOTION -> {
                    emotionDropdownOffset?.let { dropdownOffset ->
                        LinkDetailEmotionDropdown(
                            emotions = emotionOptions,
                            selectedEmotion = selectedEmotion?.tagName.orEmpty(),
                            onEmotionClick = {
                                selectedEmotion = it
                                openedDropdownType = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset { dropdownOffset }
                                .zIndex(2f)
                        )
                    }
                }

                LinkDetailDropdownType.SITUATION -> {
                    situationDropdownOffset?.let { dropdownOffset ->
                        LinkDetailSituationDropdown(
                            situations = situationOptions,
                            selectedSituation = selectedSituation,
                            onSituationClick = {
                                selectedSituationId = it.id.value
                                openedDropdownType = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset { dropdownOffset }
                                .zIndex(2f)
                        )
                    }
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
                                    isEditBaselineCaptured = false
                                    isDiscardEditModalVisible = false
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

                            if (!isAiArticleLoading) {
                                aiArticleProgress = 0f
                                onRequestAiArticle(linkuId)
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

        if (isDiscardEditModalVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .zIndex(4f)
                    .noRippleClickable {
                        // 딤 배경은 계속 수정하기와 동일하게 초안을 유지하고 모달만 닫습니다.
                        isDiscardEditModalVisible = false
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .noRippleClickable { },
                    contentAlignment = Alignment.Center,
                ) {
                    DiscardLinkEditModal(
                        onExit = {
                            // 나가기는 상세 화면을 유지하고 수정 전 상태로만 돌아갑니다.
                            discardEditChanges()
                        },
                        onContinue = {
                            // 계속 수정하기는 초안과 비교 기준을 그대로 둔 채 모달만 닫습니다.
                            isDiscardEditModalVisible = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLinkDetailScreen() {
    // 카테고리 더미데이터
    val categoryOptions = listOf(
        LinkCategoryOption(1L, "카테고리2", CategoryColorStyle.categoryStyleList[0]),
        LinkCategoryOption(2L, "카테고리3", CategoryColorStyle.categoryStyleList[1]),
        LinkCategoryOption(3L, "카테고리4", CategoryColorStyle.categoryStyleList[2]),
        LinkCategoryOption(4L, "카테고리5", CategoryColorStyle.categoryStyleList[3]),
        LinkCategoryOption(5L, "카테고리6", CategoryColorStyle.categoryStyleList[4]),
        LinkCategoryOption(6L, "카테고리7", CategoryColorStyle.categoryStyleList[5])
    )

    ThemeProvider {
        LinkDetailScreen(
            linkuId = 0L,
            linkTitle = "3일만에 오픽 AL 꿀팁",
            categoryId = 1L,
            emotion = "평온",
            situationId = 10L,
            linkUrl = "https://blog.naver.com/linkU/1234567890",
            memo = "오픽 시험 준비시 도움이 되는 내용 정리",
            tags = listOf("오픽", "AL", "영어회화", "자격증"),
            aiSummary = "",
            isAiArticleLoading = false,
            aiArticleErrorMessage = null,
            onRequestAiArticle = { },
            onClearAiArticleError = { },
            categoryOptions = categoryOptions,
            onBack = { },
            onPickImage = { },
            onDiscardSelectedImage = { },
            onSubmitEdit = { _, _, _, _, _, _, _ -> },
            onDeleteLink = { _, _ -> }
        )
    }
}
