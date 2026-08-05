package com.linku.mypage.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R

@Composable
fun FaqItem(
    question: String,
    answer: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val colors = MaterialTheme.linkuColors

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "faq_arrow_rotation"
    )

    val cardShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = cardShape,
                ambientColor = colors.black.copy(alpha = 0.02f),
                spotColor = colors.black.copy(alpha = 0.02f)
            )
            .clip(cardShape)
            .background(colors.white)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_q),
                contentDescription = null,
                modifier = Modifier
                    .size(14.dp, 18.dp)
                    .align(Alignment.Top)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(11.dp))

            Image(
                painter = painterResource(R.drawable.ic_arrow_down_gray500),
                contentDescription = null,
                modifier = Modifier
                    .height(10.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        transformOrigin = TransformOrigin.Center
                    }
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(13.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.gray[200])
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = answer,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.gray[700],
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 1.dp)
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewFaqItem() {
    ThemeProvider {
        FaqItem(
            question = "링크에 붙은 별 표시는 무엇인가요?",
            answer = "링크에 마킹된 별표시는 AI 링크 요약이 되어있는 링크임을 표시하는 마크입니다.",
            expanded = true,
            onToggle = { }
        )
    }
}