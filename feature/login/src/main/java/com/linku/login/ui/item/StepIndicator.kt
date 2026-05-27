package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.R


private fun stepLabel(step: Int) = when (step) {
    1 -> "계정 정보"
    2 -> "프로필 설정"
    3 -> "관심사 설정"
    else -> ""
}

@Composable
internal fun StepIndicator(
    currentStep: Int
) {

    val colorTheme = MaterialTheme.linkuColors
    val activeColor = colorTheme.purple[200]
    val inactiveColor = colorTheme.gray[300]

    val backgrounds = when (currentStep) {
        1 -> listOf(activeColor, colorTheme.white, colorTheme.white)
        2 -> listOf(colorTheme.purple[100], activeColor, colorTheme.white)
        3 -> listOf(colorTheme.purple[50], colorTheme.purple[100], activeColor)
        else -> listOf(colorTheme.white, colorTheme.white, colorTheme.white)
    }

    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = 18.dp)
        ) {
            for (step in 1..3) {
                val isCompleted = step < currentStep
                val isCurrent = step == currentStep

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .then(
                            if (isCurrent || isCompleted) Modifier.background(
                                backgrounds[step - 1],
                                CircleShape
                            )
                            else Modifier.border(1.dp, inactiveColor, CircleShape)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Image(
                            painter = painterResource(R.drawable.ic_login_check),
                            contentDescription = "completed",
                            modifier = Modifier.size(width = 16.dp, height = 13.dp)
                        )
                    } else {
                        Text(
                            text = step.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) colorTheme.white else inactiveColor
                        )
                    }
                }

                if (step < 3) {
                    Spacer(modifier = Modifier.width(6.dp))
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(4.2.dp)
                                .background(
                                    color = if (isCompleted) activeColor else inactiveColor,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        val labelStartPadding = when (currentStep) {
            1 -> 6.dp
            2 -> 68.dp
            3 -> 132.dp
            else -> 2.dp
        }

        Text(
            text = stepLabel(currentStep),
            modifier = Modifier.padding(start = labelStartPadding, top = 6.dp),
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Light,
            color = activeColor,
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun StepIndicatorPreview() {
    LinkuPreview {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            StepIndicator(currentStep = 1)
            StepIndicator(currentStep = 2)
            StepIndicator(currentStep = 3)
        }
    }
}