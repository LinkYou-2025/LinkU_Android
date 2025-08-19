package com.example.file

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.design.modifier.noRippleClickable
import com.example.file.ui.bottom.sheet.BottomFolderEditBottomSheet
import com.example.file.ui.bottom.sheet.LinkCategorizationBottomSheet
import com.example.file.ui.bottom.sheet.NewBottomFolderBottomSheet
import com.example.file.ui.bottom.sheet.TopFolderEditBottomSheet
import com.example.file.ui.content.BottomFolderGrid
import com.example.file.ui.content.LinksGrid
import com.example.file.ui.content.TopFolderGrid
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderState
import com.example.file.viewmodel.folder.state.FolderStateViewModel
import com.example.file.ui.theme.White
import com.example.file.ui.top.bar.FileTopBar
import com.example.file.ui.top.bar.component.ShareButton
import com.example.design.SearchBarTopSheet
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.error.SameNameException
import com.example.file.ui.bottom.sheet.ShareBottomSheet
import com.example.file.ui.content.SharedBottomFolderGrid
import com.example.file.ui.content.SharedTopFolderGrid
import com.example.file.ui.modal.FileModalWindow
import com.example.file.ui.theme.MainColor

@Composable
fun FileScreen(
    fileViewModel: FileViewModel = hiltViewModel(),
    editStateViewModel:EditStateViewModel = viewModel(),
    folderStateViewModel: FolderStateViewModel = viewModel()
) {
    Log.d("FileScreen", "FileScreen")
    // 한 번만 데이터 로딩 (최초 진입 시)
    LaunchedEffect(Unit) {
        Log.d("FileScreen", "LaunchedEffect")
        fileViewModel.getParentfolders()
        fileViewModel.loadNickname()
        fileViewModel.getCategoryColor()
        Log.d("FileScreen", "LaunchedEffect end")
    }

    Log.d("FileScreen", "FileScreen")

    // 뒤로가기 핸들러
    BackHandler(enabled = folderStateViewModel.currentFolderState in listOf(FolderState.BOTTOM, FolderState.LINKS)) {
        editStateViewModel.updateEditMode(false)
        when (folderStateViewModel.currentFolderState) {
            FolderState.BOTTOM -> {
                folderStateViewModel.updateFolderState(FolderState.TOP)
                folderStateViewModel.updateSelectedTopFolder(null)
            }
            FolderState.LINKS -> {
                folderStateViewModel.updateFolderState(FolderState.BOTTOM)
                folderStateViewModel.updateSelectedBottomFolder(null)
            }
            else -> {}
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { },
        containerColor = White,
        topBar = {
            FileTopBar(
                fileViewModel = fileViewModel,
                editStateViewModel = editStateViewModel,
                folderStateViewModel = folderStateViewModel
            )},
    ){ innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp)
            ) {
                item {
                    when(folderStateViewModel.currentFolderState) {
                        FolderState.TOP -> {
                            if(!folderStateViewModel.isSharedFolders){
                                TopFolderGrid(
                                    fileViewModel = fileViewModel,
                                    folderStateViewModel = folderStateViewModel,
                                    editStateViewModel = editStateViewModel
                                )
                            }else{
                                SharedTopFolderGrid(
                                    fileViewModel = fileViewModel,
                                    folderStateViewModel = folderStateViewModel,
                                    editStateViewModel = editStateViewModel
                                )
                            }
                        }
                        FolderState.BOTTOM -> {
                            if(!folderStateViewModel.isSharedFolders){
                                BottomFolderGrid(
                                    fileViewModel = fileViewModel,
                                    editStateViewModel = editStateViewModel,
                                    folderStateViewModel = folderStateViewModel,
                                    onFolderAdd = {
                                        folderStateViewModel.updateNewFolderBottomSheetVisible(true)
                                    }
                                )
                            }else{
                                SharedBottomFolderGrid(
                                    fileViewModel = fileViewModel,
                                    editStateViewModel = editStateViewModel,
                                    folderStateViewModel = folderStateViewModel
                                )
                            }
                        }
                        FolderState.LINKS -> {
                            LinksGrid(
                                fileViewModel = fileViewModel,
                                folderStateViewModel = folderStateViewModel,
                            )
                        }
                    }
                }
            }

            if((folderStateViewModel.currentFolderState == FolderState.BOTTOM)&&(!folderStateViewModel.isSharedFolders)){
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 19.dp, bottom = 8.dp)
                        .noRippleClickable {
                            folderStateViewModel.updateShareBottomSheetVisible(true)
                        }
                ) {
                    ShareButton()
                }
            }
        }

        if (
            folderStateViewModel.topMenuExpanded ||
            folderStateViewModel.bottomMenuExpanded
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }

        // 로딩창
        if (fileViewModel.loading.collectAsState().value) {
            // 로딩 로직
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MainColor),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(R.drawable.linku_logo),
                    contentDescription = "로딩중"
                )
            }
        }
    }

    // ---------- bottom sheets ----------

    // 중분류 폴더 수정 바텀 시트
    TopFolderEditBottomSheet(
        folderStateViewModel = folderStateViewModel,
        fileViewModel = fileViewModel
    )

    // 소분류 폴더 추가하기 바텀 시트
    NewBottomFolderBottomSheet(
        onTextDeliver = {
            fileViewModel.createSubfolder(folderStateViewModel.selectedTopFolder!!.folderId,it)
        },
        folderStateViewModel = folderStateViewModel
    )

    // 소분류 폴더 수정 바텀 시트
    var sameNameExceptionModalVisible by remember { mutableStateOf(false) }

    BottomFolderEditBottomSheet(
        onTextDeliver = {
            try{
                fileViewModel.updateSubfolder(
                    folderStateViewModel.readyToUpdateBottomFolder!!.folderId,
                    it
                )
            }catch (e:SameNameException){
                sameNameExceptionModalVisible = true
            }
        },
        folderStateViewModel = folderStateViewModel
    )

    FileModalWindow(
        visible = sameNameExceptionModalVisible,
        onDismiss = { sameNameExceptionModalVisible = false },
        title = "이미 존재하는 폴더명입니다.",
        positiveText = "확인"
    ) {}

    // 링크 추가하기 바텀 시트
    LinkCategorizationBottomSheet(
        fileViewModel = fileViewModel,
        folderStateViewModel = folderStateViewModel
    )

    // 폴더 공유 바텀 시트
    ShareBottomSheet(
        userName = fileViewModel.nickname.collectAsState().value?:"",
        folderStateViewModel = folderStateViewModel,
        fileViewModel = fileViewModel,
    )
    // ---------- bottom sheets ----------

    // 검색창 탑 시트
    SearchBarTopSheet(
        visible = folderStateViewModel.searchTopSheetVisible,
        onDismiss = { folderStateViewModel.updateSearchTopSheetVisible(false) },
        onQueryChange = { fileViewModel.fastSearch(it) },
        onQuerySave = { fileViewModel.addRecentQuery(it) },
        onQueryDelete = { fileViewModel.removeRecentQuery(it) },
        onQueryClear = { fileViewModel.clearRecentQuery() },
        fastSearchItems = fileViewModel.fastSearchItems.collectAsState().value,
        recentQuerys = fileViewModel.recentQueryList.collectAsState().value.map{it.text}
    )
}

@Preview(
    name = "Pixel 8 Size",
    widthDp = 412,
    heightDp = 915,
    showBackground = true)
@Composable
private fun PreviewFileScreen() {
    FileScreen()
}