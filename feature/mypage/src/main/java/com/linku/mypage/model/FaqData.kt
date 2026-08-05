package com.linku.mypage.model

import com.linku.core.model.Faq

internal val faqList = listOf(
    Faq(
        id = 1,
        question = "폴더에 있는 별 표시(⭐)는 무엇인가요?",
        answer = "즐겨찾기한 폴더를 의미하며, 해당 폴더는 목록 상단에 우선 정렬됩니다.",
        category = "폴더",
    ),
    Faq(
        id = 2,
        question = "링크는 어떻게 저장하나요?",
        answer = "하단의 ‘+ 버튼’을 눌러 링크를 추가할 수 있습니다.",
        category = "링크",
    ),
    Faq(
        id = 3,
        question = "링크에 있는 깃발 표시는 무엇인가요?",
        answer = "AI 요약 기능이 적용된 링크를 구분하기 위한 표시입니다.",
        category = "링크",
    ),
    Faq(
        id = 4,
        question = "저장한 링크는 어디에 보관되나요?",
        answer = "링크는 AI가 자동으로 분류한 카테고리 폴더에 저장됩니다.",
        category = "링크",
    ),
    Faq(
        id = 5,
        question = "카테고리 폴더를 직접 추가할 수 있나요?",
        answer = "현재는 기본으로 제공되는 16개의 카테고리만 사용할 수 있으며, 추후 사용자 추가 기능을 지원할 예정입니다.",
        category = "카테고리",
    ),
    Faq(
        id = 6,
        question = "링크 요약은 자동으로 생성되나요?",
        answer = "아닙니다. 링크 상세 화면에서 AI 버튼을 눌러야 요약이 생성됩니다. (일부 링크는 지원되지 않을 수 있습니다)",
        category = "링크",
    ),
    Faq(
        id = 7,
        question = "저장한 링크를 다시 추천받을 수 있나요?",
        answer = "네, 사용자가 선택한 감정과 상황에 따라 홈 화면에서 다시 추천됩니다.",
        category = "링크",
    ),
    Faq(
        id = 8,
        question = "공유받은 폴더는 어떻게 사용하나요?",
        answer = "공유받은 폴더에서 링크를 확인할 수 있으며, 편집 권한이 있는 경우 새로운 링크를 추가할 수 있습니다.",
        category = "폴더",
    ),
    Faq(
        id = 9,
        question = "링크가 저장되지 않아요",
        answer = "네트워크 상태를 확인해 주세요. 또한 일부 영상 링크 등은 지원되지 않을 수 있습니다.",
        category = "링크",
    ),
    Faq(
        id = 10,
        question = "계정을 삭제하면 데이터는 어떻게 되나요?",
        answer = "탈퇴 후 14일이 지나면 모든 데이터가 삭제됩니다.\n\n단, 14일 이내에 다시 로그인하면 계정을 복구할 수 있습니다.",
        category = "기타",
    ),
    Faq(
        id = 11,
        question = "문의는 어디로 하면 되나요?",
        answer = "아래 이메일로 문의해 주세요.\n\nlinku.cs@gmail.com",
        category = "기타",
    ),
)
