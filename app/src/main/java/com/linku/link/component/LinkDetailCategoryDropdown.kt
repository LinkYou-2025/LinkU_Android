package com.linku.link.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.color.CategoryColorStyle
import com.linku.design.theme.linkuColors

/**
 * 링크 수정 카테고리 드롭다운에 표시할 카테고리 항목입니다.
 *
 * @property id 링크 수정 요청에 전달할 서버 카테고리 ID입니다.
 * @property name 서버에서 내려온 카테고리 이름입니다.
 * @property colorStyle 서버 색상 코드 네 단계를 변환한 카테고리 색상 스타일입니다.
 */
data class LinkCategoryOption(
    val id: Long,
    val name: String,
    val colorStyle: CategoryColorStyle
)

@Composable
fun LinkDetailCategoryDropdown(
    categories: List<LinkCategoryOption>,
    selectedCategoryId: Long?,
    onCategoryClick: (LinkCategoryOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.linkuColors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.white)
            .heightIn(max = 264.dp)
            .verticalScroll(rememberScrollState())
            .padding(start = 12.dp, top = 13.dp, bottom = 13.dp, end = 56.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategoryId

            Row(
                modifier = Modifier
                    .noRippleClickable {
                        onCategoryClick(category)
                    }
                    .padding(horizontal = 6.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(category.colorStyle.color4)
                )

                Text(
                    text = category.name,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    },
                    color = if (isSelected) {
                        colors.blue[200]
                    } else {
                        colors.gray[800]
                    }
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailCategoryDropdown() {
    ThemeProvider {
        LinkDetailCategoryDropdown(
            categories = listOf(
                LinkCategoryOption(1L, "카테고리2", CategoryColorStyle.categoryStyleList[0]),
                LinkCategoryOption(2L, "카테고리3", CategoryColorStyle.categoryStyleList[1]),
                LinkCategoryOption(3L, "카테고리4", CategoryColorStyle.categoryStyleList[2]),
                LinkCategoryOption(4L, "카테고리5", CategoryColorStyle.categoryStyleList[3]),
                LinkCategoryOption(5L, "카테고리6", CategoryColorStyle.categoryStyleList[4]),
                LinkCategoryOption(6L, "카테고리7", CategoryColorStyle.categoryStyleList[5]),
                LinkCategoryOption(7L, "카테고리8", CategoryColorStyle.categoryStyleList[6]),
                LinkCategoryOption(8L, "카테고리9", CategoryColorStyle.categoryStyleList[7]),
                LinkCategoryOption(9L, "카테고리10", CategoryColorStyle.categoryStyleList[8])
            ),
            selectedCategoryId = 1L,
            onCategoryClick = { }
        )
    }
}
