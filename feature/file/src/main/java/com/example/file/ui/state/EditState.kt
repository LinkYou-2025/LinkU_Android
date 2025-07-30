package com.example.file.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class EditState : ViewModel(){
    var isEditMode by mutableStateOf(false)
        private set

    fun updateEditMode(value: Boolean) {
        isEditMode = value
    }
}