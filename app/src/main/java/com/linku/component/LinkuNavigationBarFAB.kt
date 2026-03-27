package com.linku.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linku.design.modifier.gradientTint
import com.linku.design.theme.LocalColorTheme
import com.linku.design.theme.color.Basic
import com.linku.R

/*
* 내비게이션 바 중앙에 들어갈 링크 추가 버튼 컴포넌트
* */
@Composable
fun LinkuNavigationBarFAB(
    /*
    * onClick: 링크 추가 버튼 클릭 시 링크 추가 페이지로 이동하는 콜백
    * */
    onClick: () -> Unit,
){
    // 클릭 상태를 추적하는 인스턴스
    val interactionSource = remember { MutableInteractionSource() }

    // 클릭된 상태를 받는 Boolean
    val isPressed by interactionSource.collectIsPressedAsState()

    // 링크 추가 버튼
    IconButton(
        // TODO: 링크 추가 버튼 넣기, 클릭 시 그라데이션으로 색 바뀌어야 하나?
        // 클릭 시 링크 추가 페이지로 이동
        onClick = onClick,

        // 클릭 상태 반영
        interactionSource = interactionSource,

        modifier = Modifier
            .width(57.6.dp)
            .height(48.dp)

            // 둥근 모서리에 Gray100 색을 적용한 배경
            .background(
                color = LocalColorTheme.current.gray[100],
                shape = RoundedCornerShape(14.dp)
            ),
    ) {
        // 더하기 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_plus),

            contentDescription = "링큐 추가 버튼 더하기 아이콘",

            modifier = Modifier

                // 전체의 20/57 넓이를 차지
                .fillMaxSize(20f/57f)

                .run{
                    if(isPressed)
                        gradientTint(brush = Basic.maincolor)
                    else
                        this
                },

            // 아이콘 Gray400으로 색칠
            tint = LocalColorTheme.current.gray[400],
        )
    }
}

@Preview
@Composable
private fun PreviewLinkuNavigationBarFAB(){
    LinkuNavigationBarFAB{}
}