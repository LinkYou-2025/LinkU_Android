package com.linku.login.ui.screen.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.component.BottomGradientButton
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.scaler
import com.linku.login.R
import com.linku.login.ui.alert.PasswordResetAlert
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.ResetPasswordTopHeader
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.viewmodel.ResetPasswordViewModel
import com.linku.login.viewmodel.state.ResetPasswordEffect

@Composable
fun ResetPasswordScreen(
    onNavigateToEmailLogin: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    BackHandler { onNavigateToEmailLogin() }

    val colorTheme = MaterialTheme.linkuColors
    val ui by viewModel.state.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is ResetPasswordEffect.NavigateToEmailLogin -> onNavigateToEmailLogin()
                is ResetPasswordEffect.ShowError -> { /* TODO: 토스트 - 아직 미정 */
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.white)
    ) {
        ResetPasswordTopHeader(
            onBack = { onNavigateToEmailLogin() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (20.scaler)),
            horizontalAlignment = Alignment.Start
        ) {

            // 헤더 밀기
            Spacer(modifier = Modifier.height((59.scaler)))

            // 로고 위 여백 (38 / 917)
            Spacer(modifier = Modifier.height((38.scaler)))

            Image(
                painter = painterResource(id = R.drawable.ic_logo_color),
                contentDescription = null,
                modifier = Modifier
                    .width((56.scaler))
                    .height((40.scaler))
                    .offset(x = (8).scaler),
                contentScale = ContentScale.Fit

            )

            // 로고-제목 간격 (18 / 917)
            Spacer(modifier = Modifier.height((18.scaler)))

            Text(
                text = "비밀번호 재설정",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black,
                modifier = Modifier.offset(x = 4.scaler),
            )

            // 제목-설명 간격 (22 / 917)
            Spacer(modifier = Modifier.height((22.scaler)))

            Text(
                text = "링큐에 가입했던 이메일을 입력해주세요. \n비밀번호를 다시 설정할 수 있는 메일을 보내드릴게요.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(400),
                color = colorTheme.gray[600],
                modifier = Modifier.offset(x = 4.scaler),
            )

            Spacer(modifier = Modifier.height((30.scaler)))

            LoginTextField(
                value = ui.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                hint = "이메일 주소를 입력해주세요",
            )

            ui.error?.let { error ->
                Spacer(Modifier.height(8.scaler))
                WrongRuleItem(
                    text = error,
                    modifier = Modifier.offset(x = 12.scaler),
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // 로딩 딤처리
        if (ui.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorTheme.black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorTheme.white)
                // TODO : 로딩 중 색상 변경 필요. 이거 디자이너가 정해주겠는데? 커스텀 하면 현우 오빠 마이페이꺼 가져다 써야지 히히
            }
        }

        // api 요청 성공 시 딤처리 alert 창 띄움
        if (ui.showSuccessDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorTheme.black.copy(alpha = 0.5f))
                    .clickable(enabled = true, onClick = {})
            )

            PasswordResetAlert(
                onDismissRequest = { /* 여기는 뒤로 가기가 작동하면 안되는게 맞다고 생각해서 비워둠. 뒤로가기 하면 로직이 엉망될 것 같은데 */ },
                onConfirmClick = {
                    onNavigateToEmailLogin()
                }
            )
        }

        BottomGradientButton(
            text = "메일 보내기",
            enabled = ui.isEmailValid && !ui.isLoading,
            activeGradient = colorTheme.maincolor,
            inactiveGradient = colorTheme.inactiveColor,
            onClick = {
                if (!ui.showSuccessDialog) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    viewModel.onSendEmailClicked()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Preview(showBackground = true, name = "ResetPassword UI Preview")
@Composable
private fun ResetPasswordScreenPreview() {
    LinkuPreview {
        ResetPasswordScreen(
            onNavigateToEmailLogin = {}
        )
    }
}


