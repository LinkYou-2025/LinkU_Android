package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.data.terms.MarketingTermsData
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R

@Composable
fun MarketingAgreeScreen(
    onBackClick: () -> Unit,
    isChecked: Boolean,
    onToggleClick: () -> Unit = {},
) {
    val colors = MaterialTheme.linkuColors

    val cardShape = RoundedCornerShape(22.dp)




    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.white)
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
                        .width(11.dp)
                        .noRippleClickable { onBackClick() }
                )

                Text(
                    text = "마케팅 수신 동의",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(26.75.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(colors.gray[100])
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "링큐(LINK:U) 마케팅 수신동의",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.black
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = "(선택)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.gray[600]
                            )
                        }

                        Text(
                            text = MarketingTermsData.INTRODUCTION,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = colors.black
                        )

                        MarketingTermsData.sections.forEach { (title, body) ->
                            Text(
                                text = title,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = colors.black
                            )

                            Text(
                                text = body,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = colors.black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 11.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = cardShape,
                    ambientColor = colors.black.copy(alpha = 0.03f),
                    spotColor = colors.black.copy(alpha = 0.03f)
                )
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, colors.gray[200], RoundedCornerShape(22.dp))
                .background(colors.white.copy(alpha = 0.9f))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "마케팅 수신에 동의합니다.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(
                    if (isChecked) R.drawable.ic_checkbox_checked_purple
                    else R.drawable.ic_checkbox_empty_white
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .noRippleClickable { onToggleClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMarketingAgreeScreen() {
    ThemeProvider {
        MarketingAgreeScreen(onBackClick = {}, isChecked = true)
    }
}