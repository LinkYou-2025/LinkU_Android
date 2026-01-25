package com.example.login.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.theme.font.Paperlogy
import com.example.design.theme.LocalColorTheme
import com.example.design.util.rememberFigmaDimens
import com.example.design.util.scaler
import com.example.login.R

@Composable
fun SocialLoginButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    borderColor: Color? = null,
    iconRes: Int? = null, //이메일로 시작하기는 아이콘이 없기에.
    text: String,
    textColor: Color,
    onClick: () -> Unit = {}
) {



    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = (372.scaler)) // 너비 반응형 적용
            .height((50.scaler)),
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        border = borderColor?.let { BorderStroke(1.dp, it) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
                .padding(horizontal = (18.scaler)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            //  아이콘
            if (iconRes != null) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size((22.scaler))
                )

                Spacer(modifier = Modifier.width((8.scaler)))
            }

            Text(
                text = text,
                fontFamily = Paperlogy.font,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SocialLoginButtonPreview() {
    val colorTheme = LocalColorTheme.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            //  프리뷰용 배경 (피그마 그라데이션)
            .background(brush = colorTheme.maincolor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (20.scaler)), // 좌우 여백 20
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((10.scaler)) //반응형으로 변경.ㄴ
        ) {

            // 카카오
            SocialLoginButton(
                backgroundColor = Color(0xFFFEE500),
                iconRes = R.drawable.icon_login_kakao,
                text = "카카오로 시작하기",
                textColor = colorTheme.black
            )

            //  네이버
            SocialLoginButton(
                backgroundColor = Color(0xFF03C75A),
                iconRes = R.drawable.icon_login_naver,
                text = "네이버로 시작하기",
                textColor = colorTheme.white
            )

            // 구글
            SocialLoginButton(
                backgroundColor = colorTheme.white,
                borderColor = Color(0xFFE0E0E0),
                iconRes = R.drawable.icon_login_google,
                text = "구글로 시작하기",
                textColor = colorTheme.black
            )

            // 이메일
            SocialLoginButton(
                backgroundColor = Color.Transparent,
                borderColor = Color.White,
                iconRes = null,
                text = "이메일로 시작하기",
                textColor = Color.White
            )
        }
    }
}
