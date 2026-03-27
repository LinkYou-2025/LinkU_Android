package com.linku.deeplink

import android.util.Log
import androidx.navigation.NavHostController
import com.linku.NavigationRoute

internal fun appLinkRoute(
    action: String?,
    folderId: Long?,
    onReceiveSharedFolder: (Long)->Unit,
    onUpdateIsSharedFolders: (Boolean)->Unit,
    onSetPendingShare: (Long)->Unit,
    navigator: NavHostController
) {
    if (action == "share" && folderId != null) {
        Log.d("MainApp", "route: appLink 파일 화면으로 이동")

        try {
            Log.d("MainApp", "route: appLink try 진입")

            // 공유 받는 폴더 처리, UI 업데이트 전 api 결과 우선을 위해 동기 처리.
            onReceiveSharedFolder(folderId)

            Log.d("MainApp", "route: appLink 공유 받는 폴더 처리 완료")

            // UI 상태 업데이트
            onUpdateIsSharedFolders(true)
            Log.d("MainApp", "route: appLink 공유 받은 폴더 UI 갱신 완료")

            // 파일 화면으로 이동
            navigator.navigate(NavigationRoute.File.route) {
                Log.d("MainApp", "route: appLink 파일 화면으로 이동")

                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true

                Log.d("MainApp", "route: appLink 파일 화면으로 이동 완료")
            }
        } catch (e: Exception /* UserIdNullException */) {
            Log.e("MainApp", "Exception 발생: $e")

            // (A) 미로그인: 대기 작업 저장 후 로그인 화면으로
            onSetPendingShare(folderId)

            navigator.navigate("${NavigationRoute.Login.route}?showModal=true") {
                Log.d("MainApp", "route: appLink 미로그인. 대기 작업 저장 후 로그인 화면으로")

                popUpTo(NavigationRoute.Splash.route) { inclusive = false }
                launchSingleTop = true

                Log.d("MainApp", "route: appLink 로그인 화면으로 이동 완료")
            }
        }
    }
}
