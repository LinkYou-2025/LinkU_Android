//package com.example.login.auth
//
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.ExitTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import com.example.login.R
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.platform.LocalConfiguration
//import com.example.login.Paperlogy
//import com.example.login.ui.bottom_sheet.NoAnimBottomSheet
//import com.example.login.ui.content.TermsAgreementContent
//import com.example.login.ui.item.AgreementItem
//
//
//
//
//@Preview(showBackground = true, name = "약관 동의 - 기본(모두 해제)")
//@Composable
//fun TermsAgreementContentPreview_Default() {
//    TermsAgreementContent(
//        agreeTerms = false,
//        agreePrivacy = false,
//        agreeMarketing = false,
//        onAgreeTermsChange = {},
//        onAgreePrivacyChange = {},
//        onAgreeMarketingChange = {},
//        onClickTerms = {},
//        onClickPrivacy = {},
//        onClickMarketing = {},
//        onNextClicked = { _, _, _ -> }
//    )
//}
//
//@Preview(showBackground = true, name = "약관 동의 - 필수만 체크")
//@Composable
//fun TermsAgreementContentPreview_RequiredOnly() {
//    TermsAgreementContent(
//        agreeTerms = true,
//        agreePrivacy = true,
//        agreeMarketing = false,
//        onAgreeTermsChange = {},
//        onAgreePrivacyChange = {},
//        onAgreeMarketingChange = {},
//        onClickTerms = {},
//        onClickPrivacy = {},
//        onClickMarketing = {},
//        onNextClicked = { _, _, _ -> }
//    )
//}
//
//@Preview(showBackground = true, name = "약관 동의 - 전체 동의")
//@Composable
//fun TermsAgreementContentPreview_AllChecked() {
//    TermsAgreementContent(
//        agreeTerms = true,
//        agreePrivacy = true,
//        agreeMarketing = true,
//        onAgreeTermsChange = {},
//        onAgreePrivacyChange = {},
//        onAgreeMarketingChange = {},
//        onClickTerms = {},
//        onClickPrivacy = {},
//        onClickMarketing = {},
//        onNextClicked = { _, _, _ -> }
//    )
//}
//
//
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TermsAgreementSheet(
//    navController: NavController,
//    vm: SignUpViewModel,
//    visible: Boolean,
//    onClose: () -> Unit,
//    onClickTerms: () -> Unit,
//    onClickPrivacy: () -> Unit,
//    onClickMarketing: () -> Unit
//) {
//    if (!visible) return
//
//
//    NoAnimBottomSheet(
//        visible = visible,
//        onDismissRequest = onClose,
//        scrimColor = Color.Black.copy(alpha = 0.12f),
//        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//    ) {
//
//        val agreeTerms by vm.agreeTerms.collectAsStateWithLifecycle()
//        val agreePrivacy by vm.agreePrivacy.collectAsStateWithLifecycle()
//        val agreeMarketing by vm.agreeMarketing.collectAsStateWithLifecycle()
//
//        TermsAgreementContent(
//            agreeTerms = agreeTerms,
//            agreePrivacy = agreePrivacy,
//            agreeMarketing = agreeMarketing,
//            onAgreeTermsChange = vm::setAgreeTerms,
//            onAgreePrivacyChange = vm::setAgreePrivacy,
//            onAgreeMarketingChange = vm::setAgreeMarketing,
//            onClickTerms = onClickTerms,
//            onClickPrivacy = onClickPrivacy,
//            onClickMarketing = onClickMarketing,
//            onNextClicked = { t, p, _ ->
//                if (t && p) {
//                    onClose()
//                    navController.navigate("email_verification") {
//                        launchSingleTop = true
//                    }
//                }
//            }
//        )
//    }
//}
//
//@Preview(
//    showBackground = true,
//    name = "Terms Agreement BottomSheet - 실제 화면",
//
//)
//@Composable
//fun TermsAgreementBottomSheetPreview() {
//
//    // 🔹 Preview용 상태
//    var visible by remember { mutableStateOf(true) }
//    var agreeTerms by remember { mutableStateOf(false) }
//    var agreePrivacy by remember { mutableStateOf(false) }
//    var agreeMarketing by remember { mutableStateOf(false) }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // 🔹 실제 화면 배경 흉내 (뒤에 있는 화면)
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF5F6F9))
//        )
//
//        // 🔹 실제 쓰는 BottomSheet 그대로
//        NoAnimBottomSheet(
//            visible = visible,
//            onDismissRequest = { visible = false },
//            scrimColor = Color.Black.copy(alpha = 0.12f),
//            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//        ) {
//            TermsAgreementContent(
//                agreeTerms = agreeTerms,
//                agreePrivacy = agreePrivacy,
//                agreeMarketing = agreeMarketing,
//                onAgreeTermsChange = { agreeTerms = it },
//                onAgreePrivacyChange = { agreePrivacy = it },
//                onAgreeMarketingChange = { agreeMarketing = it },
//                onClickTerms = {},
//                onClickPrivacy = {},
//                onClickMarketing = {},
//                onNextClicked = { _, _, _ -> }
//            )
//        }
//    }
//}
