package com.linku.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.repository.UserRepository
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.e
import com.linku.mypage.intent.MarketingAgreeIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketingAgreeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    // UI 이벤트(Intent) 입력 큐
    private val intentChannel = Channel<MarketingAgreeIntent>(Channel.BUFFERED)

    private val _state = MutableStateFlow(MarketingAgreeState())
    val state = _state.asStateFlow()

    // 1회성 UI 이벤트 전달용 채널
    private val _sideEffect = Channel<MarketingAgreeSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        processIntents()
    }

    fun sendIntent(intent: MarketingAgreeIntent) {
        intentChannel.trySend(intent)
    }

    private fun processIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow()
                .collect { handleIntent(it) }
        }
    }

    private suspend fun handleIntent(intent: MarketingAgreeIntent) = when (intent) {
        is MarketingAgreeIntent.InitialLoad -> loadMarketingStatus()
        is MarketingAgreeIntent.ToggleMarketingAgree -> toggleMarketingAgree()
    }

    private suspend fun loadMarketingStatus() {
        _state.update { it.copy(isLoading = true) }

        userRepository.checkMarketingTermsAgreed().fold(
            onSuccess = { isAgreed ->
                _state.update {
                    it.copy(isLoading = false, isLoaded = true, isAgreed = isAgreed)
                }
            },
            onFailure = { throwable ->
                _state.update { it.copy(isLoading = false, isLoaded = false) }
                LinkuLog.e("MarketingAgreeViewModel", "마케팅 동의 상태 조회 실패", throwable)
                _sideEffect.send(
                    MarketingAgreeSideEffect.ShowToast("마케팅 동의 상태를 불러오지 못했어요.")
                )
            }
        )
    }

    private suspend fun toggleMarketingAgree() {
        if (!_state.value.isLoaded) {
            loadMarketingStatus()
            if (!_state.value.isLoaded) return
        }

        // 낙관적 업데이트 후 실패 시 롤백
        val previous = _state.value.isAgreed
        val toggled = !previous

        _state.update { it.copy(isAgreed = toggled) }

        userRepository.updateMarketingTerms().fold(
            onSuccess = {
                val message = if (toggled) "마케팅 동의가 선택되었습니다." else "마케팅 동의가 해지되었습니다."
                _sideEffect.send(MarketingAgreeSideEffect.ShowToast(message))
            },
            onFailure = { throwable ->
                _state.update { it.copy(isAgreed = previous) }
                LinkuLog.e("MarketingAgreeViewModel", "마케팅 동의 업데이트 실패", throwable)
                _sideEffect.send(
                    MarketingAgreeSideEffect.ShowToast("마케팅 동의 처리에 실패했어요. 잠시 후에 다시 시도해주세요.")
                )
            }
        )
    }
}

data class MarketingAgreeState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isAgreed: Boolean = false,
)

sealed interface MarketingAgreeSideEffect {
    data class ShowToast(val message: String) : MarketingAgreeSideEffect
}
