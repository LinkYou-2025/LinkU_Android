package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.component.CheckIndicator
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.design.R as DesignR

//약관 동의 3세트
@Composable
internal fun AgreementItem(
    title: String,
    suffix: String,
    suffixColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRowClick: () -> Unit
) {

    // 1. 테마 및 반응형 유틸리티 가져오기
    val colorTheme = MaterialTheme.linkuColors


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onRowClick() }
    ) {
        if (checked) {
            CheckIndicator(
                checked = true,
                modifier = Modifier.noRippleClickable { onCheckedChange(false) }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(
                        width = 1.dp,
                        color = colorTheme.gray[300],
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(
                        color = colorTheme.white,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .noRippleClickable { onCheckedChange(true) }
            )
        }

        Spacer(Modifier.width(15.scaler))

        // 텍스트 영역 (아이콘을 밀어내는 핵심)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = colorTheme.black
            )

            Spacer(Modifier.width((5.scaler)))

            Text(
                text = suffix,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight(400),
                color = suffixColor
            )
        }

        //  항상 우측, 부모 padding(32dp) 기준으로 위치
        Image(
            painter = painterResource(id = DesignR.drawable.ic_right),
            contentDescription = null,
            modifier = Modifier
                .width((8.scaler))
                .height((13.scaler))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgreementItemPreview() {
    ThemeProvider {
        val colorTheme = MaterialTheme.linkuColors
        AgreementItem(
            title = "이용약관",
            suffix = "(필수)",
            suffixColor = colorTheme.purple[200],
            checked = false,
            onCheckedChange = {},
            onRowClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgreementItemCheckedPreview() {
    ThemeProvider {
        AgreementItem(
            title = "개인정보 처리방침",
            suffix = "(필수)",
            suffixColor = MaterialTheme.linkuColors.blue[200],
            checked = true,
            onCheckedChange = {},
            onRowClick = {}
        )
    }
}
