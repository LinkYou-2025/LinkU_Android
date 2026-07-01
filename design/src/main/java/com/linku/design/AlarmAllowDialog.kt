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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.linkuColors


/**
 * 알림 허용을 요청하는 커스텀 다이얼로그입니다.
 *
 * 아이콘, 제목, 설명 문구 및 두 개의 액션 버튼(나중에/허용하기)으로 구성됩니다.
 *
 * @param onDismissRequest 다이얼로그 외부를 클릭하거나 첫 번째 버튼을 눌렀을 때 실행되는 콜백
 * @param onConfirmation 두 번째(허용하기) 버튼을 눌렀을 때 실행되는 콜백
 * @param modifier 레이아웃 수정을 위한 Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmAllowDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorTheme = MaterialTheme.linkuColors


    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .width(372.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorTheme.white)
                .padding(vertical = 28.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_alarm),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(Modifier.size(12.dp))

            Text(
                text = "링큐의 알림을 받아보세요",
                color = colorTheme.black,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )

            Spacer(Modifier.size(16.dp))

            Text(
                text = "AI 요약 완료, 공유 폴더 업데이트,\n맞춤 큐레이션 등 서비스 이용에 필요한\n알림을 받아보실 수 있습니다.",
                textAlign = TextAlign.Center,
                color = colorTheme.gray[600],
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.size(19.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size( 88.dp, 34.dp).
                            clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "나중에",
                        style = LocalTextStyle.current.copy(
                            brush = colorTheme.maincolor
                        )
                    )
                }

                Spacer(Modifier.width(8.dp))

                TextButton(
                    onClick = onConfirmation,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(colorTheme.maincolor)
                        .size(88.dp, 34.dp)
                ) {
                    Text(
                        text = "허용하기",
                        color = colorTheme.white,
                        fontFamily = LocalFontTheme.current.font,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlarmAllowDialogPreview() {
    LinkuPreview {
        AlarmAllowDialog(
            onDismissRequest = {},
            onConfirmation = {}
        )
    }
}
