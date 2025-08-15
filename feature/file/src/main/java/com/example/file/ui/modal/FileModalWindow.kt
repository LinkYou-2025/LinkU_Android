package com.example.file.ui.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.file.modifier.noRippleClickable
import com.example.file.ui.theme.*

@Composable
fun FileModalWindow(
    visible: Boolean,
    onOkay: () -> Unit = {},
    onDismiss: () -> Unit,
    positiveText: String,
    negativeText: String = "",
    title: String,
    textBody: @Composable () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = White
            ){
                Column(
                    modifier = Modifier
                        .width(372.dp)
                        .padding(27.dp),
                    verticalArrangement = Arrangement.spacedBy(36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 18.dp),
                        text = title,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = DefaultFont,
                        fontWeight = FontWeight(500),
                        color = Black,
                        textAlign = TextAlign.Center,
                    )

                    textBody()

                    Row(
                        modifier = Modifier
                            .height(48.07692.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ){
                        if(negativeText.isNotEmpty()){
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(
                                        width = 1.dp,
                                        brush = MainColor,
                                        shape = RoundedCornerShape(size = 14.dp)
                                    )
                                    .background(
                                        color = White,
                                        shape = RoundedCornerShape(size = 14.dp)
                                    )
                                    .noRippleClickable {onDismiss()},
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    // 텍스트(그라데이션 및 스타일 지정)
                                    text = buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                // 폰트 크기 (16sp)
                                                fontSize = 16.sp,

                                                // 사용할 폰트 (paperlogy 폰트)
                                                fontFamily = DefaultFont,

                                                // 폰트 굵기 (500)
                                                fontWeight = FontWeight(500),

                                                // 텍스트 그라데이션 색상(링큐 메인 색상)
                                                brush = MainColor,
                                            )
                                        ) {
                                            // 실제 표시할 텍스트
                                            append(negativeText)
                                        }
                                    }
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    brush = MainColor,
                                    shape = RoundedCornerShape(size = 14.dp)
                                )
                                .noRippleClickable{
                                    onOkay()
                                    onDismiss()
                                  },
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = positiveText,
                                fontSize = 16.sp,
                                fontFamily = DefaultFont,
                                fontWeight = FontWeight(500),
                                color = White,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FileModalWindowTest(){
    FileModalWindow(
        true,
        {},
        {},
        "확인",
        "취소",
        "해당 폴더를 비공개 하시겠습니까?"
    ){
        Text(
            text = "해당 폴더는 타인과 공유중인 폴더입니다.\n비공개 폴더로 전환하시겠습니까?",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontFamily = DefaultFont,
            fontWeight = FontWeight(400),
            color = Gray600,
            textAlign = TextAlign.Center,
        )
    }
}