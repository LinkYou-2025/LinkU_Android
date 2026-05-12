package com.linku

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linku.core.datastore.session.LoginSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.linku.core.repository.RecentSearchRepository
import com.linku.data.preference.NotificationPreference
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recentRepository: RecentSearchRepository,
    val loginSessionStore: LoginSessionStore,
    private val notificationPreference: NotificationPreference
): ViewModel() {

    // 최근 검색 기록 전체 삭제
    // 앱 실행 시 실행하여 이전 계정 기록 삭제
    fun clearRecentQuery() {
        Log.d("MainViewModel", "clearRecentQuery")

        viewModelScope.launch {
            Log.d("MainViewModel", "clearRecentQuery launch")

            try{
                Log.d("MainViewModel", "clearRecentQuery try")

                recentRepository.clear()

            }catch (e: Exception){
                Log.d("MainViewModel", "clearRecentQuery catch: $e.message")
            }finally {
                Log.d("MainViewModel", "clearRecentQuery finally")
            }
        }
        Log.d("MainViewModel", "clearRecentQuery return")
    }

    // 알림 허용 여부 저장
    // 로그인 성공 후 시스템 권한 요청 결과를 로컬에 반영
    fun setNotificationEnabled(enabled: Boolean) {
        notificationPreference.setNotificationEnabled(enabled)
    }

}