package com.linku.login.ui.screen.email

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.linku.core.model.auth.Job
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SignUpViewModel

@Composable
fun SignUpJobScreen(
    navigator: NavHostController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {

    // 뷰모델 상태 확인.
    val selectedJobId = signUpViewModel.signUpForm.jobId
    val jobs = Job.getAllJobs()
    val isButtonEnabled = selectedJobId > 0

    SignUpStepLayout(
        currentStep = 2,
        totalSteps = 3,
        label = "프로필 설정",
        title = "현재 하고 계신 일이나\n활동을 알려주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            navigator.navigate("sign_up_purpose") { launchSingleTop = true }
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        jobs.forEach { job ->
            OptionButton(
                text = job.displayName,
                selected = selectedJobId == job.id,
                onClick = { signUpViewModel.onJobSelected(job.id) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.scaler))
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "직업 선택 - 프리뷰")
@Composable
fun SignUpJobScreenPreview() {
    var selectedJobId by remember { mutableStateOf(3) }
    val jobs = Job.getAllJobs()

    LinkuPreview {
        SignUpStepLayoutPreview(
            currentStep = 2,
            totalSteps = 3,
            label = "프로필 설정",
            title = "현재 하고 계신 일이나\n활동을 알려주세요",
            buttonEnabled = selectedJobId > 0,
            onNextClick = {}
        ) {
            Spacer(Modifier.height(4.scaler))

            jobs.forEach { job ->
                OptionButton(
                    text = job.displayName,
                    selected = selectedJobId == job.id,
                    onClick = { selectedJobId = job.id },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.scaler))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}