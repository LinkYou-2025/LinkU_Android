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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.rememberAsyncImagePainter
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.home.R
import com.linku.home.component.EmotionSelect
import com.linku.home.component.SituationSelect
import java.io.File

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
    val scrollState = rememberScrollState()
    val bannedDomains = listOf("youtube.com", "youtu.be")
    val showVideoWarning = bannedDomains.any { url.contains(it, ignoreCase = true) }
    val isButtonEnabled =
        url.isNotBlank() &&
        !isCheckingUrl &&
        !showVideoWarning &&
        !isInvalidLink &&
        (isDuplicateUrl != true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
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
                    color = LocalColorTheme.current.black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Text(
                text = "URL 링크",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalColorTheme.current.black,
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
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                BasicTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = LocalColorTheme.current.black, fontFamily = LocalFontTheme.current.font),
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
                    color = LocalColorTheme.current.black,
                )

                Text(
                    text = "선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.blue[200],
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
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                BasicTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.black,
                        fontFamily = LocalFontTheme.current.font
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

//            // URL 검사 결과 메시지
//            when {
//                url.isBlank() -> Unit
//                showVideoWarning -> WarningText("현재 링큐에서는 영상 콘텐츠를 지원하지 않아요!")
//                isCheckingUrl -> Text(
//                    text = "링크를 확인 중입니다…",
//                    style = TextStyle(fontSize = 13.sp, fontFamily = LocalFontTheme.current.font),
//                    color = LocalColorTheme.current.gray[600],
//                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
//                )
//                isInvalidLink -> {
//                    WarningText("유효하지 않은 링크입니다! 다시 입력해주세요.")
//                }
//                isDuplicateUrl == true -> WarningText("이미 저장된 링크예요.")
//                isDuplicateUrl == false -> Text(
//                    text = "저장 가능한 링크예요.",
//                    style = TextStyle(fontSize = 13.sp, fontFamily = LocalFontTheme.current.font),
//                    color = LocalColorTheme.current.blue[200],
//                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
//                )
//                else -> Unit
//            }
//
////            if (isInvalidLink) {
////                WarningText("유효하지 않은 링크입니다! 다시 입력해주세요.")
////            }
////
////            if (showVideoWarning) {
////                WarningText("현재 링큐에서는 영상 콘텐츠를 지원하지 않아요!")
////            }
//
//            // 둘 다 false일 때만 Spacer 추가
//            if (!isInvalidLink && !showVideoWarning) {
//                Spacer(modifier = Modifier.height(12.dp))
//            }

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
                            .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(18.dp))
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(LocalColorTheme.current.gray[100])
                            .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(18.dp))
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
                            color = LocalColorTheme.current.gray[500],
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
                    color = LocalColorTheme.current.gray[800],
                )

                Text(
                    text = "선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.blue[200]
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp, start = 20.dp, end = 20.dp)
                    .then(
                        if (memo.isEmpty()) {
                            Modifier.border(width = 1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(18.dp))
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
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                BasicTextField(
                    value = memo,
                    onValueChange = { if (it.length <= 200) onMemoChange(it) },
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = LocalColorTheme.current.black, fontFamily = LocalFontTheme.current.font),
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
                    color = LocalColorTheme.current.gray[700]
                )

                Text(
                    text = "/200자",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.gray[400],
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
                    color = LocalColorTheme.current.gray[800]
                )

                Text(
                    text = "선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.blue[200]
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
                    color = LocalColorTheme.current.gray[800]
                )

                Text(
                    text = "선택",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.blue[200]
                )
            }

            SituationSelect(
                jobId = jobId,
                selectedSituationId = selectedSituationId,
                onSituationSelect = onSituationSelect
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
                    .noRippleClickable(enabled = isButtonEnabled) { onSaveClick() }
                    .then (
                        if (isButtonEnabled) {
                            Modifier.background(Basic.maincolor)
                        } else {
                            Modifier.background(LocalColorTheme.current.gray[300])
                        }
                    )
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "저장",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColorTheme.current.white,
                    textAlign = TextAlign.Center
                )
            }
        }
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