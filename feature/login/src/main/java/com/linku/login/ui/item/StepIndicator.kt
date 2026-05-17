package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    val white = colorTheme.white

    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = 18.dp)
        ) {
            for (step in 1..3) {
                // step 원 UI
                Box(
                    modifier = when (currentStep) {
                        1 -> when (step) {
                            1 -> Modifier
                                .size(30.dp)
                                .background(activeColor, CircleShape)
                            else -> Modifier
                                .size(30.dp)
                                .border(1.dp, inactiveColor, CircleShape)
                        }

                        2 -> when (step) {
                            1 -> Modifier
                                .size(30.dp)
                                .background(colorTheme.purple[100], CircleShape)

                            2 -> Modifier
                                .size(30.dp)
                                .background(activeColor, CircleShape)
                            else -> Modifier
                                .size(30.dp)
                                .border(1.dp, inactiveColor, CircleShape)
                        }

                        3 -> when (step) {
                            1 -> Modifier
                                .size(30.dp)
                                .background(colorTheme.purple[50], CircleShape)
                            2 -> Modifier
                                .size(30.dp)
                                .background(colorTheme.purple[100], CircleShape)

                            3 -> Modifier
                                .size(28.dp)
                                .background(activeColor, CircleShape) // 3단계 3번 원은 28dp!
                            else -> Modifier
                                .size(30.dp)
                                .border(1.dp, inactiveColor, CircleShape)
                        }

                        else -> Modifier
                            .size(30.dp)
                            .border(1.dp, inactiveColor, CircleShape)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    when (currentStep) {
                        1 -> when (step) {
                            1 -> Text(
                                "1",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )

                            else -> Text(
                                step.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = inactiveColor
                            )
                        }

                        2 -> when (step) {
                            1, 2 -> if (step < 2) {
                                Image(
                                    painterResource(R.drawable.ic_login_check),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(13.dp)
                                )
                            } else {
                                Text(
                                    "2",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = white
                                )
                            }

                            else -> Text(
                                step.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = inactiveColor
                            )
                        }

                        3 -> when (step) {
                            3 -> Text(
                                "3",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )

                            else -> Image(
                                painterResource(R.drawable.ic_login_check),
                                "completed",
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(13.dp)
                            )
                        }

                        else -> Text(
                            step.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = inactiveColor
                        )
                    }
                }

                // 점선
                if (step != 3) {
                    Spacer(modifier = Modifier.width(6.dp))
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(4.2.dp)
                                .background(
                                    if (currentStep == 3) activeColor else inactiveColor,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        Text(
            text = stepLabel(currentStep),
            modifier = Modifier.padding(
                start = when (currentStep) {
                    1 -> 6.dp
                    2 -> 68.dp
                    3 -> 122.dp
                    else -> 2.dp
                },
                top = 6.dp
            ),
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