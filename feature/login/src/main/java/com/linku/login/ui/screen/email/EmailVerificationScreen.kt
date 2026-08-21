package com.linku.login.ui.screen.email

import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.design.component.BottomGradientButton
import com.linku.design.modal.ModalWindow
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LinkuPreview
import com.linku.design.theme.linkuColors
import com.linku.design.theme.linkuFont
import com.linku.design.util.scaler
import com.linku.login.ui.item.LoginTextField
import com.linku.login.ui.item.StepIndicator
import com.linku.login.ui.item.WrongRuleItem
import com.linku.login.viewmodel.EmailAuthViewModel
import com.linku.login.viewmodel.SignUpViewModel
import com.linku.login.viewmodel.state.EmailUiEffect
import com.linku.login.viewmodel.state.EmailUiEvent
import com.linku.login.viewmodel.state.EmailUiState
import java.util.Locale


@Composable
internal fun EmailVerificationScreen(
    onBackClick: () -> Unit,
    onNavigateToPassword: () -> Unit,
    viewModel: EmailAuthViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel
) {
    BackHandler {
        onBackClick()
    }

    val context = LocalContext.current
    val emailUiState by viewModel.state.collectAsStateWithLifecycle()

    // 화면 진입 시 리셋
    LaunchedEffect(Unit) {
        viewModel.onEvent(EmailUiEvent.ClearStatus)
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is EmailUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is EmailUiEffect.NavigateToPassword -> {
                    signUpViewModel.updateForm { it.copy(email = effect.verifiedEmail) }
                    onNavigateToPassword()
                }
            }
        }
    }

    EmailVerificationScreenContent(
        emailUiState = emailUiState,
        timerProvider = { emailUiState.timer },
        onEmailEvent = { viewModel.onEvent(it) }
    )
}


@Composable
internal fun EmailVerificationScreenContent(
    emailUiState: EmailUiState,
    timerProvider: () -> Int,
    onEmailEvent: (EmailUiEvent) -> Unit
) {
    val emailValid = remember(emailUiState.email) {
        emailUiState.email.length >= 5 && emailUiState.email.contains("@") &&
                Patterns.EMAIL_ADDRESS.matcher(emailUiState.email).matches()
    }

    val isButtonEnabled = if (emailUiState.isCodeSent) {
        emailUiState.code.length == 6 && !emailUiState.isLoading
    } else {
        emailValid && !emailUiState.isLoading
    }

    val colorTheme = MaterialTheme.linkuColors

    var isEmailFocused by remember { mutableStateOf(false) }
    var isCodeFocused by remember { mutableStateOf(false) }
    var showCodeNotReceivedAlert by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.scaler, end = 20.scaler, top = 52.scaler, bottom = 72.scaler), //NOTE : 원래 top=60 이었는데 52로 조정함.
            horizontalAlignment = Alignment.Start
        ) {
            StepIndicator(currentStep = 1)
            // 입력 전엔 여유 있게(72), 필드에 커서가 활성화되면(포커스) 바로 좁힘(36)
            Spacer(modifier = Modifier.height(if (isEmailFocused || isCodeFocused) 20.scaler else 72.scaler)) //NOTE : 원래 32 -> 인데 20으로 조정함.
            Text(
                text = "가입을 위한 이메일 주소를\n인증해주세요",
                fontSize = 22.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorTheme.black
            )
            Spacer(modifier = Modifier.height(32.scaler)) // NOTE : 다현이랑 이야기 해서 간격 조정해주기.. 원래 40

            if (!emailUiState.isCodeSent) {
                // 1단계: 이메일 입력 화면
                LoginTextField(
                    value = emailUiState.email,
                    onValueChange = { onEmailEvent(EmailUiEvent.EmailChanged(it)) },
                    hint = "이메일 주소를 입력해주세요",
                    enabled = !emailUiState.isLoading,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth(),
                    onFocusChanged = { isEmailFocused = it },
                )

                emailUiState.emailError?.let {
                    Spacer(modifier = Modifier.height(6.scaler))
                    Text(
                        text = it,
                        color = colorTheme.negative,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.offset(x = 4.scaler)
                    )
                }
            } else {
                // 2단계: 코드 입력 화면
                Box(modifier = Modifier.fillMaxWidth()) {
                    LoginTextField(
                        value = emailUiState.email,
                        onValueChange = {},
                        hint = "이메일 주소를 입력해주세요",
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // 인증번호 오류/만료 시, 이메일 필드 위에 재요청 버튼 노출 (인증 코드 재발송)
                    if (emailUiState.codeError != null) {
                        ResendCodeButton(
                            isTimeExpired = emailUiState.isCodeExpired,
                            enabled = !emailUiState.isLoading,
                            onClick = { onEmailEvent(EmailUiEvent.SendCodeClicked) },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 9.scaler)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.scaler))

                val codeInteractionSource = remember { MutableInteractionSource() }
                // Material3 OutlinedTextField 대신 BasicTextField + DecorationBox를 직접 씀:
                // (1) enabled=false일 때 자동으로 회색 비활성 컬러가 적용되는 걸 피하기 위해 항상
                //     enabled=true로 두고 로딩 중엔 readOnly로만 입력을 막음(색은 안 바뀜).
                // (2) contentPadding을 이메일 필드(LoginTextField)와 동일한 22.scaler로 맞춰서
                //     두 필드의 글자 시작 위치를 일치시킴(기존엔 Material3 기본 패딩이라 어긋나 있었음).
                BasicTextField(
                    value = emailUiState.code,
                    onValueChange = { onEmailEvent(EmailUiEvent.CodeChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorTheme.white, shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            brush = colorTheme.maincolor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .onFocusChanged { isCodeFocused = it.hasFocus },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = MaterialTheme.linkuFont.font,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight(500),
                        color = colorTheme.black
                    ),
                    singleLine = true,
                    enabled = true,
                    readOnly = emailUiState.isLoading,
                    cursorBrush = SolidColor(colorTheme.black),
                    // 키보드 타입을 명시하지 않으면 IME 자동완성/맞춤법 검사 밑줄이 표시됨.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        autoCorrectEnabled = false
                    ),
                    interactionSource = codeInteractionSource,
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = emailUiState.code,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = codeInteractionSource,
                            placeholder = {
                                Text("코드를 입력해주세요", fontSize = 14.sp, color = colorTheme.gray[400])
                            },
                            trailingIcon = { TimerText(timerProvider = timerProvider) },
                            contentPadding = PaddingValues(
                                start = 22.scaler,
                                end = 21.scaler,
                                top = 18.scaler,
                                bottom = 18.scaler
                            ),
                            // 테두리는 위 .border()로 직접 그리므로 Material3 기본 컨테이너는 생략함
                            // (기본 컨테이너는 브러시가 아닌 단색만 지원해서 그라데이션 테두리를 못 그림).
                            container = {}
                        )
                    }
                )

                emailUiState.codeError?.let {
                    Spacer(modifier = Modifier.height(12.scaler))
                    WrongRuleItem(
                        text = it,
                        modifier = Modifier.padding(start = 22.scaler)
                    )
                }
            }
        }

        // 하단 영역
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "인증번호가 오지 않는다면?",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = colorTheme.gray[400],
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(bottom = 21.scaler)
                    .noRippleClickable {
                        if (!emailUiState.isLoading) {
                            showCodeNotReceivedAlert = true
                        }
                    }
            )

            BottomGradientButton(
                text = if (emailUiState.isCodeSent) "인증하기" else "인증메일 발송",
                enabled = isButtonEnabled,
                onClick = {
                    if (emailUiState.isCodeSent) {
                        onEmailEvent(EmailUiEvent.VerifyCodeClicked)
                    } else {
                        onEmailEvent(EmailUiEvent.SendCodeClicked)
                    }
                }
            )
        }

        // 인증번호가 오지 않을 때 안내하는 alert
        ModalWindow(
            visible = showCodeNotReceivedAlert,
            onDismiss = { showCodeNotReceivedAlert = false },
            positiveText = "확인",
            title = "인증번호가 오지 않았나요?"
        ) {
            Text(
                text = buildAnnotatedString {
                    append("스팸 메일함 또는 프로모션 메일함도 확인해 보세요.\n메일이 도착하기까지 ")
                    withStyle(SpanStyle(color = colorTheme.gray[800])) {
                        append("최대 3분")
                    }
                    append(" 정도 소요될 수 있어요.")
                },
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = colorTheme.gray[600],
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailVerificationScreen_TimerPreview() {
    LinkuPreview {
        EmailVerificationScreenContent(
            emailUiState = EmailUiState(
                email = "test@email.com",
                code = "123456",
                isCodeSent = true,
                timer = 180
            ),
            timerProvider = { 180 },
            onEmailEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "인증번호 오류")
@Composable
fun EmailVerificationScreen_CodeErrorPreview() {
    LinkuPreview {
        EmailVerificationScreenContent(
            emailUiState = EmailUiState(
                email = "linkU2025@gmail.com",
                code = "230800",
                isCodeSent = true,
                timer = 179,
                codeError = "인증번호가 올바르지 않습니다! 다시 시도해주세요."
            ),
            timerProvider = { 179 },
            onEmailEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "인증 시간 만료")
@Composable
fun EmailVerificationScreen_TimeExpiredPreview() {
    LinkuPreview {
        EmailVerificationScreenContent(
            emailUiState = EmailUiState(
                email = "linkU2025@gmail.com",
                code = "230800",
                isCodeSent = true,
                timer = 0,
                codeError = "인증 시간이 만료되었어요. 인증번호를 다시 요청해주세요.",
                isCodeExpired = true
            ),
            timerProvider = { 0 },
            onEmailEvent = {}
        )
    }
}

@Composable
private fun TimerText(timerProvider: () -> Int) {
    val seconds = timerProvider()
    val timerText = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60)
    Text(
        text = timerText,
        color = MaterialTheme.linkuColors.negative,
        fontSize = 13.sp,
        modifier = Modifier.padding(end = 22.dp)
    )
}

/**
 * 인증번호 오류/만료 시 노출되는 재요청 버튼.
 *
 * @param isTimeExpired true면 "재요청"(시간 만료), false면 "다시요청"(코드 불일치) 문구를 표시.
 * @param enabled 로딩 중 등 중복 클릭을 막아야 할 때 false.
 * @param onClick 클릭 시 실행할 콜백 (인증 코드 재발송 이벤트를 트리거).
 */
@Composable
private fun ResendCodeButton(
    isTimeExpired: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorTheme = MaterialTheme.linkuColors
    Text(
        text = if (isTimeExpired) "재요청" else "다시요청",
        fontSize = 13.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp,
        color = colorTheme.gray[600],
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorTheme.gray[200])
            .noRippleClickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.scaler, vertical = 9.scaler)
    )
}
