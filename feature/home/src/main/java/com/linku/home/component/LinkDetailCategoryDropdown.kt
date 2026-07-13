package com.linku.home.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors

data class LinkCategoryOption(
    val id: Long,
    val name: String,
    val color: Color
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
                        .background(category.color)
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
                LinkCategoryOption(1L, "카테고리2", Color(0xFF55D6C2)),
                LinkCategoryOption(2L, "카테고리3", Color(0xFFFFBE3D)),
                LinkCategoryOption(3L, "카테고리4", Color(0xFF2FB4E9)),
                LinkCategoryOption(4L, "카테고리5", Color(0xFFFF5757)),
                LinkCategoryOption(5L, "카테고리6", Color(0xFF67D414)),
                LinkCategoryOption(6L, "카테고리7", Color(0xFFD9DEE6)),
                LinkCategoryOption(7L, "카테고리8", Color(0xFFD9DEE6)),
                LinkCategoryOption(8L, "카테고리9", Color(0xFFD9DEE6)),
                LinkCategoryOption(9L, "카테고리10", Color(0xFFD9DEE6))
            ),
            selectedCategoryId = 1L,
            onCategoryClick = { }
        )
    }
}