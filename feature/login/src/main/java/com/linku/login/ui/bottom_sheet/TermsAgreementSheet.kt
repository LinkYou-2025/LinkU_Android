package com.linku.login.ui.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.login.ui.content.TermsAgreementContent
import com.linku.login.viewmodel.SignUpViewModel

/**
 * 약관 동의 BottomSheet (애니메이션 없음)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAgreementSheet(
    navController: NavController,
    vm: SignUpViewModel,
    visible: Boolean,
    onClose: () -> Unit,
    onClickTerms: () -> Unit,
    onClickPrivacy: () -> Unit,
    onClickMarketing: () -> Unit,
    onNext: () -> Unit = {  // 기본값은 기존 동작인 이메일 인증 화면으로 유지
        navController.navigate("email_verification") {
            launchSingleTop = true
        }
    }
) {
    if (!visible) return

    val colorTheme = MaterialTheme.linkuColors

    //바텀시트 떠 있을 때, 백버튼 = 시트 닫기

    NoAnimBottomSheet(
        visible = visible,
        onDismissRequest = onClose,
        scrimColor = colorTheme.black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        // signUpForm에서 직접 약관 동의 상태 가져오기
        val agreeTerms = vm.signUpForm.agreeTerms
        val agreePrivacy = vm.signUpForm.agreePrivacy
        val agreeMarketing = vm.signUpForm.agreeMarketing

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
                    onNext()  // navigate 직접 호출 대신 콜백으로
//                    navController.navigate("email_verification") {
//                        launchSingleTop = true
//                    }
                }
            }
        )
    }
}

/* -----------------------------------------------------------------------
 * Preview
 * --------------------------------------------------------------------- */

@Preview(
    showBackground = true,
    name = "Terms Agreement BottomSheet - 실제 화면"
)
@Composable
fun TermsAgreementSheetPreview() {

    // 🔹 Preview용 상태
    var visible by remember { mutableStateOf(true) }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeMarketing by remember { mutableStateOf(false) }

    LinkuPreview {
        Box(modifier = Modifier.fillMaxSize()) {

            // 🔹 뒤에 깔린 실제 화면 느낌
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F6F9))
            )

            // 🔹 ViewModel 없이 Content만으로 프리뷰
            NoAnimBottomSheet(
                visible = visible,
                onDismissRequest = { visible = false },
                scrimColor = Color.Black.copy(alpha = 0.12f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
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
                    onNextClicked = { _, _, _ -> }
                )
            }
        }
    }
}
