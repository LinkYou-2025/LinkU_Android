package com.example.login.ui.item

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
import com.example.login.Paperlogy
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.design.R as DesignR

//약관 동의 3세트
@Composable
fun AgreementItem(
    title: String,
    suffix: String,
    suffixColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRowClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
    ) {

        // 체크박스
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(
                    width = 1.dp,
                    color = if (checked) Color(0xFFD35EFF) else Color(0xFFD7D9DF),
                    shape = RoundedCornerShape(6.dp)
                )
                .background(
                    color = if (checked) Color(0xFFD35EFF) else Color.White,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(Modifier.width(15.dp))

        // ⭐ 텍스트 영역 (아이콘을 밀어내는 핵심)
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.width(5.dp))

            Text(
                text = suffix,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight(400),
                color = suffixColor
            )
        }

        //  항상 우측, 부모 padding(32dp) 기준으로 위치
        Image(
            painter = painterResource(id = DesignR.drawable.ic_right),
            contentDescription = null,
            modifier = Modifier
                .width(8.dp)
                .height(13.dp)
        )
    }
}
