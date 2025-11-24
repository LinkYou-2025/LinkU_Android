package com.example.login.auth

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.login.R
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import com.example.login.Paperlogy



@Composable
fun AgreementItem(
    title: String,
    suffix: String,
    suffixColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRowClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }   // 행 전체 클릭으로 약관 화면 이동
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
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = suffix,
            fontSize = 12.sp,
            fontFamily = Paperlogy,
            fontWeight = FontWeight.Normal,
            color = suffixColor
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFB7B9BF)
        )
    }
}
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
    onDismissRequest: () -> Unit = {},
    onNextClicked: (agreeTerms: Boolean, agreePrivacy: Boolean, agreeMarketing: Boolean) -> Unit
) {
    val agreeAll = agreeTerms && agreePrivacy && agreeMarketing
    val nextEnabled = agreeAll || (agreeTerms && agreePrivacy)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
        ) {
            Checkbox(
                checked = agreeAll,
                onCheckedChange = { checked ->
                    onAgreeTermsChange(checked)
                    onAgreePrivacyChange(checked)
                    onAgreeMarketingChange(checked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFFD35EFF),
                    uncheckedColor = Color(0xFFD7D9DF),
                    checkmarkColor = Color.White
                ),
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.width(20.dp))
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

        Divider(color = Color(0xFFE5E5E5), modifier = Modifier.padding(vertical = 6.dp))

        Column(verticalArrangement = Arrangement.spacedBy((-12).dp)) {
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

        Spacer(modifier = Modifier.height(12.dp))

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
                    shape = RoundedCornerShape(18)
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

        Spacer(modifier = Modifier.height(12.dp))
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

@Preview(showBackground = true, name = "약관 동의 - 기본(모두 해제)")
@Composable
fun TermsAgreementContentPreview_Default() {
    TermsAgreementContent(
        agreeTerms = false,
        agreePrivacy = false,
        agreeMarketing = false,
        onAgreeTermsChange = {},
        onAgreePrivacyChange = {},
        onAgreeMarketingChange = {},
        onClickTerms = {},
        onClickPrivacy = {},
        onClickMarketing = {},
        onNextClicked = { _, _, _ -> }
    )
}

@Preview(showBackground = true, name = "약관 동의 - 필수만 체크")
@Composable
fun TermsAgreementContentPreview_RequiredOnly() {
    TermsAgreementContent(
        agreeTerms = true,
        agreePrivacy = true,
        agreeMarketing = false,
        onAgreeTermsChange = {},
        onAgreePrivacyChange = {},
        onAgreeMarketingChange = {},
        onClickTerms = {},
        onClickPrivacy = {},
        onClickMarketing = {},
        onNextClicked = { _, _, _ -> }
    )
}

@Preview(showBackground = true, name = "약관 동의 - 전체 동의")
@Composable
fun TermsAgreementContentPreview_AllChecked() {
    TermsAgreementContent(
        agreeTerms = true,
        agreePrivacy = true,
        agreeMarketing = true,
        onAgreeTermsChange = {},
        onAgreePrivacyChange = {},
        onAgreeMarketingChange = {},
        onClickTerms = {},
        onClickPrivacy = {},
        onClickMarketing = {},
        onNextClicked = { _, _, _ -> }
    )
}

@Composable
fun NoAnimBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    scrimColor: Color = Color.Black.copy(alpha = 0.12f),
    shape: Shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // ✔ 스크림 (투명 금지)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismissRequest() }
        )

        // ✔ BottomSheet 본체 — 절대 화면을 밀지 않음
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()          // ✔ 내용만큼만
                //.navigationBarsPadding()      // ✔ 네비게이션 패딩
                .imePadding(),                // ✔ 키보드 대응
            shape = shape,
            color = containerColor,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)   // ✔ 자연스러운 핸들 패딩
            ) {
                content()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAgreementSheet(
    navController: NavController,
    vm: SignUpViewModel,
    visible: Boolean,
    onClose: () -> Unit,
    onClickTerms: () -> Unit,
    onClickPrivacy: () -> Unit,
    onClickMarketing: () -> Unit
) {
    if (!visible) return

    NoAnimBottomSheet(
        visible = visible,
        onDismissRequest = onClose,
        scrimColor = Color.Black.copy(alpha = 0.12f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {

        val agreeTerms by vm.agreeTerms.collectAsStateWithLifecycle()
        val agreePrivacy by vm.agreePrivacy.collectAsStateWithLifecycle()
        val agreeMarketing by vm.agreeMarketing.collectAsStateWithLifecycle()

        TermsAgreementContent(
            agreeTerms = agreeTerms,
            agreePrivacy = agreePrivacy,
            agreeMarketing = agreeMarketing,
            onAgreeTermsChange = vm::setAgreeTerms,
            onAgreePrivacyChange = vm::setAgreePrivacy,
            onAgreeMarketingChange = vm::setAgreeMarketing,
            onClickTerms = onClickTerms,
            onClickPrivacy = onClickPrivacy,
            onClickMarketing = onClickMarketing,
            onNextClicked = { t, p, _ ->
                if (t && p) {
                    onClose()
                    navController.navigate("email_verification") {
                        popUpTo("auth_graph")
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
