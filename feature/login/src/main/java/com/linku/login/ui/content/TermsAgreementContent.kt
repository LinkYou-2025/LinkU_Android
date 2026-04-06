package com.linku.login.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.theme.font.Paperlogy
import com.linku.login.ui.item.AgreementItem
import com.linku.login.ui.item.GradientButtonCore
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.linku.design.R
import com.linku.design.theme.LocalColorTheme
import com.linku.design.util.scaler


@Composable
fun TermsAgreementContent(
    agreeTerms: Boolean,
    agreePrivacy: Boolean,
    agreeMarketing: Boolean,
    onAgreeTermsChange: (Boolean) -> Unit,
    onAgreePrivacyChange: (Boolean) -> Unit,
    onAgreeMarketingChange: (Boolean) -> Unit,
    onClickTerms: () -> Unit,
    onClickPrivacy: () -> Unit,
    onClickMarketing: () -> Unit,
    onNextClicked: (Boolean, Boolean, Boolean) -> Unit
) {
    // 1. 테마 및 반응형 유틸 가져오기
    val colorTheme = LocalColorTheme.current
    val paperlogyFamily = Paperlogy.font

    val agreeAll = agreeTerms && agreePrivacy && agreeMarketing
    val nextEnabled = agreeTerms && agreePrivacy

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        //  바텀시트 실제 높이 기준
        val topPadding = maxHeight * (46f / 280f)

        Box(modifier = Modifier.fillMaxWidth() .background(colorTheme.white)) {

            Column {

                /* ───── 전체동의 Row (좌우 32, 위 46/280) ───── */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = (32.scaler),
                            end = (32.scaler),
                            top = (36.scaler)
                            //top = topPadding 화면으로 계산이 되어서 일단 보류.
                        )
                ) {
                    // 커스텀 체크박스
                    Box(
                        modifier = Modifier
                            .size(22.scaler) // 반응형 적용
                            .border(
                                width = 1.dp,
                                color = if (agreeAll) colorTheme.purple[200] else colorTheme.gray[300],
                                shape = RoundedCornerShape(6.scaler) //반응형 적용
                            )
                            .background(
                                color = if (agreeAll) colorTheme.purple[200] else colorTheme.white,
                                shape = RoundedCornerShape(6.scaler) //반응형 적용
                            )
                            .clickable {
                                val checked = !agreeAll
                                onAgreeTermsChange(checked)
                                onAgreePrivacyChange(checked)
                                onAgreeMarketingChange(checked)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (agreeAll) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_checkbox_checked),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(11.scaler)
                                    .height(8.scaler) //반응형으로 수정.
                            )
                        }
                    }

                    Spacer(Modifier.width(15.scaler))

                    Text(
                        text = "약관 전체동의",
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = paperlogyFamily,
                        fontWeight = FontWeight.Medium,
                        color = colorTheme.black
                    )

                    Spacer(Modifier.width(8.scaler))

                    Text(
                        text = "선택항목에 대한 동의 포함",
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight(400),
                        fontFamily = paperlogyFamily,
                        color = colorTheme.gray[600]
                    )
                }



                /* ───── Divider (좌우 20) ───── */
                Divider(
                    color = colorTheme.blue[50],
                    modifier = Modifier
                        .padding(horizontal = (20.scaler), vertical = (16.scaler))
                )



                /* ───── 약관 항목들 (좌우 32, 간격 25) ───── */
                Column(
                    modifier = Modifier.padding(horizontal = (32.scaler)),
                    verticalArrangement = Arrangement.spacedBy((18.scaler))
                ) {
                    AgreementItem(
                        title = "이용약관",
                        suffix = "(필수)",
                        suffixColor = colorTheme.blue[200],
                        checked = agreeTerms,
                        onCheckedChange = onAgreeTermsChange,
                        onRowClick = onClickTerms
                    )

                    AgreementItem(
                        title = "개인정보 처리방침",
                        suffix = "(필수)",
                        suffixColor = colorTheme.blue[200],
                        checked = agreePrivacy,
                        onCheckedChange = onAgreePrivacyChange,
                        onRowClick = onClickPrivacy
                    )

                    AgreementItem(
                        title = "마케팅 수신 동의",
                        suffix = "(선택)",
                        suffixColor = colorTheme.gray[400],
                        checked = agreeMarketing,
                        onCheckedChange = onAgreeMarketingChange,
                        onRowClick = onClickMarketing
                    )
                }

                Spacer(Modifier.height((30.scaler)))

                /* ───── 다음 버튼 (좌우 31) ───── */
                GradientButtonCore(
                    text = "다음",
                    enabled = nextEnabled,
                    activeGradient = colorTheme.maincolor,
                    inactiveGradient = colorTheme.inactiveColor,
                    onClick = {
                        onNextClicked(agreeTerms, agreePrivacy, agreeMarketing)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 31.scaler)
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun TermsAgreementContentPreview() {
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeMarketing by remember { mutableStateOf(false) }

    TermsAgreementContent(
        agreeTerms = agreeTerms,
        agreePrivacy = agreePrivacy,
        agreeMarketing = agreeMarketing,
        onAgreeTermsChange = { agreeTerms = it },
        onAgreePrivacyChange = { agreePrivacy = it },
        onAgreeMarketingChange = { agreeMarketing = it },
        onClickTerms = {},
        onClickPrivacy = {},
        onClickMarketing = {},
        //onDismissRequest = {},
        onNextClicked = { _, _, _ -> }
    )
}
