package com.linku.link.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeAnimationTarget
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.rememberAsyncImagePainter
import com.linku.R
import com.linku.core.model.JobType
import com.linku.core.model.TempImageFile
import com.linku.core.model.link.ToastEvent
import com.linku.design.component.TimedCustomToastMessage
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.link.component.EmotionSelect
import com.linku.link.component.SituationSelect
import com.linku.link.util.toTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * 새 링크에 필요한 URL, 제목, 이미지, 메모 및 분류 정보를 입력하는 화면입니다.
 *
 * 메모를 편집할 때는 글자 수 카운터가 IME 상단에 정렬될 수 있도록 스크롤 위치를 조정하고,
 * 화면 바깥의 하단 내비게이션 표시 여부는 [onMemoImeVisibilityChanged]를 통해 전달합니다.
 *
 * @param onMemoImeVisibilityChanged 메모가 포커스된 상태에서 IME가 표시되는지 전달하는 콜백
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveLinkScreen(
    image: TempImageFile?,
    url: String,
    title: String,
    memo: String,
    selectedEmotionId: Long?,
    selectedSituationId: Long?,
    jobId: Long,
    onImageSelected: (TempImageFile) -> Unit,
    onPermissionDenied: () -> Unit,
    onImageLoadFailed: () -> Unit,
    onDeleteImage: () -> Unit,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onEmotionSelect: (Long?) -> Unit,
    onSituationClick: (Long) -> Unit,
    onBack: () -> Unit,
    isSaveButtonEnabled: Boolean,
    onSaveButtonClick: () -> Unit,
    onMemoImeVisibilityChanged: (Boolean) -> Unit,
    toastEvent: Flow<ToastEvent>,
) {
    val colors = MaterialTheme.linkuColors
    val context = LocalContext.current
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val contentScrollState = rememberScrollState()
    val imeInsets = WindowInsets.ime
    val imeAnimationTargetInsets = WindowInsets.imeAnimationTarget

    val currentOnImageSelected by rememberUpdatedState(onImageSelected)
    val currentOnPermissionDenied by rememberUpdatedState(onPermissionDenied)
    val currentOnImageLoadFailed by rememberUpdatedState(onImageLoadFailed)
    val currentOnMemoImeVisibilityChanged by rememberUpdatedState(
        onMemoImeVisibilityChanged,
    )

    var isMemoFocused by remember { mutableStateOf(false) }
    var scrollViewportBottomInWindow by remember { mutableFloatStateOf(Float.NaN) }
    var memoCounterBottomInWindow by remember { mutableFloatStateOf(Float.NaN) }

    val imeBottom = imeInsets.getBottom(density)
    val shouldPrioritizeMemoAboveIme = isMemoFocused && imeBottom > 0

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        coroutineScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    uri.toTempFile(context)
                }
            }.onSuccess { tempImage ->
                currentOnImageSelected(tempImage)
            }.onFailure {
                currentOnImageLoadFailed()
            }
        }
    }

    val photoPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            imagePicker.launch("image/*")
        } else {
            currentOnPermissionDenied()
        }
    }

    val launchImagePicker = {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> {
                imagePicker.launch("image/*")
            }

            ContextCompat.checkSelfPermission(
                context,
                photoPermission,
            ) == PackageManager.PERMISSION_GRANTED -> {
                imagePicker.launch("image/*")
            }

            else -> {
                permissionLauncher.launch(photoPermission)
            }
        }
    }

    val jobType = JobType.fromId(jobId)

    var toastMessage by remember { mutableStateOf("") }
    var isToastVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        toastEvent.collect { event ->
            toastMessage = event.message
            isToastVisible = true
        }
    }

    LaunchedEffect(shouldPrioritizeMemoAboveIme) {
        currentOnMemoImeVisibilityChanged(shouldPrioritizeMemoAboveIme)
    }

    LaunchedEffect(isMemoFocused, density) {
        if (!isMemoFocused) return@LaunchedEffect

        snapshotFlow {
            Triple(
                imeInsets.getBottom(density),
                imeAnimationTargetInsets.getBottom(density),
                contentScrollState.maxValue to scrollViewportBottomInWindow,
            )
        }.collectLatest { (currentImeBottom, targetImeBottom, scrollLayout) ->
            val (measuredMaxValue, _) = scrollLayout
            val isImeShown = currentImeBottom > 0
            val isImeAnimationFinished =
                targetImeBottom <= 0 || currentImeBottom >= targetImeBottom
            val isScrollMeasured = measuredMaxValue != Int.MAX_VALUE

            if (!isImeShown || !isImeAnimationFinished || !isScrollMeasured) {
                return@collectLatest
            }

            // 하단 버튼과 내비게이션이 제거된 최종 레이아웃 좌표를 읽습니다.
            withFrameNanos { }

            val counterBottom = memoCounterBottomInWindow
            val viewportBottom = scrollViewportBottomInWindow
            if (!counterBottom.isFinite() || !viewportBottom.isFinite()) {
                return@collectLatest
            }

            // 카운터 하단과 키보드 상단 사이의 실제 차이만 스크롤에 반영합니다.
            val scrollDelta = counterBottom - viewportBottom
            if (scrollDelta > -1f && scrollDelta < 1f) {
                return@collectLatest
            }

            val targetScrollValue =
                (contentScrollState.value + scrollDelta)
                    .roundToInt()
                    .coerceIn(0, contentScrollState.maxValue)

            if (targetScrollValue != contentScrollState.value) {
                contentScrollState.scrollTo(targetScrollValue)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
            .pointerInput(focusManager) {
                // 자식이 소비하지 않은 배경 탭에서만 입력 포커스를 해제합니다.
                detectTapGestures {
                    focusManager.clearFocus(force = true)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = if (shouldPrioritizeMemoAboveIme) 0.dp else 70.dp,
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.white)
                    .padding(top = 59.dp, start = 20.dp, end = 20.dp, bottom = 22.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(11.dp)
                        .noRippleClickable { onBack() }
                )

                Text(
                    text = "새로운 링크",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalFontTheme.current.font,
                    color = colors.black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        scrollViewportBottomInWindow = coordinates.boundsInWindow().bottom
                    }
                    .verticalScroll(contentScrollState)
            ) {

                Text(
                    text = "URL 링크",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black,
                    modifier = Modifier.padding(top = 7.dp, start = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp, start = 20.dp, end = 20.dp)
                        .height(52.dp)
                        .then(
                            if (url == "") {
                                Modifier.border(
                                    1.dp,
                                    color = Basic.gray[200],
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    brush = Basic.maincolor,
                                    shape = RoundedCornerShape(18.dp)
                                )
                            }
                        )
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (url.isEmpty()) {
                        Text(
                            text = "링크를 입력하거나 붙여넣어 주세요.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[400],
                            maxLines = 1,
                        )
                    }

                    BasicTextField(
                        value = url,
                        onValueChange = onUrlChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.black,
                            fontFamily = LocalFontTheme.current.font
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 17.dp, start = 24.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "링크 제목",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black,
                    )

                    Text(
                        text = "선택",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.blue[200],
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, start = 20.dp, end = 20.dp)
                        .height(50.dp)
                        .then(
                            if (title.isEmpty()) {
                                Modifier.border(
                                    1.dp,
                                    color = Basic.gray[200],
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    brush = Basic.maincolor,
                                    shape = RoundedCornerShape(18.dp)
                                )
                            }
                        )
                        .padding(horizontal = 22.dp, vertical = 15.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (title.isEmpty()) {
                        Text(
                            text = "링크 제목을 입력해주세요.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[400],
                            maxLines = 1,
                        )
                    }

                    BasicTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.black,
                            fontFamily = LocalFontTheme.current.font
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 19.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (image != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = image.file),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(18.dp))
                                    .noRippleClickable(onClick = launchImagePicker)
                            )

                            Image(
                                painter = painterResource(R.drawable.ic_delete_gray),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 16.dp)
                                    .size(30.dp)
                                    .noRippleClickable(onClick = onDeleteImage)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(colors.gray[100])
                                .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp))
                                .noRippleClickable { launchImagePicker() }
                                .padding(38.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = null,
                                modifier = Modifier.height(24.dp)
                            )

                            Text(
                                text = "사진 추가",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                color = colors.gray[500],
                                modifier = Modifier.padding(top = 7.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 27.dp, start = 24.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "메모",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[800],
                    )

                    Text(
                        text = "선택",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.blue[200]
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 13.dp, start = 20.dp, end = 20.dp)
                        .heightIn(min = 52.dp)
                        .then(
                            if (memo.isEmpty()) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = colors.gray[200],
                                    shape = RoundedCornerShape(18.dp)
                                )
                            } else {
                                Modifier.border(
                                    border = BorderStroke(width = 1.dp, brush = Basic.maincolor),
                                    shape = RoundedCornerShape(18.dp)
                                )
                            }
                        )
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (memo.isEmpty()) {
                        Text(
                            text = "메모할 내용을 입력해주세요.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[400]
                        )
                    }

                    BasicTextField(
                        value = memo,
                        onValueChange = { if (it.length <= 200) onMemoChange(it) },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.black,
                            fontFamily = LocalFontTheme.current.font
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isMemoFocused = it.isFocused }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp, top = 10.dp)
                        .onGloballyPositioned { coordinates ->
                            // 스크롤 viewport 밖에서도 클리핑되지 않은 실제 하단 좌표를 저장합니다.
                            memoCounterBottomInWindow =
                                coordinates.positionInWindow().y + coordinates.size.height
                        },
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = memo.length.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[700]
                    )

                    Text(
                        text = "/200자",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[400],
                        modifier = Modifier.padding(start = 1.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 24.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "감정",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[800]
                    )

                    Text(
                        text = "선택",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.blue[200]
                    )
                }

                EmotionSelect(
                    selectedEmotionId = selectedEmotionId,
                    onEmotionSelect = onEmotionSelect
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 24.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "상황",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.gray[800]
                    )

                    Text(
                        text = "선택",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.blue[200]
                    )
                }

                SituationSelect(
                    jobType = jobType,
                    selectedSituationId = selectedSituationId,
                    onSituationClick = onSituationClick
                )

                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        if (!shouldPrioritizeMemoAboveIme) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .noRippleClickable {
                            onSaveButtonClick()
                        }
                        .then(
                            if (isSaveButtonEnabled) {
                                Modifier.background(Basic.maincolor)
                            } else {
                                Modifier.background(colors.gray[300])
                            }
                        )
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "저장",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.white,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        TimedCustomToastMessage(
            visible = isToastVisible,
            toastMessage = toastMessage,
            onDismiss = { isToastVisible = false },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 86.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveLinkScreen() {
    ThemeProvider {
        SaveLinkScreen(
            image = null,
            url = "",
            title = "",
            memo = "",
            selectedEmotionId = null,
            selectedSituationId = null,
            jobId = 2L,
            onImageSelected = { },
            onPermissionDenied = { },
            onImageLoadFailed = { },
            onDeleteImage = { },
            onUrlChange = { },
            onTitleChange = { },
            onMemoChange = { },
            onEmotionSelect = { },
            onSituationClick = { },
            onBack = { },
            isSaveButtonEnabled = false,
            onSaveButtonClick = { },
            onMemoImeVisibilityChanged = { },
            toastEvent = emptyFlow()
        )
    }
}
