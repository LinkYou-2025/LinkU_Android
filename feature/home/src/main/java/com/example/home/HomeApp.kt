package com.example.home

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.home.screen.HomeScreen
import com.example.home.screen.SaveLinkResultScreen
import com.example.home.screen.SaveLinkScreen
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun HomeApp(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()

//    // === 감정/상황 키 → 서버 ID 매핑 ===
//    fun emotionKeyToId(key: String): Long = when (key) {
//        "joy" -> 1L
//        "calm" -> 2L
//        "excitement" -> 3L
//        "sadness" -> 4L
//        "irritation" -> 5L
//        "anger" -> 6L
//        else -> 0L
//    }
//    fun taskKeyToSituationId(key: String): Long = when (key) {
//        "트렌드 확인" -> 11L
//        "과제 중"   -> 12L
//        "쇼핑 중"   -> 13L
//        "데이트 중" -> 14L
//        "통학 중"   -> 15L
//        "알바 중"   -> 16L
//        "휴식 중"   -> 17L
//        "자기 전"   -> 18L
//        else -> 0L
//    }

    // 외부 브라우저 열기
    fun openUrl(url: String) {
        runCatching {
            val fixed = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fixed))
            context.startActivity(intent)
        }.onFailure {
            Toast.makeText(context, "링크를 열 수 없어요.", Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            HomeScreen(
                homeViewModel = viewModel,
                userName = viewModel.userName.orEmpty().ifBlank { "링큐" },
                showRecommendations = viewModel.showRecommendations,
                recommendedLinks = viewModel.recommendedLinks,
                recentLinks = viewModel.recentLinks,
                isRecommending = viewModel.isRecommending,
                onRecommendRequest = { emotionId, situationId, size ->
                    viewModel.fetchRecommendations(
                        situationId = situationId,
                        emotionId = emotionId,
                        size = size
                    )
                },
                needMoreForRecommendation = viewModel.needMoreForRecommendation,
                onClearNeedMoreNotice = viewModel::clearNeedMoreNotice,
                jobId = viewModel.jobId ?: 2L,
                onLinkClick = { id ->                    // ✅ 추가
                    navController.navigate("savelinkresult/$id")
                }
            )
        }

        composable("savelink") {
            // 이미지 픽커: 선택 → Uri를 임시 File로 복사 → viewModel.setImage(file)
            val imagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    runCatching { uri.toTempFile(context) }
                        .onSuccess { file -> viewModel.setImage(file) }
                        .onFailure {
                            Toast.makeText(context, "이미지 로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            SaveLinkScreen(
                image = viewModel.image,
                url = viewModel.url,
                memo = viewModel.memo,
                selectedEmotionId = viewModel.selectedEmotionId,
                onPickImage = { imagePicker.launch("image/*") },
                onUrlChange = viewModel::setUrl,
                onMemoChange = viewModel::setMemo,
                onEmotionSelect = viewModel::selectEmotion,
                onSaveClick = {
                    viewModel.saveLink(
                        onSucceed = { saved ->
//                            Log.d("SaveLinkDebug", "저장 성공: $it")
                            // ✅ 넘긴 값: 저장 성공한 객체와, 네비게이션에 넘길 linkuId
                            Log.d("SaveLinkFlow", "넘긴 값 -> saved(LinkSimpleInfo) = $saved")
                            Log.d("SaveLinkFlow", "넘긴 값 -> navigate param linkuId = ${saved.linkuId}")

                            viewModel.resetForm()
//                            navController.navigate("savelinkresult")
                            // 저장 직후 상세화면으로 id 전달
                            navController.navigate("savelinkresult/${saved.linkuId}")
                        },
                        onFailed = { e ->
                            Log.e("SaveLinkDebug", "저장 실패", e)
                            Toast.makeText(
                                context,
                                e.message ?: "저장에 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onBack = { navController.popBackStack() },
                isCheckingUrl = viewModel.isCheckingUrl,
                isDuplicateUrl = viewModel.isDuplicateUrl,
                isInvalidLink = viewModel.isInvalidUrl
            )
        }

        composable("savelinkresult/{linkuId}") { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("linkuId")
            val linkuId = backStackEntry.arguments?.getString("linkuId")?.toLongOrNull()

            // ✅ 네비게이션으로 넘어온 값(문자열/파싱 결과) 확인
            Log.d("SaveLinkFlow", "넘어온 값 -> route arg (raw) = $raw")
            Log.d("SaveLinkFlow", "넘어온 값 -> route arg (parsed) = $linkuId")

            if (linkuId == null) {
                // 잘못 들어온 경우 안전하게 되돌리기
                Log.d("HomeAppLinkResult", "id가 null입니다.")
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                // 화면 진입 시 상세 로드
                LaunchedEffect(linkuId) {
                    viewModel.loadLinkDetail(linkuId) // 상세 안에 있으면 이걸로 표시, 없으면 내부에서 AI 호출
                }

                // 🔹 상세 데이터 전달 (필요시 로딩 상태 활용)
                SaveLinkResultScreen(
                    link = viewModel.linkDetail,                     // ✅ LinkResultInfo?
                    aiArticle = viewModel.aiArticleDetail,
                    isLoading = viewModel.isLoadingLinkDetail || viewModel.isLoadingAiArticle,
                    onBack = { navController.popBackStack() },
                    onOpenLink = { url ->
                        val fixed = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fixed))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

/** Uri를 앱 캐시 폴더의 임시 File로 복사 */
private fun Uri.toTempFile(context: android.content.Context): File {
    val fileName = "picked_${System.currentTimeMillis()}.jpg"
    val tempFile = File(context.cacheDir, fileName)
    context.contentResolver.openInputStream(this).use { input: InputStream? ->
        FileOutputStream(tempFile).use { output ->
            if (input != null) input.copyTo(output)
        }
    }
    return tempFile
}