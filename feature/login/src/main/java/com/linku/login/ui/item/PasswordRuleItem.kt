package com.linku.login.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.rememberFigmaDimens
import com.linku.design.util.scaler
import com.linku.login.R


//회원가입 로직에서 사용하는 체크박스(그 네모 박스에 체크 아이콘 있는거)
@Composable
fun PasswordRuleItem(
    text: String,
    satisfied: Boolean,
    modifier: Modifier = Modifier
) {
    //디자인 모듈.
    val colorTheme = LocalColorTheme.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckIndicator(checked = satisfied) //체크박스(활성화/비활성화)

        Spacer(modifier = Modifier.width((8.scaler)))

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            fontFamily = Paperlogy.font,
            color = colorTheme.gray[600]
        )
    }
}

//프리뷰
@Preview(
    name = "PasswordRuleItem - States",
    showBackground = true
)
@Composable
private fun PasswordRuleItemPreview() {


    Column(
        modifier = Modifier.padding((16.scaler)),
        verticalArrangement = Arrangement.spacedBy((8.scaler))
    ) {
        PasswordRuleItem(
            text = "영문자를 포함해야 해요",
            satisfied = true
        )

        PasswordRuleItem(
            text = "숫자를 포함해야 해요",
            satisfied = false
        )
    }
}