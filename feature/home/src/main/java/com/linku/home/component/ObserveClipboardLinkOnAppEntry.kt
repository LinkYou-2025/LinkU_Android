package com.linku.home.component

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.linku.home.model.ClipboardLinkCandidate
import com.linku.home.util.parseClipboardLinkCandidate

/**
 * LinkU가 포그라운드로 진입한 뒤 창 포커스를 얻는 순간 최신 클립보드 링크를 한 번 읽습니다.
 *
 * Android 10 이상에서는 포커스가 없는 앱의 클립보드 접근이 제한되므로 `ON_RESUME`만으로 바로
 * 읽지 않고 창 포커스까지 기다립니다. 앱이 이미 포그라운드인 동안 발생한 클립보드 변경은 관찰하지
 * 않아 링크 작성 중 화면이 갑자기 전환되는 동작을 방지합니다.
 *
 * @param skipClipboardRead 공유 Intent URL처럼 더 우선하는 진입 데이터가 있어 현재 읽기를 건너뛸지 여부
 * @param onCandidateDetected 앱 진입 시 읽은 URL 후보. 클립보드가 웹 URL이 아니면 `null`
 */
@Composable
fun ObserveClipboardLinkOnAppEntry(
    skipClipboardRead: Boolean = false,
    onCandidateDetected: (ClipboardLinkCandidate?) -> Unit,
) {
    val context = LocalContext.current
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused
    val clipboard = remember(context) {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    val currentOnCandidateDetected by rememberUpdatedState(onCandidateDetected)

    // 최초 실행도 앱 진입으로 취급하고, 이후에는 ON_RESUME마다 다음 포커스 획득을 기다립니다.
    var isClipboardReadPending by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isClipboardReadPending = true
    }

    LaunchedEffect(isWindowFocused, isClipboardReadPending, clipboard, skipClipboardRead) {
        if (!isWindowFocused || !isClipboardReadPending) {
            return@LaunchedEffect
        }

        isClipboardReadPending = false
        val candidate = if (skipClipboardRead) {
            null
        } else {
            readClipboardLinkCandidate(context, clipboard)
        }
        currentOnCandidateDetected(candidate)
    }
}

/** 현재 클립보드 첫 항목과 복사 시각을 하나의 링크 후보로 읽습니다. */
private fun readClipboardLinkCandidate(
    context: Context,
    clipboard: ClipboardManager,
): ClipboardLinkCandidate? {
    val clip = clipboard.primaryClip ?: return null
    if (clip.itemCount <= 0) {
        return null
    }

    val clipboardText = clip.getItemAt(0).coerceToText(context)?.toString()
    return parseClipboardLinkCandidate(
        clipboardText = clipboardText,
        copiedAtMillis = clip.description.timestamp,
    )
}
