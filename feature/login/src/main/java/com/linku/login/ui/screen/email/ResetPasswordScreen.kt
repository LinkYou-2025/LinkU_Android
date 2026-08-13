package com.linku.login.ui.screen.email

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.component.BottomGradientButton
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.util.LocalStatusBarDarkIcons
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

    // 딤(로딩/성공 알럿)이 뜨는 동안엔 상태바 아이콘이 검정색 그대로라 딤 배경(검정 50%) 위에서
    // 거의 안 보임 — 상태바도 edge-to-edge라 딤이 그 아래까지 비치기 때문(EdgeToEdgeSystemBars 참고).
    // 딤이 떠 있는 동안만 흰 아이콘으로 전환하고, 화면을 벗어나면 기본값(검정)으로 되돌림.
    val statusBarDarkIcons = LocalStatusBarDarkIcons.current
    val isDimmed = ui.isLoading || ui.showSuccessDialog
    DisposableEffect(isDimmed) {
        statusBarDarkIcons.value = !isDimmed
        onDispose { statusBarDarkIcons.value = true }
    }

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

            // 로고 위 여백 (48 / 917)
            Spacer(modifier = Modifier.height((48.scaler)))

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

            // 제목-설명 간격 (18 / 917)
            Spacer(modifier = Modifier.height((18.scaler)))

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
        // Box.fillMaxSize()는 Activity 콘텐츠 영역 안에서만 그려져서 OS 3버튼 내비게이션 바
        // 영역까지는 안 비침(회색으로 뚝 끊겨 보임). Dialog는 별도의 시스템 창(Window)에 그려지므로
        // 하단 내비게이션 바까지 포함해 화면 전체를 딤 처리할 수 있음 — 로그아웃/탈퇴 다이얼로그와 동일 패턴.
        if (ui.isLoading) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
            ) {
                val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
                // 다이얼로그 기본 딤을 끄고, 아래 커스텀 딤(0.5f)만 적용.
                SideEffect { dialogWindow?.setDimAmount(0f) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorTheme.black.copy(alpha = 0.5f))
                        .noRippleClickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorTheme.white)
                    // TODO : 로딩 중 색상 변경 필요. 이거 디자이너가 정해주겠는데? 커스텀 하면 현우 오빠 마이페이꺼 가져다 써야지 히히
                }
            }
        }

        // api 요청 성공 시 딤처리 alert 창 띄움
        if (ui.showSuccessDialog) {
            Dialog(
                onDismissRequest = { /* 여기는 뒤로 가기가 작동하면 안되는게 맞다고 생각해서 비워둠. 뒤로가기 하면 로직이 엉망될 것 같은데 */ },
                properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
            ) {
                val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
                SideEffect { dialogWindow?.setDimAmount(0f) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorTheme.black.copy(alpha = 0.5f))
                        .noRippleClickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    PasswordResetAlert(
                        onDismissRequest = {},
                        onConfirmClick = {
                            onNavigateToEmailLogin()
                        }
                    )
                }
            }
        }

        BottomGradientButton(
            text = "메일 보내기",
            enabled = ui.isEmailValid && !ui.isLoading,
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


