package com.linku.file.ui.modal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont

/**
 * 공유폴더 목록과 상세 화면이 함께 사용하는 상태 없는 나가기 확인 Dialog입니다.
 *
 * 요청 중에는 확인·취소·바깥 영역·시스템 뒤로가기 dismiss를 모두 막습니다. 이 컴포넌트는
 * 대상 폴더나 API 작업을 소유하지 않으며, 단일 실행 보장의 최종 경계는 호출자가 전달한
 * [isLeaving] 상태와 ViewModel 요청 guard입니다.
 *
 * @param visible Dialog 표시 여부
 * @param isLeaving 나가기 API 요청이 진행 중인지 여부
 * @param title 확인 제목
 * @param message 나가기 결과를 설명하는 본문
 * @param confirmLabel 확인 버튼 문구와 접근성 레이블
 * @param dismissLabel 취소 버튼 문구와 접근성 레이블
 * @param onConfirm 나가기 확인 요청
 * @param onDismiss API 호출 없이 Dialog를 닫는 요청
 */
@Composable
internal fun SharedFolderLeaveDialog(
    visible: Boolean,
    isLeaving: Boolean,
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val colors = MaterialTheme.linkuColors
    val font = MaterialTheme.linkuFont.font
    val enabledAlpha = if (isLeaving) 0.55f else 1f

    Dialog(
        onDismissRequest = {
            if (!isLeaving) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isLeaving,
            dismissOnClickOutside = !isLeaving,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 372.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = colors.white,
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = 19.dp,
                        bottom = 28.dp,
                        start = 27.dp,
                        end = 27.dp,
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(com.linku.design.R.drawable.ic_logo_color),
                        contentDescription = null,
                        modifier = Modifier.size(width = 42.dp, height = 36.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = title,
                        color = colors.black,
                        fontFamily = font,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = message,
                        color = colors.gray[600],
                        fontFamily = font,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(27.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .alpha(enabledAlpha)
                                .border(
                                    width = 1.dp,
                                    brush = colors.maincolor,
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .background(
                                    color = colors.white,
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .noRippleClickable(
                                    enabled = !isLeaving,
                                    onClickLabel = dismissLabel,
                                    role = Role.Button,
                                    onClick = onDismiss,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = dismissLabel,
                                color = colors.blue[200],
                                fontFamily = font,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .alpha(enabledAlpha)
                                .background(
                                    brush = colors.maincolor,
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .noRippleClickable(
                                    enabled = !isLeaving,
                                    onClickLabel = confirmLabel,
                                    role = Role.Button,
                                    onClick = onConfirm,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isLeaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = colors.white,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(
                                    text = confirmLabel,
                                    color = colors.white,
                                    fontFamily = font,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedFolderLeaveDialogPreview() {
    LinkuPreview {
        SharedFolderLeaveDialog(
            visible = true,
            isLeaving = false,
            title = "폴더를 나가시겠습니까?",
            message = "폴더를 나가면 새로운 링크를 통해서만\n다시 이용이 가능합니다.",
            confirmLabel = "해제하기",
            dismissLabel = "취소하기",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
