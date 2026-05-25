package com.linku.login.ui.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.linku.design.theme.LinkuPreview
import com.linku.login.ui.content.TermsAgreementContent
import com.linku.login.ui.model.TermsAgreementEvent
import com.linku.login.ui.model.TermsAgreementState


/**
 * 약관 동의 BottomSheet (애니메이션 없음)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAgreementSheet(
    state: TermsAgreementState,
    event: TermsAgreementEvent,
    visible: Boolean,
) {
    if (!visible) return

    NoAnimBottomSheet(
        onDismissRequest = event.onClose
    ) {
        TermsAgreementContent(
            state = state,
            event = event,
        )
    }
}

@Preview
@Composable
fun TermsAgreementSheetPreview() {
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeMarketing by remember { mutableStateOf(false) }

    LinkuPreview {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F6F9))
            )

            NoAnimBottomSheet(onDismissRequest = { }) {
                TermsAgreementContent(
                    state = TermsAgreementState(
                        agreeTerms = agreeTerms,
                        agreePrivacy = agreePrivacy,
                        agreeMarketing = agreeMarketing,
                    ),
                    event = TermsAgreementEvent(
                        onAgreeTermsChange = { agreeTerms = it },
                        onAgreePrivacyChange = { agreePrivacy = it },
                        onAgreeMarketingChange = { agreeMarketing = it }
                    ),
                )
            }
        }
    }
}