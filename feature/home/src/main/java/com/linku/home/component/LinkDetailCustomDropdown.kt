package com.linku.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.ThemeProvider
import com.linku.home.R

@Composable
fun LinkDetailCustomDropdown(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
    onGoClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .width(240.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(LocalColorTheme.current.white)
            .padding(horizontal = 24.dp, vertical = 13.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onEditClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_link_edit),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "링크 수정하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onDeleteClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_link_delete),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "링크 삭제하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onShareClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_link_share),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "링크 공유하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onGoClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_link_go_gray),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "링크 보러가기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = LocalColorTheme.current.black
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewLinkDetailCustomDropdown() {
    ThemeProvider {
        LinkDetailCustomDropdown(
            onEditClick = { },
            onDeleteClick = { },
            onShareClick = { },
            onGoClick = { },
            onDismiss = { },
            modifier = Modifier
        )
    }
}