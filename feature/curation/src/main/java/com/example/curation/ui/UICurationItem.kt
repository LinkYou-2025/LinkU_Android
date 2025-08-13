package com.example.curation.ui


data class UICurationItem(
    val title: String,
    val date: String,
    val imageRes: Int? = null,     // 로컬 드로어블(옵션)
    val imageUrl: String? = null,  // 네트워크 이미지(옵션)
    val liked: Boolean
)