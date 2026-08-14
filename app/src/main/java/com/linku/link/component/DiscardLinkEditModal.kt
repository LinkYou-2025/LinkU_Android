package com.linku.link.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.R
import com.linku.design.BrushText
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.Basic
import com.linku.design.theme.linkuColors

/**
 * 저장하지 않은 링크 수정 내용을 버리고 화면에서 나갈지 확인하는 모달입니다.
 *
 * 왼쪽 보조 버튼은 수정 내용을 폐기하는 동작을, 오른쪽 주요 버튼은 현재 수정 상태를
 * 유지한 채 모달을 닫는 동작을 호출합니다. 전체 화면 딤과 시스템 뒤로가기 처리는 이
 * 컴포넌트를 배치하는 상위 화면에서 담당합니다.
 *
 * @param onExit 수정 내용을 버리고 링크 상세 화면에서 나갈 때 호출됩니다.
 * @param onContinue 수정 내용을 유지하고 계속 편집할 때 호출됩니다.
 */
@Composable
fun DiscardLinkEditModal(
    onExit: () -> Unit,
    onContinue: () -> Unit,
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.white),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_linku_blur),
                contentDescription = null,
                modifier = Modifier.height(25.dp),
            )
        }

        Text(
            text = stringResource(R.string.link_edit_discard_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.black,
            modifier = Modifier.padding(top = 15.dp),
        )

        Text(
            text = stringResource(R.string.link_edit_discard_body),
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            color = colors.gray[600],
            modifier = Modifier.padding(top = 13.dp),
        )

        Row(
            modifier = Modifier.padding(top = 20.dp, start = 27.dp, end = 27.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        border = BorderStroke(1.dp, brush = Basic.maincolor),
                        shape = RoundedCornerShape(14.dp),
                    )
                    .background(colors.white)
                    .noRippleClickable(onClick = onExit),
                contentAlignment = Alignment.Center,
            ) {
                BrushText(
                    text = stringResource(R.string.link_edit_discard_exit),
                    brush = colors.maincolor,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush = colors.maincolor)
                    .noRippleClickable(onClick = onContinue),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.link_edit_discard_continue),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.white,
                )
            }
        }

        Spacer(modifier = Modifier.height(23.dp))
    }
}

/** 링크 수정 내용 폐기 확인 모달의 기본 형태를 미리 봅니다. */
@Preview(showBackground = false)
@Composable
private fun PreviewDiscardLinkEditModal() {
    ThemeProvider {
        DiscardLinkEditModal(
            onExit = {},
            onContinue = {},
        )
    }
}
