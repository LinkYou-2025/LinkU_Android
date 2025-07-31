package com.example.home.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.color.Basic
import com.example.home.R
import com.example.home.component.AIArticleModal
import kotlinx.coroutines.delay

@Composable
fun SaveLinkResultScreen(
    selectedImageUri: String? = null // 외부에서 전달받은 URI
) {
    var showAISummary by remember { mutableStateOf(false) }
    var showAIArticleModal by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }

    var memoText by remember { mutableStateOf("본격적으로 오픽 시험 준비하기 전에 봐야할 영상!!!!") }
    var isMemoEditing by remember { mutableStateOf(false) }

    // ✅ 수정 완료 시 텍스트 입력 종료
    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            isMemoEditing = false
        }
    }

    // ✅ showAIArticleModal 상태 변화 감지하여 3초 뒤 자동 닫힘
    LaunchedEffect(showAIArticleModal) {
        if (showAIArticleModal) {
            delay(3000)
            showAIArticleModal = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                isEditMode = isEditMode,
                showAISummary = showAISummary,
                onEditClick = { isEditMode = !isEditMode } // 🔹 토글 처리
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "링크",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, LocalColorTheme.current.gray[200])
                        .padding(horizontal = 22.dp, vertical = 15.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "링크",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(209.3.dp)
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.gray[100])
                    .border(1.dp, LocalColorTheme.current.gray[200]),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedImageUri),
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
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            color = LocalColorTheme.current.gray[800]
                        )

                        if (isEditMode) {
                            Text(
                                text = "수정 불가",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                                color = LocalColorTheme.current.blue[200],
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, LocalColorTheme.current.gray[200])
                            .background(LocalColorTheme.current.gray[100])
                            .padding(horizontal = 22.dp, vertical = 15.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawIntoCanvas {
                                val text = "#오픽 #AL #영어회화 #자격증"
                                val textSizePx = 14.sp.toPx()

                                val paintForWidth = android.graphics.Paint().apply {
                                    textSize = textSizePx
                                }
                                val textWidth = paintForWidth.measureText(text)

                                val gradient = android.graphics.LinearGradient(
                                    0f, 0f, textWidth, 0f,  // 텍스트 길이에 맞춰 그라데이션
                                    intArrayOf(
                                        0xFF2C6FFF.toInt(),
                                        0xFFCB59EB.toInt()
                                    ),
                                    null,
                                    android.graphics.Shader.TileMode.CLAMP
                                )

                                val paint = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    textSize = textSizePx
                                    shader = gradient
                                }

                                it.nativeCanvas.drawText(
                                    text,
                                    0f,
                                    size.height / 2 + textSizePx / 2.5f,
                                    paint
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
                        Canvas(
                            modifier = Modifier
                                .height(11.dp)
                                .padding(start = 4.dp)
                        ) {
                            drawIntoCanvas {
                                val text = "AI 본문 요약"
                                val textSizePx = 14.sp.toPx()

                                val paintForWidth = android.graphics.Paint().apply {
                                    textSize = textSizePx
                                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                                }
                                val textWidth = paintForWidth.measureText(text)

                                val gradient = android.graphics.LinearGradient(
                                    0f, 0f, textWidth, 0f,
                                    intArrayOf(
                                        0xFF2C6FFF.toInt(),
                                        0xFFCB59EB.toInt()
                                    ),
                                    null,
                                    android.graphics.Shader.TileMode.CLAMP
                                )

                                val paint = android.graphics.Paint().apply {
                                    isAntiAlias = true
                                    textSize = textSizePx
                                    shader = gradient
                                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
                                }

                                it.nativeCanvas.drawText(
                                    text,
                                    0f,
                                    size.height / 2 + textSizePx / 2.5f,
                                    paint
                                )
                            }
                        }

                        if (isEditMode) {
                            Text(
                                text = "수정 불가",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                                color = LocalColorTheme.current.blue[200],
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
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        LocalColorTheme.current.blue[200].copy(alpha = 0.1f),
                                        LocalColorTheme.current.purple[200].copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(horizontal = 22.dp, vertical = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "오픽 시험에서는 인터뷰어 Ava와의 대화를 친구처럼 자연스럽게 임하며, 목표 점수에 맞춰 답변량과 유창성을 조절하고, MBC 구조와 콤보 유형 연습을 통해 고득점을 노리는 전략적 접근이 중요하다.",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            color = LocalColorTheme.current.black
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
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.gray[800],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 51.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(LocalColorTheme.current.gray[100])
                        .padding(horizontal = 22.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isMemoEditing) {
                        BasicTextField(
                            value = memoText,
                            onValueChange = { memoText = it },
                            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light, color = LocalColorTheme.current.black),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = memoText,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Light),
                            color = LocalColorTheme.current.black,
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
                        .background(LocalColorTheme.current.blue[200])
                        .clickable {
                            showAIArticleModal = true
                            showAISummary = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AI 요약 보기",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = LocalColorTheme.current.white
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
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.gray[400]
                    )
                }

                Spacer(modifier = Modifier.height(41.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 14.dp, end = 19.71.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(brush = Basic.maincolor)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "링크 바로 가기",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = LocalColorTheme.current.white
                )

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(R.drawable.ic_link_go),
                    contentDescription = null,
                    modifier = Modifier.height(14.dp)
                )
            }
        }

        if (showAIArticleModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)) // 40% 투명한 검정색 배경
                    .zIndex(1f)
                    .clickable(enabled = false) {}, // 외부 클릭 막기
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AIArticleModal(
                        modifier = Modifier
//                    .align(Alignment.Center)
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    isEditMode: Boolean = false,
    showAISummary: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    // 태그 샘플
    val tags = listOf("카테고리", "감정")

    var titleText by remember { mutableStateOf("3일만에 오픽 AL") }
    var isTitleEditing by remember { mutableStateOf(false) }

    // ✅ 수정 완료 시 텍스트 입력 종료
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
            .background(LocalColorTheme.current.blue[200])
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
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "저장된 링크",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    color = LocalColorTheme.current.white
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = if (isEditMode) "완료" else "수정",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                        color = LocalColorTheme.current.blue[50],
                        modifier = Modifier
                            .clickable { onEditClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.75.dp))


            Row(
                modifier = Modifier
                    .padding(start = 24.dp),
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
                        .then(
                            if (isEditMode) {
                                Modifier
                                    .clip(RoundedCornerShape(13.dp))
                                    .border(1.dp, LocalColorTheme.current.blue[100])
                                    .padding(top = 4.dp, start = 15.dp, end = 15.dp, bottom = 4.dp)
                            } else {
                                Modifier
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTitleEditing) {
                        BasicTextField(
                            value = titleText,
                            onValueChange = { titleText = it },
                            textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LocalColorTheme.current.white)
                        )
                    } else {
                        Text(
                            text = titleText,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            color = LocalColorTheme.current.white
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
                                    titleText = ""
                                    isTitleEditing = true
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            // tags
            Row(
                modifier = Modifier
                    .padding(start = 24.dp)
            ) {
                tags.forEach { tag ->
                    Row(
                        modifier = Modifier
                            .background(
                                LocalColorTheme.current.blue[50],
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 8.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tag,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.blue[300]
                            )
                        )

                        if (isEditMode) {
                            Spacer(modifier = Modifier.width(6.dp))

                            Image(
                                painter = painterResource(R.drawable.ic_toggle),
                                contentDescription = null,
                                modifier = Modifier.height(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSaveLinkResultScreen() {
    SaveLinkResultScreen()
}