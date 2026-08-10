package com.linku.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.UserInfo
import com.linku.core.model.auth.Gender
import com.linku.core.model.auth.Job
import com.linku.core.model.auth.LoginType
import com.linku.core.model.auth.NicknameCheckState
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalFontTheme
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.R

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onPickProfileImage: () -> Unit,
    onChangeProfileImage: () -> Unit,
    onNicknameInputChanged: (String) -> Unit,
    nicknameCheckState: NicknameCheckState,
    onSubmit: (nickname: String, jobId: Long) -> Unit,
    userInfo: UserInfo,
    userSocialLoginType: LoginType,
) {
    val colors = MaterialTheme.linkuColors
    val userNickname = userInfo.nickname
    val userJobId = userInfo.jobId

    var isProfileImageChanged by remember { mutableStateOf(false) }

    var name by remember(userNickname) { mutableStateOf(userNickname) }
    // 성별은 서버에서 받은 값을 표시만 하고 이 화면에서는 변경하지 않음(변경 불가).
    // userInfo.gender는 서버 원본 값("MALE"/"FEMALE")이라 Gender enum으로 변환해서 비교해야 함.
    val gender = remember(userInfo.gender) { Gender.fromApiValue(userInfo.gender) }

    val jobOptions = remember { Job.getAllJobs() }
    var selectedJob by remember(userJobId) {
        mutableStateOf(Job.fromId(userJobId.toInt()).takeIf { it != Job.NONE }
            ?: jobOptions.first())
    }

    val socialLoginGuideText = when (userSocialLoginType) {
        LoginType.KAKAO -> "카카오로 가입된 계정이예요."
        LoginType.GOOGLE -> "구글로 가입된 계정이예요."
        LoginType.EMAIL -> "이메일로 가입된 계정이예요."
        LoginType.NONE -> null
    }

    val showSocialLoginGuide = !socialLoginGuideText.isNullOrBlank()

    val trimmedName = name.trim()
    val isNicknameChanged = trimmedName != userNickname

    LaunchedEffect(trimmedName) {
        onNicknameInputChanged(trimmedName)
    }

    val isNicknameAvailable =
        !isNicknameChanged || nicknameCheckState is NicknameCheckState.Available

    val isJobChanged = selectedJob.id.toLong() != userJobId

    val isSubmitEnabled =
        isProfileImageChanged ||
                (isNicknameChanged && isNicknameAvailable) ||
                isJobChanged

    val nicknameWarningText = if (isNicknameChanged && trimmedName.isNotBlank()) {
        when (val checkState = nicknameCheckState) {
            is NicknameCheckState.Duplicated -> "이미 존재하는 닉네임이예요."
            is NicknameCheckState.Error -> checkState.message
            else -> null
        }
    } else {
        null
    }

    fun handleSubmit() {
        if (isNicknameChanged && (trimmedName.isBlank() || !isNicknameAvailable)) return

        onSubmit(trimmedName, selectedJob.id.toLong())

        if (isProfileImageChanged) {
            onChangeProfileImage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.white)
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
                    .noRippleClickable { onBackClick() }
            )

            Text(
                text = "내 정보 수정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = LocalFontTheme.current.font,
                color = colors.black,
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
                        .border(4.dp, colors.white, shape = CircleShape)
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
                    color = colors.gray[600],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(13.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.dp,
                            color = colors.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(colors.white)
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
                                color = colors.black
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
                                    if (name.isNotEmpty()) Modifier.noRippleClickable { name = "" }
                                    else Modifier
                                )
                        )
                    }
                }

                if (nicknameWarningText != null) {
                    Spacer(modifier = Modifier.height(6.5.dp))

                    Text(
                        text = nicknameWarningText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.negative,
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
                    color = colors.gray[600],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(13.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.dp,
                            color = colors.gray[200],
                            shape = RoundedCornerShape(18.dp)
                        )
                        .background(colors.gray[100])
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userInfo.email,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[600]
                    )
                }

                if (showSocialLoginGuide) {
                    Spacer(modifier = Modifier.height(7.5.dp))

                    Text(
                        text = socialLoginGuideText!!,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.gray[500],
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
                    color = colors.gray[600],
                )

                Spacer(modifier = Modifier.height(12.5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = if (gender == Gender.MALE) 5.dp else 1.dp,
                                    color = colors.blue[200],
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "남성",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[800]
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = if (gender == Gender.FEMALE) 5.dp else 1.dp,
                                    color = colors.blue[200],
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "여성",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.gray[800]
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
                    color = colors.gray[600],
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(13.dp))

                JobDropdownMenu(
                    options = jobOptions.map { it.displayName },
                    selectedOption = selectedJob.displayName,
                    onOptionSelected = { name ->
                        selectedJob = jobOptions.first { it.displayName == name }
                    }
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
                            .background(colors.maincolor)
                            .noRippleClickable { handleSubmit() }
                    } else {
                        Modifier.background(colors.gray[300])
                    }
                )
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "완료",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.white
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
    val colors = MaterialTheme.linkuColors

    var expanded by remember { mutableStateOf(false) }

    Box {
        JobDropdownField(
            selectedOption = selectedOption,
            onClick = { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = colors.white,
            shadowElevation = 10.dp,
            modifier = Modifier
                .width(372.dp)
                .heightIn(max = menuMaxHeight)
                .border(width = 1.dp, color = colors.gray[200], shape = RoundedCornerShape(18.dp))
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
    val colors = MaterialTheme.linkuColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .border(width = 1.dp, color = colors.gray[200], shape = RoundedCornerShape(18.dp))
            .noRippleClickable { onClick() }
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
                color = colors.black
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
    val colors = MaterialTheme.linkuColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                if (selected) {
                    R.drawable.ic_checkbox_checked
                } else {
                    R.drawable.ic_checkbox_empty
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
            color = colors.black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    ThemeProvider {
        EditProfileScreen(
            onBackClick = { },
            onPickProfileImage = { },
            onChangeProfileImage = { },
            onNicknameInputChanged = { },
            nicknameCheckState = NicknameCheckState.Idle,
            onSubmit = { _, _ -> },
            userInfo = UserInfo(
                nickname = "세나",
                email = "longtime03@naver.com",
                gender = "FEMALE",
                jobId = Job.COLLEGE.id.toLong(),
                jobName = Job.COLLEGE.displayName,
                myLinku = 0L,
                myFolder = 0L,
                myAiLinku = 0L
            ),
            userSocialLoginType = LoginType.EMAIL
        )
    }
}