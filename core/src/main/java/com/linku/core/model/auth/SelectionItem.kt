package com.linku.core.model.auth

interface SelectionItem {
    val displayName: String
    val serverKey: String
    // drawable 파일명 규칙: ic_purpose_{name} 또는 ic_interest_{name}
    // getIdentifier로 0 반환 시 회색 placeholder 표시 -> 디자이너 이슈로 일단 부득이하게 이렇게 처리하는데 나중에 빼야함.
    val iconName: String
}