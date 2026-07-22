package com.linku.data.mapper

import com.linku.core.model.CategoryColorList
import com.linku.data.api.dto.folder.CategoryColorListResponseDTO

fun CategoryColorListResponseDTO.toDomain(): CategoryColorList =
    CategoryColorList(
        categoryId = categoryId,
        categoryName = categoryName,
        colorName = colorName,
        colorCode1 = colorCode1,
        colorCode2 = colorCode2,
        colorCode3 = colorCode3,
        colorCode4 = colorCode4
    )
