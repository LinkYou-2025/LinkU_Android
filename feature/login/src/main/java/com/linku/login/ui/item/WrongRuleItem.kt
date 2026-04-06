package com.linku.login.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.scaler


//회원가입 로직에서 사용하는 체크박스(그 네모 박스에 체크 아이콘 있는거)
@Composable
fun WrongRuleItem(
    text: String,
    // satisfied: Boolean,
    modifier: Modifier = Modifier
) {

    val colorTheme = LocalColorTheme.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // CheckIndicator(checked = satisfied) //체크박스(활성화/비활성화)
        WrongIndicator()

        Spacer(modifier = Modifier.width((8.scaler)))

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight(400),
            fontFamily = Paperlogy.font,
            color = colorTheme.negative
        )
    }
}

//프리뷰
@Preview(
    name = "PasswordRuleItem - States",
    showBackground = true
)
@Composable
private fun WrongRuleItemPreview() {


    Column(
        modifier = Modifier.padding((16.scaler)),
        verticalArrangement = Arrangement.spacedBy((8.scaler))
    ) {
        WrongRuleItem(
            text = "이미 사용 중인 닉네임입니다.",

        )
    }
}