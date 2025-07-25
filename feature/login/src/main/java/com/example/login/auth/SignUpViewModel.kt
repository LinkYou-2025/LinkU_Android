package com.example.login.auth


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var nickname by mutableStateOf("")
    var gender by mutableStateOf(0)
    var jobId by mutableStateOf(0)

    var purposeList by mutableStateOf<List<String>>(emptyList())
    var interestList by mutableStateOf<List<String>>(emptyList())
}