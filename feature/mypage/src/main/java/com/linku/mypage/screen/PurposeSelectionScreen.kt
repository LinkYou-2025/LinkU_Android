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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.linku.core.model.auth.Purpose
import com.linku.core.model.auth.icon.iconRes
import com.linku.design.BrushText
import com.linku.design.theme.ThemeProvider
import com.linku.design.theme.linkuColors
import com.linku.mypage.component.CustomInfoSelectionContent
import com.linku.mypage.component.CustomInfoSelectionItem

@Composable
fun PurposeSelectionScreen(
    navController: NavController,
    initialSelected: Set<Purpose>,
    onNextClick: (Set<Purpose>) -> Unit
) {
    val colors = MaterialTheme.linkuColors

    val selectedItems = remember(initialSelected) {
        mutableStateListOf(*initialSelected.toTypedArray())
    }
    val hasChanges = selectedItems.toSet() != initialSelected

    CustomInfoSelectionContent(
        questionContent = {
            Column {
                BrushText(
                    text = "어떤 목적으로 링크를",
                    brush = colors.maincolor,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrushText(
                        text = "저장",
                        brush = colors.maincolor,
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )

                    Text(
                        text = "하고 싶으신가요?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.black
                    )
                }

                Text(
                    text = "모두 선택해주세요.",
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
                items(Purpose.entries) { purpose ->
                    val isSelected = purpose in selectedItems

                    CustomInfoSelectionItem(
                        text = purpose.displayName.replace("\n", " "),
                        iconRes = purpose.iconRes,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedItems.remove(purpose)
                            } else {
                                selectedItems.add(purpose)
                            }
                        }
                    )
                }
            }
        },
        buttonText = "다음",
        isButtonEnabled = hasChanges,
        onBackClick = { navController.popBackStack() },
        onButtonClick = { onNextClick(selectedItems.toSet()) }
    )
}

@Preview(showBackground = true)
@Composable
fun PurposeSelectionScreenPreview() {
    val navController = rememberNavController()

    ThemeProvider {
        PurposeSelectionScreen(
            navController = navController,
            initialSelected = emptySet(),
            onNextClick = {}
        )
    }
}
