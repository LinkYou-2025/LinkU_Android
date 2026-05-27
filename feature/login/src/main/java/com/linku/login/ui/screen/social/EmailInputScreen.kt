package com.linku.login.ui.screen.social

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.font.Paperlogy
import com.linku.design.util.scaler
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel

/**
 * 소셜 로그인 후 이메일 입력 화면
 * - OTP 인증 없이 이메일 형식만 검증
 * - 형식이 맞으면 다음 단계로 진행
 * 사용하지 않는 ui이지만, 혹시나 이메일 받아야 하는 순간(?)이 있을까봐 그대로 둠.
 */
@Composable
internal fun EmailInputScreen(
    navigator: NavHostController,
    parentEntry: NavBackStackEntry,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    BackHandler {
        parentEntry.savedStateHandle["from_email_input"] = true
        navigator.popBackStack()
    }

    var email by remember { mutableStateOf("") }
    val trimmed = email.trim()
    val emailValid = remember(trimmed) {
        trimmed.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }

    SignUpStepLayout(
        currentStep = 1,
        title = "이메일 주소를 입력해주세요",
        buttonEnabled = emailValid,
        onNextClick = {
            signUpViewModel.updateForm { it.copy(email = email.trim()) }
            navigator.navigate("sign_up_password")
        }
    ) {
        // title 아래 기존 12 → layout 32 이미 있으니 subTitle 바로 배치
        Text(
            text = "계정 복구 및 알림 수신에 사용됩니다",
            fontSize = 14.sp,
            fontFamily = Paperlogy.font,
            color = LocalColorTheme.current.gray[500]
        )

        Spacer(Modifier.height(20.scaler)) // 12 + 32(layout) 차이 보정 → subTitle 후 32 확보

        LoginTextField(
            value = email,
            onValueChange = { email = it },
            hint = "이메일 주소를 입력해주세요",
            modifier = Modifier.fillMaxWidth()
        )

        if (email.isNotBlank() && !emailValid) {
            Spacer(Modifier.height(10.scaler))
            WrongRuleItem(
                text = "이메일 양식이 올바르지 않습니다!",
                modifier = Modifier.padding(start = 12.scaler)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "이메일 입력 - 빈 상태")
@Composable
fun EmailInputScreenPreview_Empty() {
    SignUpStepLayoutPreview(
        currentStep = 1,
        title = "이메일 주소를 입력해주세요",
        buttonEnabled = false
    ) {
        Text(
            text = "계정 복구 및 알림 수신에 사용됩니다",
            fontSize = 14.sp,
            fontFamily = Paperlogy.font,
            color = LocalColorTheme.current.gray[500]
        )
        Spacer(Modifier.height(20.scaler))
        LoginTextField(
            value = "",
            onValueChange = {},
            hint = "이메일 주소를 입력해주세요",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "이메일 입력 - 오류")
@Composable
fun EmailInputScreenPreview_Invalid() {
    SignUpStepLayoutPreview(
        currentStep = 1,
        title = "이메일 주소를 입력해주세요",
        buttonEnabled = false
    ) {
        Text(
            text = "계정 복구 및 알림 수신에 사용됩니다",
            fontSize = 14.sp,
            fontFamily = Paperlogy.font,
            color = LocalColorTheme.current.gray[500]
        )
        Spacer(Modifier.height(20.scaler))
        LoginTextField(
            value = "linku",
            onValueChange = {},
            hint = "이메일 주소를 입력해주세요",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.scaler))
        WrongRuleItem(
            text = "이메일 양식이 올바르지 않습니다!",
            modifier = Modifier.padding(start = 12.scaler)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "이메일 입력 - 유효")
@Composable
fun EmailInputScreenPreview_Valid() {
    SignUpStepLayoutPreview(
        currentStep = 1,
        title = "이메일 주소를 입력해주세요",
        buttonEnabled = true
    ) {
        Text(
            text = "계정 복구 및 알림 수신에 사용됩니다",
            fontSize = 14.sp,
            fontFamily = Paperlogy.font,
            color = LocalColorTheme.current.gray[500]
        )
        Spacer(Modifier.height(20.scaler))
        LoginTextField(
            value = "test@example.com",
            onValueChange = {},
            hint = "이메일 주소를 입력해주세요",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}