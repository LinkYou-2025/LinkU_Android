package com.linku.design.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.R
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
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
 * @see Dialog
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
            ModalWindowContent(
                onOkay = onOkay,
                onDismiss = onDismiss,
                positiveText = positiveText,
                negativeText = negativeText,
                title = title,
                textBody = textBody
            )
        }
    }
}

@Composable
private fun ModalWindowContent(
    onOkay: () -> Unit = {},
    onDismiss: () -> Unit,
    positiveText: String,
    negativeText: String = "",
    title: String,
    textBody: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Basic.white
    ) {
        Column(
            modifier = Modifier
                .width(372.dp)
                .padding(start = 28.dp, end = 28.dp, top = 19.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_modal_logo),
                tint = Color.Unspecified,
                contentDescription = "링큐 로고"
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontFamily = LocalFontTheme.current.font,
                fontWeight = FontWeight(500),
                color = Basic.black,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            textBody()

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .height(48.07692.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (negativeText.isNotEmpty()) {
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
                            .noRippleClickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontFamily = LocalFontTheme.current.font,
                                        fontWeight = FontWeight(500),
                                        brush = Basic.maincolor,
                                    )
                                ) {
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
                        .noRippleClickable {
                            onOkay()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
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

@Preview(showBackground = true)
@Composable
private fun ModalWindowPreview() {
    LinkuPreview {
        ModalWindowContent(
            title = "AI 요약을 완료하지 못했어요",
            positiveText = "확인",
            negativeText = "취소",
            onDismiss = {},
            textBody = {
                Text(
                    text = "모달 본문 내용입니다. 여기에 상세 내용을 작성합니다.",
                    fontSize = 14.sp,
                    fontFamily = LocalFontTheme.current.font,
                    color = LocalColorTheme.current.gray[600],
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalWindowSingleButtonPreview() {
    LinkuPreview {
        ModalWindowContent(
            title = "AI 요약을 완료하지 못했어요",
            positiveText = "나가기",
            onDismiss = {},
            textBody = {
                Text(
                    text = "링크는 저장되었어요.\nAI 요약은 잠시 후 다시 시도해주세요.",
                    fontSize = 14.sp,
                    fontFamily = LocalFontTheme.current.font,
                    color = LocalColorTheme.current.gray[600],
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}
