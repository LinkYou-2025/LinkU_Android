package com.example.home

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = "onboarding",
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("onboarding") {
            HomeScreen(userName = "세나")
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
                        onSucceed = {
                            Log.d("SaveLinkDebug", "저장 성공: $it")
                            viewModel.resetForm()
                            navController.navigate("savelinkresult")
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
                onBack = { navController.popBackStack() }
            )
        }

        composable("savelinkresult") {
            SaveLinkResultScreen()
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