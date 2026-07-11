package com.linku.home.viewmodel

import androidx.lifecycle.ViewModel
import com.linku.core.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repository: AlarmRepository
) : ViewModel() {


}