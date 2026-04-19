package com.linku.login.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.font.Paperlogy
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.linku.design.theme.LocalColorTheme
import com.linku.design.util.rememberFigmaDimens
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
    val colorTheme = LocalColorTheme.current
    val paperlogyFamily = Paperlogy.font

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
    ) {

        // 체크박스
        Box(
            modifier = Modifier
                .size((22.scaler))
                .border(
                    width = 1.dp,
                    color = if (checked) colorTheme.purple[200]!! else colorTheme.gray[300]!!,
                    shape = RoundedCornerShape((6.scaler))
                )
                .background(
                    color = if (checked) colorTheme.purple[200]!! else colorTheme.white,
                    shape = RoundedCornerShape((6.scaler))
                )
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colorTheme.white,
                    modifier = Modifier.size((14.scaler))
                )
            }
        }

        Spacer(Modifier.width((15.scaler)))

        // 텍스트 영역 (아이콘을 밀어내는 핵심)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontFamily = paperlogyFamily,
                fontWeight = FontWeight.Normal,
                color = colorTheme.black
            )

            Spacer(Modifier.width((5.scaler)))

            Text(
                text = suffix,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontFamily = paperlogyFamily,
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
