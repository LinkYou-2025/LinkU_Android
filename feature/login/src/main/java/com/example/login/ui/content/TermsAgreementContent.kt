package com.example.login.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.Paperlogy
import com.example.login.ui.item.AgreementItem
import com.example.login.ui.item.GradientButtonCore

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
    val agreeAll = agreeTerms && agreePrivacy && agreeMarketing
    val nextEnabled = agreeTerms && agreePrivacy

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        //  바텀시트 실제 높이 기준
        val topPadding = maxHeight * (46f / 280f)

        Box(modifier = Modifier.fillMaxWidth()) {

            Column {

                /* ───── 전체동의 Row (좌우 32, 위 46/280) ───── */
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 32.dp,
                            end = 32.dp,
                            top = 42.dp
                            //top = topPadding 화면으로 계산이 되어서 일단 보류.
                        )
                ) {
                    // 커스텀 체크박스
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(
                                width = 1.dp,
                                color = if (agreeAll) Color(0xFFD35EFF) else Color(0xFFD7D9DF),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .background(
                                color = if (agreeAll) Color(0xFFD35EFF) else Color.White,
                                shape = RoundedCornerShape(6.dp)
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
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = "약관 전체동의",
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF000208)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "선택항목에 대한 동의 포함",
                        fontSize = 12.sp,
                        fontFamily = Paperlogy,
                        color = Color(0xFF87898F)
                    )
                }



                /* ───── Divider (좌우 20) ───── */
                Divider(
                    color = Color(0xFFE5E5E5),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                )



                /* ───── 약관 항목들 (좌우 32, 간격 25) ───── */
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    AgreementItem(
                        title = "이용약관",
                        suffix = "(필수)",
                        suffixColor = Color(0xFF2C6FFF),
                        checked = agreeTerms,
                        onCheckedChange = onAgreeTermsChange,
                        onRowClick = onClickTerms
                    )

                    AgreementItem(
                        title = "개인정보 처리방침",
                        suffix = "(필수)",
                        suffixColor = Color(0xFF2C6FFF),
                        checked = agreePrivacy,
                        onCheckedChange = onAgreePrivacyChange,
                        onRowClick = onClickPrivacy
                    )

                    AgreementItem(
                        title = "마케팅 수신 동의",
                        suffix = "(선택)",
                        suffixColor = Color(0xFFB7B9BF),
                        checked = agreeMarketing,
                        onCheckedChange = onAgreeMarketingChange,
                        onRowClick = onClickMarketing
                    )
                }

                Spacer(Modifier.height(30.dp))

                /* ───── 다음 버튼 (좌우 31) ───── */
                GradientButtonCore(
                    text = "다음",
                    enabled = nextEnabled,
                    activeGradient = listOf(
                        Color(0xFF4D5FFF),
                        Color(0xFFA032F5)
                    ),
                    inactiveGradient = listOf(
                        Color(0xFFE1D6F9),
                        Color(0xFFF3E7FB)
                    ),
                    onClick = {
                        onNextClicked(agreeTerms, agreePrivacy, agreeMarketing)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 31.dp)
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
