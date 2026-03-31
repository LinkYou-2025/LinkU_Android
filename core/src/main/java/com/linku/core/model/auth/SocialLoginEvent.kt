package com.linku.core.model.auth

sealed class SocialLoginEvent {
    data class NavigateToSocialEntry(
        val socialToken: String,
        val provider: String
    ) : SocialLoginEvent()
}