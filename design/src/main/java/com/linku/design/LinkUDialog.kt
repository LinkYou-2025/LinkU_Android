package com.linku.design


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme


/**
 * LinkU 디자인 시스템을 따르는 커스텀 알림 다이얼로그입니다.
 *
 * 아이콘, 제목, 설명 문구 및 두 개의 액션 버튼(취소/확인)으로 구성됩니다.
 *
 * @param onDismissRequest 다이얼로그 외부를 클릭하거나 첫 번째 버튼을 눌렀을 때 실행되는 콜백
 * @param onConfirmation 두 번째(확인) 버튼을 눌렀을 때 실행되는 콜백
 * @param dialogTitle 다이얼로그 상단에 표시될 제목
 * @param dialogText 다이얼로그 중앙에 표시될 상세 설명 문구
 * @param buttonText1 첫 번째 버튼(일반적으로 취소 또는 나중에)의 텍스트
 * @param buttonText2 두 번째 버튼(일반적으로 확인 또는 허용)의 텍스트
 * @param modifier 레이아웃 수정을 위한 Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkUDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
    dialogTitle: String = "링큐의 알림을 받아보세요",
    dialogText: String = "AI 요약 완료, 공유 폴더 업데이트,\n맞춤 큐레이션 등 서비스 이용에 필요한\n알림을 받아보실 수 있습니다.",
    buttonText1: String = "나중에",
    buttonText2: String = "허용하기",
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .width(372.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LocalColorTheme.current.white)
                .padding(vertical = 28.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_alarm),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(Modifier.size(12.dp))

            Text(
                text = dialogTitle,
                color = LocalColorTheme.current.black,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )

            Spacer(Modifier.size(16.dp))

            Text(
                text = dialogText,
                textAlign = TextAlign.Center,
                color = LocalColorTheme.current.gray[600],
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.size(19.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = modifier.size( 88.dp, 34.dp).
                            clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = buttonText1,
                        style = TextStyle(
                            fontFamily = LocalFontTheme.current.font,
                            brush = LocalColorTheme.current.maincolor
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = onConfirmation,
                    modifier = modifier.clip(RoundedCornerShape(8.dp))
                        .background(LocalColorTheme.current.maincolor)
                        .size(88.dp, 34.dp)
                ) {
                    Text(
                        text = buttonText2,
                        style = TextStyle(
                            color = LocalColorTheme.current.white,
                            fontFamily = LocalFontTheme.current.font,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LinkUDialogPreview() {
    LinkuPreview {
        LinkUDialog(
            onDismissRequest = {},
            onConfirmation = {}
        )
    }
}