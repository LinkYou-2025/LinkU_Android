package com.linku.design.modal

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
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.color.Basic

/**
 * 사용자에게 중요한 정보를 알리거나 특정 동작에 대한 확인을 받기 위한 모달 다이얼로그 창입니다.
 *
 * 이 컴포넌트는 화면 중앙에 배치되며, 제목, 본문 콘텐츠, 그리고 최대 2개의 버튼(확인/취소)을 제공합니다.
 * [negativeText]가 비어있지 않은 경우에만 취소 버튼이 활성화됩니다.
 *
 * @param visible 모달 창의 표시 여부. true일 때 화면에 나타납니다.
 * @param onOkay '확인' 성격의 버튼을 클릭했을 때 실행될 콜백 함수.
 * @param onDismiss 모달을 닫아야 할 때(취소 버튼 클릭 또는 외부 영역 클릭 시) 실행될 콜백 함수.
 * @param positiveText 확인 버튼에 표시될 텍스트.
 * @param negativeText 취소 버튼에 표시될 텍스트. 빈 문자열일 경우 취소 버튼은 표시되지 않습니다.
 * @param title 모달 상단에 표시될 강조된 제목 텍스트.
 * @param textBody 모달 중앙에 배치될 사용자 정의 Composable 본문 내용.
 *
 * @see androidx.compose.ui.window.Dialog
 */
@Composable
fun ModalWindow(
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
                color = Basic.white
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
                        fontFamily = LocalFontTheme.current.font,
                        fontWeight = FontWeight(500),
                        color = Basic.black,
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
                                        brush = Basic.maincolor,
                                        shape = RoundedCornerShape(size = 14.dp)
                                    )
                                    .background(
                                        color = Basic.white,
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
                                                fontFamily = LocalFontTheme.current.font,

                                                // 폰트 굵기 (500)
                                                fontWeight = FontWeight(500),

                                                // 텍스트 그라데이션 색상(링큐 메인 색상)
                                                brush = Basic.maincolor,
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
                                    brush = Basic.maincolor,
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
                                fontFamily = LocalFontTheme.current.font,
                                fontWeight = FontWeight(500),
                                color = Basic.white,
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
private fun ModalWindowTest(){
    ModalWindow(
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
            fontFamily = LocalFontTheme.current.font,
            fontWeight = FontWeight(400),
            color = Basic.gray[600],
            textAlign = TextAlign.Center,
        )
    }
}