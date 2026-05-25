package com.linku.login.ui.model

data class TermsAgreementEvent(
    val onClose: () -> Unit = {},
    val onAgreeTermsChange: (Boolean) -> Unit = {},
    val onAgreePrivacyChange: (Boolean) -> Unit = {},
    val onAgreeMarketingChange: (Boolean) -> Unit = {},
    val onClickTerms: () -> Unit = {},
    val onClickPrivacy: () -> Unit = {},
    val onClickMarketing: () -> Unit = {},
    val onNext: () -> Unit = {},
)