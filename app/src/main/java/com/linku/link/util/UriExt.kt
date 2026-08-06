package com.linku.link.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.linku.core.model.TempImageFile
import java.io.File
import java.io.FileOutputStream

fun Uri.toTempFile(context: Context): TempImageFile {
    val contentResolver = context.contentResolver

    val mimeType = contentResolver
        .getType(this)
        ?.takeIf { it.startsWith("image/") }
        ?: DEFAULT_MIME_TYPE

    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)
        ?: DEFAULT_EXTENSION

    val tempFile = File(
        context.cacheDir,
        "picked_${System.currentTimeMillis()}.$extension",
    )

    val inputStream = requireNotNull(
        contentResolver.openInputStream(this),
    ) {
        "선택한 이미지 파일을 열 수 없습니다."
    }

    inputStream.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }

    return TempImageFile(
        file = tempFile,
        mimeType = mimeType,
    )
}

private const val DEFAULT_MIME_TYPE = "image/jpeg"
private const val DEFAULT_EXTENSION = "jpg"