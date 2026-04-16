package com.linku.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.rememberFigmaDimens
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.linku.login.R
/**
 * 직접 ui를 만들어서 추후 4,5,6단계도 손쉽게 확장이 가능함.
 * 추후 기능 확장까지 고려함.
 * */

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    label: String,
    modifier: Modifier = Modifier,

) {

    val colorTheme = LocalColorTheme.current

    val activeColor = colorTheme.purple[200]
    val inactiveColor = colorTheme.gray[300]
    val white = colorTheme.white

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = if (currentStep == 3) 6.dp else 18.dp)
        ) {
            for (step in 1..totalSteps) {

                val isCompleted = step < currentStep
                val isCurrent = step == currentStep
                val isStep3Current = currentStep == 3 && step == 3

                Box(
                    modifier = Modifier
                        .size(if (isStep3Current) 28.dp else 30.dp)
                        .then(
                            when {
                                isCompleted -> Modifier.background(
                                    when {
                                        currentStep == 3 && step == 1 -> colorTheme.purple[50]  // 1번째 원
                                        else -> colorTheme.purple[100]                           // 2번째 원
                                    },
                                    CircleShape
                                )
                                isCurrent -> Modifier.background(activeColor, CircleShape)
                                else -> Modifier.border(1.dp, inactiveColor, CircleShape)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isCompleted -> {
                            Image(
                                painter = painterResource(id = R.drawable.ic_login_check),
                                contentDescription = "completed",
                                modifier = Modifier
                                    .width(16.dp) // 피그마 상으로는 14,11인데 저번에 실제 기기로는 조금 더 작아보임. 지난번 비율 생각해서 키움.
                                    .height(13.dp)
                            )
                        }

                        isCurrent -> {
                            Text(
                                text = step.toString(),
                                fontSize = if (isStep3Current) 18.sp else 16.sp,
                                fontFamily = Paperlogy.font,
                                fontWeight = FontWeight.Bold,
                                color = white
                            )
                        }

                        else -> {
                            Text(
                                text = step.toString(),
                                fontSize = 16.sp,
                                fontFamily = Paperlogy.font,
                                fontWeight = FontWeight.Bold,
                                color = inactiveColor
                            )
                        }
                    }
                }

                // 🔹 점선 (3단계에서는 activeColor 사용)
                if (step != totalSteps) {
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

        //  라벨 위치 분기 (핵심)
        Text(
            text = label,
            modifier = Modifier.padding(
                start = when (currentStep) {
                    1 -> 6.dp      // 계정 정보
                    2 -> 68.dp      // 기존 프로필 설정
                    3 -> 122.dp     // InterestStepIndicator 그대로
                    else -> 2.dp
                },
                top = 6.dp
            ),
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontFamily = Paperlogy.font,
            fontWeight = FontWeight.Light,
            color = activeColor,
            textAlign = TextAlign.Center
        )
    }
}
@Preview(showBackground = true)
@Composable
fun StepIndicatorStep1Preview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        StepIndicator(
            currentStep = 1,
            totalSteps = 3,
            label = "계정 정보"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StepIndicatorStep2Preview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        StepIndicator(
            currentStep = 2,
            totalSteps = 3,
            label = "프로필 설정"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StepIndicatorStep3Preview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        StepIndicator(
            currentStep = 3,
            totalSteps = 3,
            label = "관심사 설정"
        )
    }
}