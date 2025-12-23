package com.example.login.ui.item

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
import com.example.login.Paperlogy

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    label: String,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFFCB59EB),
    inactiveColor: Color = Color(0xFFD6D6D6),
    completedColor: Color = Color(0xFFE5ACF4)
) {
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
                                isCompleted -> Modifier.background(completedColor, CircleShape)
                                isCurrent -> Modifier.background(activeColor, CircleShape)
                                else -> Modifier.border(1.dp, inactiveColor, CircleShape)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isCompleted -> {
                            Text(
                                text = "✓",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        isCurrent -> {
                            Text(
                                text = step.toString(),
                                fontSize = if (isStep3Current) 18.sp else 16.sp,
                                fontFamily = Paperlogy,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        else -> {
                            Text(
                                text = step.toString(),
                                fontSize = 16.sp,
                                fontFamily = Paperlogy,
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
                    2 -> 68.dp      // 기존 프로필 설정
                    3 -> 122.dp     // InterestStepIndicator 그대로
                    else -> 2.dp
                },
                top = 6.dp
            ),
            fontSize = 13.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Light,
            color = activeColor,
            textAlign = TextAlign.Center
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