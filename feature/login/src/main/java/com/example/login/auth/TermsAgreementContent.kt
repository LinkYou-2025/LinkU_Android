package com.example.login.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.login.R
import com.example.login.Paperlogy

@Composable
fun TermsAgreementScreen(navController: NavController) {
    var agreeAll by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeMarketing by remember { mutableStateOf(false) }

    val nextEnabled = agreeAll || (agreeTerms && agreePrivacy)

    LaunchedEffect(agreeAll) {
        if (agreeAll) {
            agreeTerms = true
            agreePrivacy = true
            agreeMarketing = true
        } else {
            agreeTerms = false
            agreePrivacy = false
            agreeMarketing = false
        }
    }

    TermsAgreementContent(
        agreeTerms = agreeTerms,
        agreePrivacy = agreePrivacy,
        agreeMarketing = agreeMarketing,
        onAgreeTermsChange = {
            agreeTerms = it
            agreeAll = agreeTerms && agreePrivacy && agreeMarketing
        },
        onAgreePrivacyChange = {
            agreePrivacy = it
            agreeAll = agreeTerms && agreePrivacy && agreeMarketing
        },
        onAgreeMarketingChange = {
            agreeMarketing = it
            agreeAll = agreeTerms && agreePrivacy && agreeMarketing
        },
        onNextClicked = { terms, privacy, marketing ->
            if (terms && privacy) {
                navController.navigate("terms/service")
            }
        }
    )
}

@Composable
fun TermsAgreementContent(
    agreeTerms: Boolean,
    agreePrivacy: Boolean,
    agreeMarketing: Boolean,
    onAgreeTermsChange: (Boolean) -> Unit,
    onAgreePrivacyChange: (Boolean) -> Unit,
    onAgreeMarketingChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit = {},
    onNextClicked: (agreeTerms: Boolean, agreePrivacy: Boolean, agreeMarketing: Boolean) -> Unit
) {
    val agreeAll = agreeTerms && agreePrivacy && agreeMarketing
    val nextEnabled = agreeAll || (agreeTerms && agreePrivacy)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreeAll,
                onCheckedChange = { checked ->
                    onAgreeTermsChange(checked)
                    onAgreePrivacyChange(checked)
                    onAgreeMarketingChange(checked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFD35EFF),
                    checkmarkColor = Color.White
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "약관 전체동의",
                fontSize = 16.sp,
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C1C1E)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "선택항목에 대한 동의 포함",
                fontFamily = Paperlogy,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Color(0xFF87898F)
            )
        }

        Divider(
            color = Color(0xFFE5E5E5),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy((-12).dp)
        ) {
            AgreementItem(
                title = "이용약관",
                suffix = "(필수)",
                suffixColor = Color(0xFF2C6FFF),
                checked = agreeTerms,
                onCheckedChange = onAgreeTermsChange
            )
            AgreementItem(
                title = "개인정보 처리방침",
                suffix = "(필수)",
                suffixColor = Color(0xFF2C6FFF),
                checked = agreePrivacy,
                onCheckedChange = onAgreePrivacyChange
            )
            AgreementItem(
                title = "마케팅 수신 동의",
                suffix = "(선택)",
                suffixColor = Color(0xFFB7B9BF),
                checked = agreeMarketing,
                onCheckedChange = onAgreeMarketingChange
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (nextEnabled)
                            listOf(Color(0xFF4D5FFF), Color(0xFFA032F5))
                        else
                            listOf(Color(0xFFE1D6F9), Color(0xFFF3E7FB))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable(enabled = nextEnabled) {
                    onNextClicked(agreeTerms, agreePrivacy, agreeMarketing)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AgreementItem(
    title: String,
    suffix: String,
    suffixColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFD35EFF),
                uncheckedColor = Color(0xFFDADADA),
                checkmarkColor = Color.White
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = suffix,
            fontSize = 12.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Normal,
            color = suffixColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TermsAgreementContentPreview() {
    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        TermsAgreementContent(
            agreeTerms = true,
            agreePrivacy = true,
            agreeMarketing = false,
            onAgreeTermsChange = {},
            onAgreePrivacyChange = {},
            onAgreeMarketingChange = {},
            onNextClicked = { _, _, _ -> }
        )
    }
}