package com.example.data.preference

interface AuthPreference {
    var accessToken: String?
    var refreshToken: String?
    var userId: Long?
}