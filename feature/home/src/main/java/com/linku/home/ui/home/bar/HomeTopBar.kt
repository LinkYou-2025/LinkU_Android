package com.linku.home.ui.home.bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.Situation
import com.linku.core.model.SituationOptions
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic
import com.linku.design.theme.font.Taebaek
import com.linku.design.theme.linkuColors
import com.linku.design.top.bar.AlarmButton
import com.linku.file.ui.theme.MainColor
import com.linku.home.R
import com.linku.home.ui.home.bar.component.EmotionIconSelector
import com.linku.home.ui.home.bar.component.HomeSearchBar
import com.linku.home.ui.home.bar.component.SelectedSummaryRow
import com.linku.home.ui.home.bar.component.TaskSelector

@Composable
fun HomeTopBar(
    isNoticeExist: Boolean,  // 알림 존재 여부
    userName: String,  // 사용자 이름
    selectedEmotionId: Long?,
    onEmotionChange: (Long?) -> Unit,
    selectedTaskId: Long?,
    onTaskChange: (Long?) -> Unit,
    situations: List<Situation>,
    recommendEnabled: Boolean,
    onRecommendClick: () -> Unit,
    isCollapsed: Boolean,
    onExpandClick: () -> Unit,
    hasRequestedRecommend: Boolean,
    onAlarmClick: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors
    val density = LocalDensity.current
    val expandDragThreshold = with(density) { 20.dp.toPx() }

    val buttonBrush =
        if (recommendEnabled) Basic.maincolor
        else Brush.horizontalGradient(
            listOf(
                Color(0xFF2C6FFF).copy(alpha = 0.2f),
                Color(0xFFC800FF).copy(alpha = 0.2f)
            )
        )

    var draggedDistance by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            )
            .background(colors.white)
            .padding(start = 16.dp, end = 16.dp, top = 49.dp, bottom = 13.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 19.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 24.sp,
                            fontFamily = Taebaek.font,
                            fontWeight = FontWeight.Normal,
                            brush = MainColor,
                        )
                    ) {
                        append("링큐")
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .noRippleClickable { onAlarmClick() }
            ) {
                AlarmButton(
                    isNoticeExist = isNoticeExist,
                    modifier = Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(13.44.dp))

        HomeSearchBar()

        Spacer(modifier = Modifier.height(18.dp))

        AnimatedVisibility(
            visible = !isCollapsed,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Text(
                    text = "${userName}님의 감정과 상황을 알려주세요!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LocalFontTheme.current.font,
                    color = colors.black,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                Text(
                    text = "오늘의 감정은 어때요?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalFontTheme.current.font,
                    color = colors.gray[700],
                    modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
                )

                EmotionIconSelector(
                    selectedEmotionId = selectedEmotionId,
                    onEmotionChange = onEmotionChange
                )

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = "지금 뭐하는 중이에요?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalFontTheme.current.font,
                    color = colors.gray[700],
                    modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
                )

                TaskSelector(
                    selectedTask = selectedTaskId,
                    onTaskChange = onTaskChange,
                    situations = situations
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 80.dp, end = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(220.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush = buttonBrush)
                            .noRippleClickable(enabled = recommendEnabled) { onRecommendClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "링크 추천해줘!",
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontFamily = LocalFontTheme.current.font),
                            color = colors.white
                        )
                    }

                    Spacer(modifier = Modifier.height(6.5.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = isCollapsed,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if ((selectedEmotionId != null && selectedTaskId != null) && hasRequestedRecommend) {
                    SelectedSummaryRow(
                        selectedEmotionId = selectedEmotionId,
                        selectedTaskId = selectedTaskId,
                        situations = situations,
                    )
                }

                Spacer(modifier = Modifier.height(13.dp))

                Image(
                    painter = painterResource(R.drawable.ic_down_arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(44.dp)
                        .align(Alignment.CenterHorizontally)
                        .pointerInput(expandDragThreshold) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    draggedDistance = 0f
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    draggedDistance += dragAmount
                                },
                                onDragEnd = {
                                    if (draggedDistance >= expandDragThreshold) {
                                        onExpandClick()
                                    }
                                    draggedDistance = 0f
                                },
                                onDragCancel = {
                                    draggedDistance = 0f
                                }
                            )
                        }
                        .noRippleClickable {
                            onExpandClick()
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeTopBar() {
    var selectedEmotion by remember { mutableStateOf<Long?>(1L) }
    var selectedTask by remember { mutableStateOf<Long?>(null) }

    val sampleSituations = SituationOptions.situationsFor(jobId = 2L)

    HomeTopBar(
        isNoticeExist = true,
        userName = "루시",
        selectedEmotionId = selectedEmotion,
        onEmotionChange = { selectedEmotion = it },
        selectedTaskId = selectedTask,
        onTaskChange = { selectedTask = it },
        situations = sampleSituations,
        recommendEnabled = (selectedEmotion != null && selectedTask != null),
        onRecommendClick = { },
        isCollapsed = false,
        onExpandClick = { },
        hasRequestedRecommend = false,
        onAlarmClick = { }
    )
}
