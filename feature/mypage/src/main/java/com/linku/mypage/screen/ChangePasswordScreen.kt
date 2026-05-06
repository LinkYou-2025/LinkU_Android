package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.mypage.R

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userEmail: String,
    onClickFinish: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val hasValidLength = password.length in 8..20
    val hasValidCombination =
        password.any { it in 'a'..'z' || it in 'A'..'Z' } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }

    val isPasswordMatched = confirmPassword.isNotEmpty() && password == confirmPassword
    val isPasswordMismatched = confirmPassword.isNotEmpty() && password != confirmPassword
    val isChangePasswordEnabled = hasValidLength && hasValidCombination && isPasswordMatched

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColorTheme.current.white)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 59.dp, start = 20.dp, end = 20.dp)
                .height(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(11.dp)
                    .noRippleClickable { navController.popBackStack() }
            )

            Text(
                text = "비밀번호 변경",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.height(38.25.dp))

        // 이메일 (변경 X)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "이메일",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalColorTheme.current.gray[600],
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border( width = 1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.gray[100])
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userEmail,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.gray[600]
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 비밀번호 변경
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "새 비밀번호",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalColorTheme.current.gray[600],
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.5.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border( width = 1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(18.dp))
                    .background(LocalColorTheme.current.white)
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (password.isEmpty()) {
                            Text(
                                text = "새로운 비밀번호를 입력해주세요",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.gray[500]
                            )
                        }

                        BasicTextField(
                            value = password,
                            onValueChange = { password = it },
                            singleLine = true,
                            textStyle = if (isPasswordVisible) {
                                TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = LocalColorTheme.current.black
                                )
                            } else {
                                TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = LocalColorTheme.current.black
                                )
                            },
                            visualTransformation = if (isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation(mask = '●')
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible) R.drawable.ic_seek_pw
                            else R.drawable.ic_hide_pw
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(18.dp)
                            .noRippleClickable { isPasswordVisible = !isPasswordVisible }
                    )
                }
            }

            // 비밀번호 조건 체크
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            if (hasValidCombination) R.drawable.ic_checkbox_checked
                            else R.drawable.ic_checkbox_unchecked
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "영문, 숫자, 특수기호 조합",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.gray[600]
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            if (hasValidLength) R.drawable.ic_checkbox_checked
                            else R.drawable.ic_checkbox_unchecked
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "8~20자",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.gray[600]
                    )
                }
            }
            
            // 비밀번호 확인
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = LocalColorTheme.current.gray[200],
                        shape = RoundedCornerShape(18.dp)
                    )
                    .background(LocalColorTheme.current.white)
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (confirmPassword.isEmpty()) {
                            Text(
                                text = "비밀번호를 확인해주세요",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.gray[500]
                            )
                        }

                        BasicTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            singleLine = true,
                            textStyle = if (isConfirmPasswordVisible) {
                                TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = LocalColorTheme.current.black
                                )
                            } else {
                                TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = LocalColorTheme.current.black
                                )
                            },
                            visualTransformation = if (isConfirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation(mask = '●')
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Icon(
                        painter = painterResource(
                            if (isConfirmPasswordVisible) R.drawable.ic_seek_pw
                            else R.drawable.ic_hide_pw
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(18.dp)
                            .noRippleClickable { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                    )
                }
            }

            // 비밀번호 확인 문구
            Spacer(modifier = Modifier.height(10.dp))

            if (isPasswordMatched || isPasswordMismatched) {
                Row(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            if (isPasswordMatched) R.drawable.ic_correct_pw
                            else R.drawable.ic_incorrect_pw
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isPasswordMatched) {
                            "비밀번호가 일치합니다."
                        } else {
                            "비밀번호가 일치하지 않습니다! 다시 입력해주세요."
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isPasswordMatched) {
                            LocalColorTheme.current.positive
                        } else {
                            LocalColorTheme.current.negative
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 완료 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .then(
                    if (isChangePasswordEnabled) {
                        Modifier.background(
                            brush = LocalColorTheme.current.maincolor,
                            shape = RoundedCornerShape(18.dp)
                        )
                    } else {
                        Modifier.background(
                            color = LocalColorTheme.current.gray[300],
                            shape = RoundedCornerShape(18.dp)
                        )
                    }
                )
                .noRippleClickable(enabled = isChangePasswordEnabled) {
                    onClickFinish(password)
                }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "완료",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColorTheme.current.white
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChangePasswordScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        ChangePasswordScreen(
            navController = navController,
            userEmail = "longtime03@naver.com",
            onClickFinish = { }
        )
    }
}