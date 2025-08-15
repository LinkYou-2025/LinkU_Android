package com.example.data.api


import java.io.IOException
import com.example.core.error.TokenExpiredException
import com.example.data.api.dto.BaseResponse
import com.example.data.api.dto.RefreshApi
import com.example.data.preference.AuthPeference

// --- 기존 ServerApi + RefreshApi 확장 (AuthApi 제거) ---
interface ServerApi :
    UserApi,
    LinkuApi,
    CurationApi,
    AIArticleApi,
    FolderApi,
    CategoryApi,
    RefreshApi



// 이전 리프레쉬 없을 때, 혹시 오류 발생시 참고.
//suspend fun <T> ServerApi.withCheck(
//    getter: suspend ServerApi.() -> BaseResponse<T>
//): T {
//    val response = getter()
//    if (!response.isSuccess) throw Exception(response.message)
//    return response.result
//}
//// refreshToken 갱신 有
////suspend fun <T> ServerApi.withAuth(
////    authPreference: AuthPreference,
////    routine: suspend ServerApi.() -> BaseResponse<T>,
////): T {
////    try {
////        return withCheck { routine() }
////    } catch (_: Exception) {
////        val response = withCheck { refreshToken(authPreference.refreshToken!!) }
////        authPreference.refreshToken = response.refreshToken!!
////        authPreference.accessToken = response.accessToken!!
////        return withCheck { routine() }
////    }
////}
//
//// 토큰 자동 갱신이 불가능하므로 단순 실패 처리 (→ 로그인 페이지로 유도)
//suspend fun <T> ServerApi.withAuth(
//    authPreference: AuthPreference,
//    routine: suspend ServerApi.() -> BaseResponse<T>
//): T {
//    return try {
//        withCheck { routine() }
//    } catch (e: Exception) {
//        // refreshToken 없이 처리 → 로그인 유도
//        throw TokenExpiredException("Access token expired. Please log in again.")
//    }
//}