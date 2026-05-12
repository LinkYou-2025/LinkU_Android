package com.linku.login.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.R
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.ui.item.AgreementItem
import com.linku.login.ui.item.GradientButtonCore
import com.linku.login.ui.model.TermsAgreementEvent
import com.linku.login.ui.model.TermsAgreementState


@Composable
internal fun TermsAgreementContent(
    state: TermsAgreementState,
    event: TermsAgreementEvent,
) {
    val colorTheme = MaterialTheme.linkuColors

    val agreeAll = state.agreeTerms && state.agreePrivacy && state.agreeMarketing
    val nextEnabled = state.agreeTerms && state.agreePrivacy

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (32.scaler),
                        end = (32.scaler),
                        top = (36.scaler)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .size(22.scaler)
                        .border(
                            width = 1.dp,
                            color = if (agreeAll) colorTheme.purple[200] else colorTheme.gray[300],
                            shape = RoundedCornerShape(6.scaler)
                        )
                        .background(
                            color = if (agreeAll) colorTheme.purple[200] else colorTheme.white,
                            shape = RoundedCornerShape(6.scaler)
                        )
                        .clickable {
                            val checked = !agreeAll
                            event.onAgreeTermsChange(checked)
                            event.onAgreePrivacyChange(checked)
                            event.onAgreeMarketingChange(checked)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (agreeAll) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_checkbox_checked),
                            contentDescription = null,
                            modifier = Modifier
                                .width(11.scaler)
                                .height(8.scaler)
                        )
                    }
                }

                Spacer(Modifier.width(15.scaler))

                Text(
                    text = "약관 전체동의",
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorTheme.black
                )

                Spacer(Modifier.width(8.scaler))

                Text(
                    text = "선택항목에 대한 동의 포함",
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight(400),
                    color = colorTheme.gray[600]
                )
            }

            HorizontalDivider(
                color = colorTheme.blue[50],
                modifier = Modifier
                    .padding(horizontal = (20.scaler), vertical = (16.scaler))
            )


            Column(
                modifier = Modifier.padding(horizontal = (32.scaler)),
                verticalArrangement = Arrangement.spacedBy((18.scaler))
            ) {
                AgreementItem(
                    title = "이용약관",
                    suffix = "(필수)",
                    suffixColor = colorTheme.blue[200],
                    checked = state.agreeTerms,
                    onCheckedChange = event.onAgreeTermsChange,
                    onRowClick = event.onClickTerms
                )

                AgreementItem(
                    title = "개인정보 처리방침",
                    suffix = "(필수)",
                    suffixColor = colorTheme.blue[200],
                    checked = state.agreePrivacy,
                    onCheckedChange = event.onAgreePrivacyChange,
                    onRowClick = event.onClickPrivacy
                )

                AgreementItem(
                    title = "마케팅 수신 동의",
                    suffix = "(선택)",
                    suffixColor = colorTheme.gray[400],
                    checked = state.agreeMarketing,
                    onCheckedChange = event.onAgreeMarketingChange,
                    onRowClick = event.onClickMarketing
                )
            }


            Spacer(Modifier.height((30.scaler)))


            GradientButtonCore(
                text = "다음",
                enabled = nextEnabled,
                activeGradient = colorTheme.maincolor,
                inactiveGradient = colorTheme.inactiveColor,
                onClick = event.onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 31.scaler)
            )
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
        state = TermsAgreementState(
            agreeTerms = agreeTerms,
            agreePrivacy = agreePrivacy,
            agreeMarketing = agreeMarketing,
        ),
        event = TermsAgreementEvent(
            onClose = { },
            onAgreeTermsChange = { agreeTerms = it },
            onAgreePrivacyChange = { agreePrivacy = it },
            onAgreeMarketingChange = { agreeMarketing = it },
            onClickTerms = { },
            onClickPrivacy = { },
            onClickMarketing = { },
            onNext = { },
        ),
    )
}
