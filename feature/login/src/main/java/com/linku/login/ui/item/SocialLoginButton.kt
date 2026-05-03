package com.linku.login.ui.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.auth.LoginType
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R

@Composable
internal fun SocialLoginButton(
    modifier: Modifier = Modifier,
    type: LoginType,
    onClick: () -> Unit = {}
) {
    val colorTheme = MaterialTheme.linkuColors

    val backgroundColor = when (type) {
        LoginType.KAKAO -> Color(0xFFFEE500)
        LoginType.GOOGLE -> colorTheme.white
        LoginType.EMAIL -> Color.Transparent
    }
    val text = when (type) {
        LoginType.KAKAO -> "카카오로 시작하기"
        LoginType.GOOGLE -> "Google로 시작하기"
        LoginType.EMAIL -> "이메일로 시작하기"
    }
    val textColor = when (type) {
        LoginType.KAKAO -> colorTheme.black
        LoginType.GOOGLE -> colorTheme.googleLoginColor
        LoginType.EMAIL -> colorTheme.white
    }
    val borderColor = when (type) {
        LoginType.KAKAO -> null
        LoginType.GOOGLE -> Color(0xFFE0E0E0)
        LoginType.EMAIL -> Color.White
    }
    val iconRes = when (type) {
        LoginType.KAKAO -> R.drawable.icon_login_kakao
        LoginType.GOOGLE -> R.drawable.icon_login_google
        LoginType.EMAIL -> null
    }
    val iconSize = when (type) {
        LoginType.KAKAO -> Modifier.size(width = 22.scaler, height = 20.scaler)
        LoginType.GOOGLE -> Modifier.size(20.scaler)
        LoginType.EMAIL -> Modifier  // null이라 어차피 안 쓰임
    }

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
                    modifier = iconSize
                )

                Spacer(modifier = Modifier.width((8.scaler)))
            }

            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontFamily = FontFamily.Default // 안드로이드 기본 폰트 roboto 사용함.
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SocialLoginButtonPreview() {

    val colorTheme = MaterialTheme.linkuColors

    LinkuPreview {
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
                    type = LoginType.KAKAO
                )

                // 구글
                SocialLoginButton(
                    type = LoginType.GOOGLE
                )

                // 이메일
                SocialLoginButton(
                    type = LoginType.EMAIL
                )
            }
        }
    }
}
