package com.linku.core.usecase

import com.linku.core.preference.NotificationPreference
import com.linku.core.repository.AlarmRepository
import com.linku.core.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val alarmRepository: AlarmRepository,
    private val notificationPreference: NotificationPreference
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {

    }
}
