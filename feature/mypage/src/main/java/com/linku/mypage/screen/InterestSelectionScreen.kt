package com.linku.mypage.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.core.model.auth.Interest
import com.linku.core.model.auth.icon.iconRes
import com.linku.design.BrushText
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.component.CustomInfoSelectionContent
import com.linku.mypage.component.CustomInfoSelectionItem

@Composable
fun InterestSelectionScreen(
    onBackClick: () -> Unit,
    initialSelected: Set<Interest>,
    onFinishClick: (Set<Interest>) -> Unit
) {
    val colors = MaterialTheme.linkuColors

    val selectedItems = remember(initialSelected) {
        mutableStateListOf(*initialSelected.toTypedArray())
    }
    val hasChanges = selectedItems.toSet() != initialSelected

    CustomInfoSelectionContent(
        questionContent = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrushText(
                        text = "어떤 분야의 콘텐츠",
                        brush = colors.maincolor,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )

                    Text(
                        text = "에",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black
                    )
                }

                Text(
                    text = "관심 있으신가요?\n모두 선택해주세요.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.black
                )
            }
        },
        selectionContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(Interest.entries) { interest ->
                    val isSelected = interest in selectedItems

                    CustomInfoSelectionItem(
                        text = interest.displayName.replace("\n", " "),
                        iconRes = interest.iconRes,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedItems.remove(interest)
                            } else {
                                selectedItems.add(interest)
                            }
                        }
                    )
                }
            }
        },
        buttonText = "완료",
        isButtonEnabled = hasChanges,
        onBackClick = onBackClick,
        onButtonClick = { onFinishClick(selectedItems.toSet()) }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewInterestSelectionScreen() {
    ThemeProvider {
        InterestSelectionScreen(
            onBackClick = {},
            initialSelected = emptySet(),
            onFinishClick = {}
        )
    }
}
