package com.linku.login.ui.screen.social

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Job
import com.linku.design.util.scaler
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SocialAuthViewModel

@Composable
fun SocialJobScreen(
    navigator: NavHostController,
    viewModel: SocialAuthViewModel
) {

    // SocialAuthViewModel 상태
    val selectedJob by viewModel.job.collectAsStateWithLifecycle()
    val jobs = Job.getAllJobs()

    val isButtonEnabled = selectedJob != Job.NONE

    SignUpStepLayout(
        currentStep = 3,
        totalSteps = 3,
        label = "프로필 설정",
        title = "현재 하고 계신 일이나\n활동을 알려주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            navigator.navigate("social_purpose") { launchSingleTop = true }
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        jobs.forEach { job ->
            OptionButton(
                text = job.displayName,
                selected = selectedJob == job,
                onClick = { viewModel.updateJob(job) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.scaler))
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "소셜 직업 선택 - 프리뷰")
@Composable
fun SocialJobScreenPreview() {
    var selectedJob by remember { mutableStateOf(Job.NONE) }
    val jobs = Job.getAllJobs()

    SignUpStepLayoutPreview(
        currentStep = 3,
        totalSteps = 3,
        label = "프로필 설정",
        title = "현재 하고 계신 일이나\n활동을 알려주세요",
        buttonEnabled = selectedJob != Job.NONE,
        onNextClick = {}
    ) {
        Spacer(Modifier.height(4.scaler))

        jobs.forEach { job ->
            OptionButton(
                text = job.displayName,
                selected = selectedJob == job,
                onClick = { selectedJob = job },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.scaler))
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
