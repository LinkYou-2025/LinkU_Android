package com.example.login.auth
//여기는 링큐 로그인 하는 스크린입니다.
//추후, 디자인이 나오면 쇼셜 로그인 구현이 필요합니다.

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.R
import com.example.login.Paperlogy
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController



@Composable
fun LoginScreen(
    navigator: NavHostController,
    logoOffsetY: Float = 0f,
    contentAlpha: Float = 1f,
    emailButtonColor: Color = Color(0x66FFFFFF),
    onSignUpClick: () -> Unit = {} // 회원가입 클릭 시 호출되는 콜백 함수
) {

    val isPreview = LocalInspectionMode.current

    val parentEntry = remember(navigator.currentBackStackEntry) {
        if (!isPreview) {
            navigator.getBackStackEntry("auth_graph")
        } else {
            null
        }
    }


// EmailVerificationScreen → 뒤로가기 시 약관 시트를 자동으로 다시 열기
    if (!isPreview && parentEntry != null) {
    val fromEmail by parentEntry.savedStateHandle
        .getStateFlow("from_email_verification", false)
        .collectAsState()

    LaunchedEffect(fromEmail) {
        if (fromEmail) {
            parentEntry.savedStateHandle["show_terms_sheet"] = true
            parentEntry.savedStateHandle["from_email_verification"] = false
        }
    }

// 약관 체크 상태 감지 → LoginScreen 재조합 강제
    val signUpVm: SignUpViewModel = hiltViewModel(parentEntry)

    val agreeTerms by signUpVm.agreeTerms.collectAsState()
    val agreePrivacy by signUpVm.agreePrivacy.collectAsState()
    val agreeMarketing by signUpVm.agreeMarketing.collectAsState()

    LaunchedEffect(agreeTerms, agreePrivacy, agreeMarketing) {
        parentEntry.savedStateHandle["trigger_terms_rerender"] =
            System.currentTimeMillis()
    }

    val trigger by parentEntry.savedStateHandle
        .getStateFlow("trigger_terms_rerender", 0L)
        .collectAsState()
    }

    LaunchedEffect(Unit) {
        println(" LoginScreen Loaded")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFC800FF), Color(0xFF2C6FFF)),
                    start = Offset(0f, 0f),           // 좌상단
                    end = Offset.Infinite             // 우하단 (대각선)
                )
            )
    ){
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            //  BoxWithConstraintsScope 사용
            val screenHeight = maxHeight

            // 상단 로고 비율
            val logoTopRatio = 245f / 917f
            val logoTopOffset = screenHeight * logoTopRatio

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {


                //  로고 위 여백 (비율 기반)
                Spacer(modifier = Modifier.height(logoTopOffset))

                // 로고 (피그마 크기 그대로)
                Image(
                    painter = painterResource(id = R.drawable.img_login_logo),
                    contentDescription = "LinkU Logo",
                    modifier = Modifier
                        .padding(3.dp)
                        .width(150.dp)   // 149.49561 → 실사용 150
                        .height(106.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(90.dp))

                //  이메일 로그인 버튼 //TODO : 여기서부터 이이서 로그인 ui 수정하기!
                Surface(
                    color = emailButtonColor,
                    shape = RoundedCornerShape(32),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navigator.navigate("email_login")
                        }
                ) {
                    Row(
                       modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,

                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_email_png),
                            contentDescription = null,
                            modifier = Modifier
                                .width(20.dp)
                                .height(16.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "이메일로 로그인",
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            color = Color.White,
                            fontFamily = Paperlogy,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 비밀번호 찾기| 회원가입
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 76.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "비밀번호 찾기",
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontFamily = Paperlogy,
                        fontWeight = FontWeight(400),
                        modifier = Modifier.clickable {
                            navigator.navigate("resetPassword")
                        }
                    )

                    Spacer(modifier = Modifier.width(24.dp))
                    Text("   |   ", color = Color.White)

                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        "회원가입",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = Paperlogy,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))

                // SNS 구분선
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(Modifier.weight(1f).height(1.dp).background(Color.White.copy(0.4f)))
                    Text("  SNS 로그인  ", color = Color.White, fontSize = 12.sp)
                    Box(Modifier.weight(1f).height(1.dp).background(Color.White.copy(0.4f)))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SNS 버튼
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_kakao),
                        contentDescription = null,
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(Modifier.width(24.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_naver),
                        contentDescription = null,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val dummyNavController = rememberNavController()
    LoginScreen(navigator = dummyNavController, logoOffsetY = 0f)
}

