package com.example.home.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.home.R
import java.io.File

@Composable
fun SaveLinkScreen(
    image: File?,
    url: String,
    memo: String,
    selectedEmotionId: Long?,
    onPickImage: () -> Unit,
    onUrlChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onEmotionSelect: (Long?) -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
    isCheckingUrl: Boolean,
    isDuplicateUrl: Boolean?,
    isInvalidLink: Boolean,
) {
    val bannedDomains = listOf("youtube.com", "youtu.be")
    val showVideoWarning = bannedDomains.any { url.contains(it, ignoreCase = true) }
    val isButtonEnabled =
        url.isNotBlank() &&
        !isCheckingUrl &&
        !showVideoWarning &&
        !isInvalidLink &&
        (isDuplicateUrl != true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
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
                    modifier = Modifier
                        .size(width = 10.dp, height = 16.25.dp)
                        .clickable { onBack() }
                )

                Spacer(modifier = Modifier.width(131.dp))

                Text(
                    text = "새로운 링크",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.black
                )
            }

            Text(
                text = "URL 링크 입력",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = LocalColorTheme.current.black,
                modifier = Modifier.padding(top = 31.dp, start = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
                    .height(50.dp)
                    .border(width = 1.dp, brush = Basic.maincolor, shape = RoundedCornerShape(18.dp))
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (url.isEmpty()) {
                    Text(
                        text = "링크를 입력하거나 붙여넣어 주세요.",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                BasicTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = LocalColorTheme.current.black),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // URL 검사 결과 메시지
            when {
                url.isBlank() -> Unit
                showVideoWarning -> WarningText("현재 링큐에서는 영상 콘텐츠를 지원하지 않아요!")
                isCheckingUrl -> Text(
                    text = "링크를 확인 중입니다…",
                    style = TextStyle(fontSize = 13.sp),
                    color = LocalColorTheme.current.gray[600],
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                )
                isInvalidLink -> {
                    WarningText("유효하지 않은 링크입니다! 다시 입력해주세요.")
                }
                isDuplicateUrl == true -> WarningText("이미 저장된 링크예요.")
                isDuplicateUrl == false -> Text(
                    text = "저장 가능한 링크예요.",
                    style = TextStyle(fontSize = 13.sp),
                    color = LocalColorTheme.current.blue[200],
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                )
                else -> Unit
            }

//            if (isInvalidLink) {
//                WarningText("유효하지 않은 링크입니다! 다시 입력해주세요.")
//            }
//
//            if (showVideoWarning) {
//                WarningText("현재 링큐에서는 영상 콘텐츠를 지원하지 않아요!")
//            }

            // 둘 다 false일 때만 Spacer 추가
            if (!isInvalidLink && !showVideoWarning) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(209.3.dp)
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.gray[100])
                    .border(1.dp, LocalColorTheme.current.gray[200], RoundedCornerShape(18.dp))
                    .clickable { onPickImage() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (image != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = image),
                        contentDescription = "선택된 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // ✅ 박스에 꽉 차도록
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
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            color = LocalColorTheme.current.gray[500],
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "메모",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text(
                    text = "선택",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.blue[200],
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    .height(50.dp)
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
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                if (memo.isEmpty()) {
                    Text(
                        text = "메모할 내용을 입력해주세요.",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                BasicTextField(
                    value = memo,
                    onValueChange = { if (it.length <= 200) onMemoChange(it) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = LocalColorTheme.current.black),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp, top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memo.length.toString(),
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[700]
                )

                Text(
                    text = "/200자",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.gray[400]
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "감정",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 8.dp)
                )

                Text(
                    text = "선택",
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = LocalColorTheme.current.blue[200],
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            EmotionSelect(
                selectedEmotionId = selectedEmotionId,
                onEmotionSelect = onEmotionSelect
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
                        brush = if (isButtonEnabled) {
                            Basic.maincolor
                        } else {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0x1A2C6FFF),
                                    Color(0x1AC800FF)
                                )
                            )
                        }
                    )

                    .clickable(enabled = isButtonEnabled) { onSaveClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "저장",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun WarningText(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
        color = LocalColorTheme.current.negative,
        modifier = modifier.padding(start = 32.dp)
    )
}

@Composable
fun EmotionSelect(
    selectedEmotionId: Long?,
    onEmotionSelect: (Long?) -> Unit
) {
    val emotions = listOf(
        1L to ("😃" to "즐거움"),
        2L to ("😐" to "평온"),
        3L to ("😍" to "설렘"),
        4L to ("🥲" to "우울"),
        5L to ("😫" to "짜증"),
        6L to ("😡" to "분노")
    )

    Column(
        modifier = Modifier.padding(top = 15.dp, start = 20.dp)
    ) {
        // 첫 번째 줄: 4개
        Row {
            emotions.take(4).forEach { (id, pair) ->
                EmotionBadge(
                    emoji = pair.first, label = pair.second,
                    selected = selectedEmotionId == id,
                    onToggle = { onEmotionSelect(if (selectedEmotionId == id) null else id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 두 번째 줄: 2개
        Row {
            emotions.drop(4).forEach { (id, pair) ->
                EmotionBadge(
                    emoji = pair.first, label = pair.second,
                    selected = selectedEmotionId == id,
                    onToggle = { onEmotionSelect(if (selectedEmotionId == id) null else id) }
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
private fun EmotionBadge(
    emoji: String,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val boxBackground = Brush.horizontalGradient(
        listOf(
            Color(0x1A2C6FFF),
            Color(0x1AC800FF)
        )
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = if (selected) boxBackground
                else SolidColor(LocalColorTheme.current.white)
            )
            .then(
                if (selected) Modifier.border(
                    width = 1.dp,
                    brush = Basic.maincolor,
                    shape = RoundedCornerShape(20.dp)
                ) else Modifier.border(
                    width = 1.dp,
                    color = LocalColorTheme.current.gray[200],
                    shape = RoundedCornerShape(20.dp)
                )
            )
            .clickable { onToggle() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp), // 고정된 높이와 너비 설정
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = TextStyle(fontSize = 16.sp)  // 조금 작다면 18로 하기
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) LocalColorTheme.current.black else LocalColorTheme.current.gray[800]
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveLinkScreen() {
    SaveLinkScreen(
        image = null,
        url = "",
        memo = "",
        selectedEmotionId = null,
        onPickImage = {},
        onUrlChange = {},
        onMemoChange = {},
        onEmotionSelect = {},
        onSaveClick = {},
        onBack = {},
        isCheckingUrl = false,
        isDuplicateUrl = null,
        isInvalidLink = false
    )
}