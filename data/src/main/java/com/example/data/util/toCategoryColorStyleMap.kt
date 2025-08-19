package com.example.data.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.core.model.CategoryColorList
import com.example.design.theme.color.CategoryColorStyle

// 카테고리 색상 리스트를 Map으로 변환하는 확장 함수. 카테고리명을 키로 사용.
fun List<CategoryColorList>.toCategoryColorStyleMap(): Map<String, CategoryColorStyle> {
    return this.associate { categoryColorList ->
        categoryColorList.categoryName to CategoryColorStyle(
            color1 = Color(categoryColorList.colorCode4.toColorInt()),
            color2 = Color(categoryColorList.colorCode3.toColorInt()),
            color3 = Color(categoryColorList.colorCode2.toColorInt()),
            color4 = Color(categoryColorList.colorCode1.toColorInt())
        )
    }
}