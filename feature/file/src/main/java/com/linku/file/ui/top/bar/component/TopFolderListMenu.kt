package com.linku.file.ui.top.bar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linku.design.modifier.gradientTint
import com.linku.design.modifier.noRippleClickable
import com.linku.design.theme.linkuColors
import com.linku.file.FileViewModel
import com.linku.file.R
import com.linku.file.viewmodel.folder.state.FolderState
import com.linku.file.viewmodel.folder.state.FolderStateViewModel

/**
 * 폴더 목록의 상단 메뉴를 관리하는 컴포저블 함수입니다.
 * [FolderStateViewModel]과 [FileViewModel]을 사용하여 메뉴의 상태를 제어하고,
 * 사용자의 선택에 따라 "나의 폴더"와 "공유받은 폴더" 사이의 전환 로직을 수행합니다.
 *
 * @param folderStateViewModel 메뉴의 확장 상태 및 폴더 유형 상태를 관리하는 뷰모델.
 * @param fileViewModel 공유 폴더 목록 조회 등 파일 관련 데이터를 처리하는 뷰모델.
 *
 * @see TopFolderListMenuLayout
 */
@Composable
fun TopFolderListMenu(
    folderStateViewModel: FolderStateViewModel,
    fileViewModel: FileViewModel
){
    TopFolderListMenuLayout(
        isSharedFolders = folderStateViewModel.isSharedFolders,
        topMenuExpanded = folderStateViewModel.topMenuExpanded,
        onDismissRequest = {
            /*
            * 메뉴의 열린 상태를 닫힘으로 수정하는 로직
            * */

            // topMenuExpanded를 false로 수정
            folderStateViewModel.updateTopMenuExpanded(false)
       },
        onSelectMyFolders = {
            /*
            * 메뉴를 닫음과 동시에
            * 공유받은 폴더들을 보이지 않게 하고
            * 나의 폴더들을 보이게 하는 로직
            * */

            // isShredFolders를 false로 수정
            folderStateViewModel.updateIsSharedFolders(false)

            // topMenuExpanded를 false로 수정
            folderStateViewModel.updateTopMenuExpanded(false)

            // folderState를 TOP으로 수정
            folderStateViewModel.updateFolderState(FolderState.TOP)
        },
        onSelectSharedFolders = {
            /*
            * 메뉴를 닫음과 동시에
            * 나의 폴더들을 보이지 않게 하고
            * 공유받은 폴더들을 보이게 하는 로직
            */

            // 공유 폴더를 받아 뷰모델에 저장
            fileViewModel.getSharedFolders()

            // isShredFolders를 true로 수정
            folderStateViewModel.updateIsSharedFolders(true)

            // topMenuExpanded를 false로 수정
            folderStateViewModel.updateTopMenuExpanded(false)

            // folderState를 TOP으로 수정
            folderStateViewModel.updateFolderState(FolderState.TOP)
        }
    )
}

/**
 * "나의 폴더"와 "공유받은 폴더" 사이를 전환할 수 있는 드롭다운 메뉴 레이아웃을 표시합니다.
 *
 * @param modifier 드롭다운 메뉴에 적용할 [Modifier].
 * @param isSharedFolders 현재 선택된 뷰가 "공유받은 폴더"인지 여부.
 * @param topMenuExpanded 메뉴의 표시 상태 (true이면 메뉴가 열림).
 * @param onDismissRequest 메뉴 바깥 영역을 클릭하거나 닫으려 할 때 호출되는 콜백.
 * @param onSelectMyFolders 메뉴에서 "나의 폴더"를 선택했을 때 수행할 동작.
 * @param onSelectSharedFolders 메뉴에서 "공유받은 폴더"를 선택했을 때 수행할 동작.
 *
 * @see TopFolderListMenu
 * @see TopFolderListMenuRow
 */
@Composable
private fun TopFolderListMenuLayout(
    modifier: Modifier = Modifier,
    isSharedFolders: Boolean,
    topMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectMyFolders: () -> Unit,
    onSelectSharedFolders: () -> Unit
){
    val colors = MaterialTheme.linkuColors
    // 선택된 폴더
    val selectedText = if (isSharedFolders) "공유받은 폴더" else "나의 폴더"

    /*
    * 상단 폴더 목록 메뉴
    *
    * 구조:
    * [ v 나의 폴더     ]
    * [   공유받은 폴더 ]
    * */

    DropdownMenu(
        modifier = modifier
            .width(180.dp)
            .padding(vertical = 15.dp),
        shape = RoundedCornerShape(18.dp),

        //
        offset = DpOffset(0.dp, 10.dp),
        expanded = topMenuExpanded,
        onDismissRequest = onDismissRequest,
        containerColor = colors.white
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterVertically),
        ) {
            TopFolderListMenuRow(
                selectedOption = "나의 폴더",
                selectedText = selectedText,
                onClick = onSelectMyFolders
            )

            TopFolderListMenuRow(
                selectedOption = "공유받은 폴더",
                selectedText = selectedText,
                onClick = onSelectSharedFolders
            )
        }
    }
}

/**
 * 상단 폴더 목록 메뉴의 개별 항목을 표시하는 컴포저블 함수입니다.
 * 현재 선택된 상태에 따라 아이콘과 텍스트의 강조(색상, 굵기 등) 스타일을 다르게 적용합니다.
 *
 * @param selectedOption 해당 메뉴 항목이 나타내는 옵션의 명칭 (예: "나의 폴더", "공유받은 폴더").
 * @param selectedText 현재 실제로 선택되어 있는 옵션의 명칭.
 * @param onClick 항목이 클릭되었을 때 실행할 콜백 함수. 현재 선택된 옵션과 다를 경우에만 작동합니다.
 *
 * @see TopFolderListMenuLayout
 */
@Composable
private fun TopFolderListMenuRow(
    selectedOption: String,
    selectedText: String,
    onClick: () -> Unit
){
    val colors = MaterialTheme.linkuColors

    /*
     * 폴더 목록의 개별 항목
     *
     * 구조: [체크 박스 -> 이름]
     * */

    Row(
        modifier = Modifier

            // 가로 전체 사용
            .fillMaxWidth()

            // 체크가 안된 항목만 체크 가능
            .noRippleClickable(
                enabled = selectedOption != selectedText,
                onClick = onClick
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // 맨 앞 여백
        Spacer(modifier = Modifier.width(21.dp))

        // 체크 박스 공간
        Icon(
            painter = painterResource(R.drawable.ic_top_folders_menu),
            contentDescription = null,
            modifier = Modifier
                .gradientTint(
                    // 선택된 항목은 그라데이션,
                    // 아닌 항목은 체크가 안 보이게 흰색.
                    brush = if (selectedOption == selectedText) colors.maincolor
                            else Brush.horizontalGradient(listOf(colors.white, colors.white)),

                    // 공간 내 로고 부분만 색칠하기 위해 SrcAtop으로 설정
                    blendMode = BlendMode.SrcAtop
                )
        )

        // 체크 박스 공간과 항목명 사이 여백
        Spacer(modifier = Modifier.width(8.dp))

        // 항목명
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        // 폰트 크기 (15sp)
                        fontSize = 15.sp,

                        // 폰트 굵기
                        fontWeight = FontWeight(
                            weight = if (selectedOption == selectedText) 500
                                     else 400
                        ),

                        // 텍스트 그라데이션 색상(링큐 메인 색상)
                        brush = if (selectedOption == selectedText) colors.maincolor
                                else Brush.horizontalGradient(listOf(colors.black, colors.black))
                    )
                ) {
                    // 항목명 텍스트
                    append(selectedOption)
                }
            },
        )
    }
}

@Preview(heightDp = 900)
@Composable
private fun FolderListMenuTest(){
    TopFolderListMenuLayout(
        isSharedFolders = true,
        topMenuExpanded = true,
        onDismissRequest = {},
        onSelectMyFolders = {},
        onSelectSharedFolders = {}
    )
}
