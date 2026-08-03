package com.linku.link.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun Uri.toTempFile(context: Context): File {
    val fileName = "picked_${System.currentTimeMillis()}.jpg"
    val tempFile = File(context.cacheDir, fileName)

    val inputStream = requireNotNull(
        context.contentResolver.openInputStream(this)
    ) {
        "선택한 이미지 파일을 열 수 없습니다."
    }

    inputStream.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}