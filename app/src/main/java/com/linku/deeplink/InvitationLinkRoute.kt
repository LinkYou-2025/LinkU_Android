package com.linku.deeplink

import androidx.navigation.NavHostController
import com.linku.NavigationRoute
import com.linku.core.util.caller.getCaller
import com.linku.core.util.logging.LinkuLog
import com.linku.core.util.logging.d
import com.linku.core.util.logging.e

internal fun invitationLinkRoute(
    token: String,
    isLoggedIn: Boolean,
    onReceiveSharedFolderInvitation: (
        String,
        () -> Unit,
        (Throwable) -> Unit,
    ) -> Unit,
    onUpdateIsSharedFolders: (Boolean) -> Unit,
    onSetPendingInvitation: (String) -> Unit,
    onInvalidLink: () -> Unit = {},
    navigator: NavHostController
) {
    val caller = getCaller()

    LinkuLog.d(caller) {"isLoggedIn: $isLoggedIn"}

    val deepLinkDestinationId = navigator.currentDestination?.id

    fun isHandlingDeepLink(): Boolean =
        navigator.currentDestination?.id == deepLinkDestinationId

    fun navigateAsRoot(route: String) {
        navigator.navigate(route) {
            popUpTo(navigator.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun handleInvalidLink() {
        onUpdateIsSharedFolders(false)
        onInvalidLink()
        navigateAsRoot(
            if (isLoggedIn) NavigationRoute.Home.route else "login_root"
        )
    }

    try{
        LinkuLog.d(caller) {"Enter try"}

        if (token.isBlank()) {
            handleInvalidLink()
            return
        }

        if (isLoggedIn) {
            LinkuLog.d(caller) {"Enter if isLoggedIn"}

            LinkuLog.d(caller) {"start onReceiveSharedFolderInvitation"}
            onReceiveSharedFolderInvitation(
                token,
                {
                    if (isHandlingDeepLink()) {
                        runCatching {
                            LinkuLog.d(caller) {"start onUpdateIsSharedFolders"}
                            onUpdateIsSharedFolders(true)
                            LinkuLog.d(caller) {"onUpdateIsSharedFolders done"}

                            LinkuLog.d(caller) {"start NavigationRoute.File"}
                            navigateAsRoot(NavigationRoute.File.route)
                            LinkuLog.d(caller) {"NavigationRoute.File done"}
                        }.onFailure {
                            onUpdateIsSharedFolders(false)
                            if (isHandlingDeepLink()) {
                                runCatching { handleInvalidLink() }
                            }
                        }
                    }
                },
                {
                    if (isHandlingDeepLink()) {
                        runCatching { handleInvalidLink() }
                    }
                },
            )
            LinkuLog.d(caller) {"onReceiveSharedFolderInvitation done"}

            LinkuLog.d(caller) {"Escape if isLoggedIn"}
        } else {
            LinkuLog.d(caller) {"Enter else isLoggedIn"}


            LinkuLog.d(caller) {"start onSetPendingInvitation"}
            onSetPendingInvitation(token)
            LinkuLog.d(caller) {"onSetPendingInvitation done"}

            LinkuLog.d(caller) {"start NavigationRoute.Login"}
            navigateAsRoot("login_root")
            LinkuLog.d(caller) {"NavigationRoute.Login done"}

            LinkuLog.d(caller) {"Escape else isLoggedIn"}
        }
    } catch (e: Exception){
        LinkuLog.e(caller, e) {"Enter catch"}

        onSetPendingInvitation("")

        LinkuLog.d(caller) {"start onInvalidLink"}
        if (isHandlingDeepLink()) {
            runCatching { handleInvalidLink() }
        }
        LinkuLog.d(caller) {"onInvalidLink done"}

        LinkuLog.d(caller, e) {"Enter catch"}
    } finally {
        LinkuLog.d(caller) {"return"}
    }
}
