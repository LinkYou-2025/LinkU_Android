package com.example.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.design.modifier.noRippleClickable
import com.example.design.theme.LocalColorTheme
import com.example.design.theme.LocalFontTheme
import com.example.design.theme.ThemeProvider
import com.example.design.theme.color.Basic
import com.example.mypage.R

@Composable
fun EditProfileScreen(
    navController: NavController,
    onPickProfileImage: () -> Unit,
    onChangeProfileImage: () -> Unit,
    onChangeNickname: (String) -> Unit,
    onChangeGender: (String) -> Unit,
    onChangeJob: (String) -> Unit,
    userNickname: String,
    userJob: String,
    userEmail: String,
    userGender: String,
    userSocialLoginType: String,
) {
    var isProfileImageChanged by remember { mutableStateOf(false) }

    var name by remember(userNickname) { mutableStateOf(userNickname) }
    var selectedGender by remember(userGender) { mutableStateOf(userGender) }

    val jobOptions = listOf("고등학생", "대학생", "직장인", "자영업자", "프리랜서", "취준생")
    var selectedJob by remember(userJob) {
        mutableStateOf(
            userJob.takeIf { it.isNotBlank() } ?: jobOptions.first()
        )
    }

    val socialLoginGuideText = when (userSocialLoginType) {
        "KAKAO" -> "카카오로 가입한 계정이에요."
        "GOOGLE" -> "구글로 가입한 계정이에요."
        "NAVER" -> "네이버로 가입한 계정이에요."
        else -> null
    }

    val showSocialLoginGuide = !socialLoginGuideText.isNullOrBlank()

    val trimmedName = name.trim()
    val isSubmitEnabled =
        isProfileImageChanged ||
                trimmedName != userNickname ||
                selectedGender != userGender ||
                selectedJob != userJob

    fun isNicknameDuplicated(nickname: String): Boolean {
        // TODO: 추후 닉네임 중복 확인 API 연결
        return false
    }

    val showNicknameWarning =
        trimmedName.isNotBlank() &&
                trimmedName != userNickname &&
                isNicknameDuplicated(trimmedName)


    fun handleSubmit() {
        val nicknameChanged = trimmedName != userNickname
        val genderChanged = selectedGender != userGender
        val jobChanged = selectedJob != userJob
        val profileImageChanged = isProfileImageChanged

        if (nicknameChanged) {
            if (trimmedName.isBlank()) return

            val isDuplicated = isNicknameDuplicated(trimmedName)
            if (isDuplicated) return

            onChangeNickname(trimmedName)
        }

        if (genderChanged) {
            onChangeGender(selectedGender)
        }

        if (jobChanged) {
            onChangeJob(selectedJob)
        }

        if (profileImageChanged) {
            onChangeProfileImage()
        }
    }

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
                    .width(10.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "내 정보 수정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = LocalColorTheme.current.black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(37.75.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 프로필 사진
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(R.drawable.img_profile_default),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(4.dp, LocalColorTheme.current.white, shape = CircleShape)
                        .align(Alignment.BottomEnd)
                        .noRippleClickable {
                            onPickProfileImage()
                            isProfileImageChanged = true
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(37.dp))

            // 이름 변경
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "닉네임",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.gray[600],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(13.dp))

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
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = LocalColorTheme.current.black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp)
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_delete_gray),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(18.dp)
                                .then(
                                    if (name.isNotEmpty()) Modifier.clickable { name = "" }
                                    else Modifier
                                )
                        )
                    }
                }

                if (showNicknameWarning) {
                    Spacer(modifier = Modifier.height(6.5.dp))

                    Text(
                        text = "이미 존재하는 닉네임이에요.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.negative,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(13.dp))
                } else {
                    Spacer(modifier = Modifier.height(17.5.dp))
                }
            }

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

                Spacer(modifier = Modifier.height(13.dp))

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

                if (showSocialLoginGuide) {
                    Spacer(modifier = Modifier.height(7.5.dp))

                    Text(
                        text = socialLoginGuideText!!,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = LocalColorTheme.current.gray[500],
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(25.dp))
                } else {
                    Spacer(modifier = Modifier.height(27.5.dp))
                }
            }

            // 성별
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "성별",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.gray[600],
                )

                Spacer(modifier = Modifier.height(12.5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.noRippleClickable { selectedGender = "남성" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = if (selectedGender == "남성") 5.dp else 1.dp,
                                    color = LocalColorTheme.current.blue[200],
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "남성",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalColorTheme.current.gray[800]
                        )
                    }

                    Row(
                        modifier = Modifier.noRippleClickable { selectedGender = "여성" },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = if (selectedGender == "여성") 5.dp else 1.dp,
                                    color = LocalColorTheme.current.blue[200],
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "여성",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = LocalColorTheme.current.gray[800]
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(33.5.dp))

            // 하고 있는 일이나 활동
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "하고 있는 일 · 활동",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = LocalColorTheme.current.gray[600],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(13.dp))

                JobDropdownMenu(
                    options = jobOptions,
                    selectedOption = selectedJob,
                    onOptionSelected = { selectedJob = it }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 25.dp)
                .clip(RoundedCornerShape(18.dp))
                .then(
                    if (isSubmitEnabled) {
                        Modifier
                            .background(LocalColorTheme.current.maincolor)
                            .noRippleClickable { handleSubmit() }
                    } else {
                        Modifier.background(LocalColorTheme.current.gray[300])
                    }
                )
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

@Composable
fun JobDropdownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    menuMaxHeight: Dp = 262.dp
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        JobDropdownField(
            selectedOption = selectedOption,
            onClick = { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(372.dp)
                .heightIn(max = menuMaxHeight)
                .border(width = 1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(18.dp))
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color(0xFF7C7C7C).copy(alpha = 0.25f),
                    spotColor = Color(0xFF7C7C7C).copy(alpha = 0.25f)
                )
                .background(LocalColorTheme.current.white, RoundedCornerShape(18.dp))
                .padding(horizontal = 22.dp, vertical = 11.dp),
            offset = DpOffset(x = 0.dp, y = (-302).dp)
        ) {
            options.forEach { option ->
                JobDropdownItem(
                    text = option,
                    selected = option == selectedOption,
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun JobDropdownField(
    selectedOption: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(LocalColorTheme.current.white)
            .border(width = 1.dp, color = LocalColorTheme.current.gray[200], shape = RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = LocalColorTheme.current.black
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_down_gray500),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun JobDropdownItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                if (selected) {
                    R.drawable.ic_checkbox_checked
                } else {
                    R.drawable.ic_checkbox_unchecked
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = LocalColorTheme.current.black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    val navController = rememberNavController()

    ThemeProvider {
        EditProfileScreen(
            navController = navController,
            onPickProfileImage = { },
            onChangeProfileImage = { },
            onChangeNickname = { },
            onChangeGender = { },
            onChangeJob = { },
            userNickname = "세나",
            userJob = "대학생",
            userEmail = "longtime03@naver.com",
            userGender = "여성",
            userSocialLoginType = "NAVER"
        )
    }
}