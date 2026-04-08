package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R
import com.linku.design.R as Res

@Composable
fun AccountSettingScreen(
    navController: NavController,
    isSocialLogin: Boolean,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onCustomInfoSettingClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(10.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "계정 설정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.75.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(LocalColorTheme.current.white)
                .graphicsLayer {
                    shadowElevation = 12.dp.toPx()
                    ambientShadowColor = Color.Black.copy(alpha = 0.02f)
                    spotShadowColor = Color.Black.copy(alpha = 0.02f)
                }
                .padding(horizontal = 20.dp, vertical = 19.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(19.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "내 정보 수정",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.black
                    )

                    Image(
                        painter = painterResource(Res.drawable.ic_detail),
                        contentDescription = null,
                        modifier = Modifier
                            .size(8.dp, 14.dp)
                            .noRippleClickable() {
                                onEditProfileClick()
                            }
                    )
                }

                if (!isSocialLogin) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "비밀번호 변경",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalColorTheme.current.black
                        )

                        Image(
                            painter = painterResource(Res.drawable.ic_detail),
                            contentDescription = null,
                            modifier = Modifier
                                .size(8.dp, 14.dp)
                                .noRippleClickable {
                                    onChangePasswordClick()
                                }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "맞춤정보 설정",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.black
                    )

                    Image(
                        painter = painterResource(Res.drawable.ic_detail),
                        contentDescription = null,
                        modifier = Modifier
                            .size(8.dp, 14.dp)
                            .noRippleClickable() {
                                onCustomInfoSettingClick()
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSettingScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        AccountSettingScreen(
            navController = navController,
            isSocialLogin = false,
            onEditProfileClick = {},
            onChangePasswordClick = {},
            onCustomInfoSettingClick = {}
        )
    }
}