package com.linku.home.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.linku.core.model.JobType
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors
import com.linku.home.R
import com.linku.home.component.EmotionSelect
import com.linku.home.component.SituationSelect
import com.linku.home.component.TimedCustomToastMessage
import com.linku.home.component.ToastType
import com.linku.home.util.UrlValidationResult
import com.linku.home.util.validateUrlInput
import java.io.File

private fun UrlValidationResult.toToastMessage(): String {
    return when (this) {
        UrlValidationResult.InvalidFormat,
        UrlValidationResult.MultipleLinks -> "유효하지 않은 링크입니다!"
        UrlValidationResult.VideoFormat -> "영상 콘텐츠는 지원하지 않아요!"
        UrlValidationResult.Valid -> "유효한 링크입니다!"
    }
}

@Composable
fun SaveLinkScreen(
    image: File?,
    url: String,
    title: String = "",
    memo: String,
    selectedEmotionId: Long?,
    selectedSituationId: Long?,
    jobId: Long,
    onPickImage: () -> Unit,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onEmotionSelect: (Long?) -> Unit,
    onSituationSelect: (Long?) -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
    isCheckingUrl: Boolean,
    isDuplicateUrl: Boolean?,
    isInvalidLink: Boolean,
) {
    val colors = MaterialTheme.linkuColors
    val jobType = JobType.fromId(jobId)

    val scrollState = rememberScrollState()
    val urlValidationResult = remember(url) { validateUrlInput(url) }

    var isUrlToastVisible by remember { mutableStateOf(false) }
    var urlToastMessage by remember { mutableStateOf("") }
    var isUrlToastSuccess by remember { mutableStateOf(false) }
    val isValidUrlFormat = urlValidationResult == UrlValidationResult.Valid

    val isButtonEnabled =
        isValidUrlFormat &&
                !isCheckingUrl &&
                !isInvalidLink &&
                isDuplicateUrl != true

    fun showUrlToast(result: UrlValidationResult) {
        urlToastMessage = result.toToastMessage()
        isUrlToastSuccess = result == UrlValidationResult.Valid
        isUrlToastVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
                .verticalScroll(scrollState)
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

            Text(
                text = "URL 링크",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.black,
                modifier = Modifier.padding(top = 31.dp, start = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp, start = 20.dp, end = 20.dp)
                    .then(
                        if (url == "") {
                            Modifier.border(1.dp, color = Basic.gray[200], shape = RoundedCornerShape(20.dp))
                        } else {
                            Modifier.border(width = 1.dp, brush = Basic.maincolor, shape = RoundedCornerShape(18.dp))
                        }
                    )
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (url.isEmpty()) {
                    Text(
                        text = "링크를 입력하거나 붙여넣어 주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[400]
                    )
                }

                BasicTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = colors.black, fontFamily = LocalFontTheme.current.font),
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
                    .then(
                        if (title.isEmpty()) {
                            Modifier.border(1.dp, color = Basic.gray[200], shape = RoundedCornerShape(20.dp))
                        } else {
                            Modifier.border(width = 1.dp, brush = Basic.maincolor, shape = RoundedCornerShape(18.dp))
                        }
                    )
                    .padding(horizontal = 22.dp, vertical = 15.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (title.isEmpty()) {
                    Text(
                        text = "링크 제목을 입력해주세요.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[400]
                    )
                }

                BasicTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
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
                    Image(
                        painter = rememberAsyncImagePainter(model = image),
                        contentDescription = "선택된 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp))
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.gray[100])
                            .border(1.dp, colors.gray[200], RoundedCornerShape(18.dp))
                            .noRippleClickable { onPickImage() }
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
                    .then(
                        if (memo.isEmpty()) {
                            Modifier.border(width = 1.dp, color = colors.gray[200], shape = RoundedCornerShape(18.dp))
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
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[400]
                    )
                }

                BasicTextField(
                    value = memo,
                    onValueChange = { if (it.length <= 200) onMemoChange(it) },
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = colors.black, fontFamily = LocalFontTheme.current.font),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp, top = 10.dp),
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
                onSituationClick = onSituationSelect
            )

            Spacer(modifier = Modifier.height(70.dp))
        }

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
                        showUrlToast(urlValidationResult)

                        if (isButtonEnabled) {
                            onSaveClick()
                        }
                    }
                    .then (
                        if (isButtonEnabled) {
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
                    textAlign = TextAlign.Center
                )
            }
        }

        TimedCustomToastMessage(
            visible = isUrlToastVisible,
            toastMessage = urlToastMessage,
            toastType = if (isUrlToastSuccess) {
                ToastType.SUCCESS
            } else {
                ToastType.ERROR
            },
            onDismiss = { isUrlToastVisible = false },
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
            onPickImage = { },
            onUrlChange = { },
            onTitleChange = { },
            onMemoChange = { },
            onEmotionSelect = { },
            onSituationSelect = { },
            onSaveClick = { },
            onBack = { },
            isCheckingUrl = false,
            isDuplicateUrl = null,
            isInvalidLink = false
        )
    }
}