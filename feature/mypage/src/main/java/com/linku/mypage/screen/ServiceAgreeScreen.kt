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
import androidx.compose.ui.platform.LocalUriHandler
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
fun ServiceAgreeScreen(
    navController: NavController,
    onMarketingAgreeClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.gray[100])
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(11.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "서비스 약관",
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
                .clip(RoundedCornerShape(22.dp))
                .background(LocalColorTheme.current.white)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(19.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp)
                    .noRippleClickable() {
                        uriHandler.openUri("https://linkuterms.site/terms.html")
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "서비스 이용약관",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )

                Image(
                    painter = painterResource(Res.drawable.ic_detail),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp, 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp)
                    .noRippleClickable() {
                        uriHandler.openUri("https://linkuterms.site/privacy.html")
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "개인정보 처리 방침",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )

                Image(
                    painter = painterResource(Res.drawable.ic_detail),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp, 14.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp)
                    .noRippleClickable() {
                        onMarketingAgreeClick()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "마케팅 수신 동의",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )

                Image(
                    painter = painterResource(Res.drawable.ic_detail),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp, 14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewServiceAgreeScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        ServiceAgreeScreen(
            navController = navController,
            onMarketingAgreeClick = {}
        )
    }
}