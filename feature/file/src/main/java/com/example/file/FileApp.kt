package com.example.file

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.file.ui.link.SaveLinkResultScreen
import com.example.file.viewmodel.edit.state.EditStateViewModel
import com.example.file.viewmodel.folder.state.FolderStateViewModel

@Composable
fun FileApp(
    fileViewModel: FileViewModel = hiltViewModel(),
    editStateViewModel:EditStateViewModel = viewModel(),
    folderStateViewModel: FolderStateViewModel = viewModel()
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    fileViewModel.registeronLinkClick { id ->
        fileViewModel.setLinkDetail(id)
        navController.navigate("savelinkresult/${id}")
    }

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            FileScreen(
                fileViewModel = fileViewModel,
                editStateViewModel = editStateViewModel,
                folderStateViewModel = folderStateViewModel
            )
        }

        composable("savelinkresult/{linkuId}") { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("linkuId")
            val linkuId = backStackEntry.arguments?.getString("linkuId")?.toLongOrNull()

            val currentLinkuId = rememberUpdatedState(linkuId)

            val aiProgress = fileViewModel.aiProgress.collectAsState().value

            // ✅ 네비게이션으로 넘어온 값(문자열/파싱 결과) 확인
            Log.d("SaveLinkFlow", "넘어온 값 -> route arg (raw) = $raw")
            Log.d("SaveLinkFlow", "넘어온 값 -> route arg (parsed) = $linkuId")

            if (linkuId == null) {
                // 잘못 들어온 경우 안전하게 되돌리기
                Log.d("HomeAppLinkResult", "id가 null입니다.")
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                // 화면 진입 시 상세 로드
//                LaunchedEffect(linkuId) {
//                    viewModel.loadLinkDetail(linkuId) // 상세 안에 있으면 이걸로 표시, 없으면 내부에서 AI 호출
//                }

                val link = fileViewModel.linkDetail.collectAsState().value
                val aiArticle = fileViewModel.aiArticleDetail.collectAsState().value
                val isLoadingLink = fileViewModel.isLoadingLinkDetail.collectAsState().value
                val isLoadingAi = fileViewModel.isLoadingAiArticle.collectAsState().value
                val categoryColorMap = fileViewModel.categoryColorMap.collectAsState().value

                // 🔹 상세 데이터 전달 (필요시 로딩 상태 활용)
                SaveLinkResultScreen(
                    link = link,
                    aiArticle = aiArticle,
                    isLoading = (isLoadingLink || isLoadingAi),
                    isAiLoading = isLoadingAi,
                    onBack = { navController.popBackStack() },
                    onOpenLink = { url ->
                        Log.d("FileApp", "onOpenLink 호출! url=$url")
                        val fixed = if (url.startsWith("http")) url else "https://$url"
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse(fixed)
                        )
                        try {
                            context.startActivity(intent)
                        } catch (t: Throwable) {
                            Toast.makeText(context, "링크를 열 수 없어요.", Toast.LENGTH_SHORT).show()
                            Log.e("FileApp", "startActivity 실패", t)
                        }
                    },
                    categoryColorMap = categoryColorMap,
                    onSubmitEdit = { title, memo, categoryId, emotionId ->
                        fileViewModel.updateLink(
                            title = title,
                            memo = memo,
                            categoryId = categoryId,
                            emotionId = emotionId,
                            onSucceed = {
                                Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show()
                            },
                            onFailed = { e ->
                                Log.e("SaveLinkFlow", "수정 실패", e)
                                Toast.makeText(context, e.message ?: "수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onRequestAiSummary = {
                        val id = currentLinkuId.value
                        Log.d("FileApp", "onRequestAiSummary 호출! linkuId=$id, vm=${fileViewModel.hashCode()}")
                        if (id != null) {
                            fileViewModel.loadAiArticle(id)
                        } else {
                            Log.e("FileApp", "linkuId가 null이라 AI 호출 불가")
                        }
                    },
                    aiProgress = aiProgress,
                    onCancelAi = { fileViewModel.cancelAiArticleJob() },
                )
            }
        }
    }
}