package com.linku.login.ui.screen.social

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linku.core.model.auth.Job
import com.linku.design.theme.LinkuPreview
import com.linku.design.util.scaler
import com.linku.login.ui.item.OptionButton
import com.linku.login.ui.layout.SignUpStepLayout
import com.linku.login.ui.layout.SignUpStepLayoutPreview
import com.linku.login.viewmodel.SocialAuthViewModel
import com.linku.login.viewmodel.state.SocialAuthUiEffect

@Composable
fun SocialJobScreen(
    onBackClick: () -> Unit,
    onNavigateToPurpose: () -> Unit,
    viewModel: SocialAuthViewModel
) {
    BackHandler { onBackClick() }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val selectedJob = uiState.socialLoginForm.job
    val jobs = Job.getAllJobs()

    val isButtonEnabled = selectedJob != Job.NONE

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SocialAuthUiEffect.NavigateToAdditionalInfo -> {
                    onNavigateToPurpose()
                }

                else -> { /* 딱히 할게 없네용 */
                }
            }
        }
    }

    SignUpStepLayout(
        currentStep = 3,
        title = "현재 하고 계신 일이나\n활동을 알려주세요",
        buttonEnabled = isButtonEnabled,
        onNextClick = {
            onNavigateToPurpose()
        }
    ) {
        Spacer(Modifier.height(4.scaler)) // layout 32 + 4 = 기존 36과 동일

        jobs.forEach { job ->
            OptionButton(
                text = job.displayName,
                selected = selectedJob == job,
                onClick = { viewModel.onJobChanged(job) },
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

    LinkuPreview {
        SignUpStepLayoutPreview(
            currentStep = 3,
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
}
