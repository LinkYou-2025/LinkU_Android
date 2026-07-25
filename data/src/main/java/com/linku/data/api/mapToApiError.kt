package com.linku.data.api

import com.linku.core.error.ApiError
import org.json.JSONObject
import retrofit2.HttpException


internal fun mapToApiError(code: String, message: String): ApiError = when (code) {

    // =========================================================
    // 공통 에러 (COMMON)
    // =========================================================
    "COMMON400" -> ApiError.Common.BadRequest(message)
    "COMMON401" -> ApiError.Common.Unauthorized(message)
    "COMMON403" -> ApiError.Common.Forbidden(message)
    "COMMON404" -> ApiError.Common.NotFound(message)
    "COMMON429" -> ApiError.Common.TooManyRequests(message)
    "COMMON500" -> ApiError.Common.InternalServer(message)

    // =========================================================
    // S3 파일 에러 (S3)
    // =========================================================
    "S34001" -> ApiError.S3.InvalidFile(message)
    "S34002" -> ApiError.S3.InvalidImage(message)
    "S34003" -> ApiError.S3.InvalidUrl(message)
    "S34004" -> ApiError.S3.FileEmpty(message)
    "S34005" -> ApiError.S3.ExtractUrlFailed(message)
    "S3404" -> ApiError.S3.FileNotFound(message)
    "S35001" -> ApiError.S3.UploadFailed(message)
    "S35002" -> ApiError.S3.DeleteFailed(message)


    // =========================================================
    // 인증/OAuth 에러 (OAUTH)
    // =========================================================
    "AUTH4001" -> ApiError.Common.Unauthorized(message)
    "AUTH4011" -> ApiError.Auth.InvalidIdToken(message)
    "AUTH4002" -> ApiError.Auth.TokenExpired(message)
    "AUTH4003" -> ApiError.Common.Forbidden(message)

    "OAUTH4003" -> ApiError.Auth.SocialEmailRequired(message)
    "OAUTH4004" -> ApiError.Auth.SocialUnsupportedProvider(message)
    "OAUTH4008" -> ApiError.Auth.InvalidIdToken(message)
    "OAUTH4009" -> ApiError.Auth.SocialExternalIdRequired(message)
    "OAUTH4010" -> ApiError.Auth.InvalidEmailFormat(message)
    "OAUTH5001" -> ApiError.Auth.AuthAccountSaveFailed(message)

    // =========================================================
    // 사용자 에러 (USERS)
    // =========================================================
    "USERS4001" -> ApiError.User.AlreadyActiveUser(message)
    "USERS4002" -> ApiError.User.InvalidGender(message)
    "USERS4004" -> ApiError.User.ExpiredVerificationCode(message)
    "USERS4005" -> ApiError.User.InvalidPassword(message)
    "USERS4006" -> ApiError.User.PasswordMismatch(message)
    "USERS4011" -> ApiError.User.VerificationFailed(message)
    "USERS4012" -> ApiError.User.LoginFailed(message)
    "USERS4014" -> ApiError.User.SocialAccountOnly(message)
    "USERS4041" -> ApiError.User.NotFound(message)
    "USERS4042" -> ApiError.User.Inactive(message)
    "USERS4091" -> ApiError.User.DuplicateNickname(message)
    "USERS4092" -> ApiError.User.DuplicateEmail(message)
    "USERS5001" -> ApiError.User.SendMailFailed(message)
    "TERMS4001" -> ApiError.Terms.InvalidTermsType(message)

    // =========================================================
    // LinkU 링크 에러 (LINKU)
    // =========================================================
    "LINKU4001" -> ApiError.Linku.VideoNotAllowed(message)
    "LINKU4002" -> ApiError.Linku.InvalidUrl(message)
    "LINKU4003" -> ApiError.Linku.NotEnoughLinks(message)
    "LINKU4004" -> ApiError.Linku.NoRecommendation(message)
    "LINKU4005" -> ApiError.Linku.NewUser(message)
    "LINKU404" -> ApiError.Linku.UserLinkuNotFound(message)
    "LINKU4041" -> ApiError.Linku.NotFound(message)
    "LINKU4043" -> ApiError.Linku.SearchHistoryNotFound(message)

    // =========================================================
    // 카테고리/도메인/감정/상황 에러
    // =========================================================
    "CATEGORY4041" -> ApiError.Resource.CategoryNotFound(message)
    "DOMAIN4041" -> ApiError.Resource.DomainNotFound(message)
    "EMOTION4041" -> ApiError.Resource.EmotionNotFound(message)
    "FOLDER4041" -> ApiError.Resource.FolderNotFound(message)
    "SITUATION4041" -> ApiError.Resource.SituationNotFound(message)

    // =========================================================
    // 폴더 에러 (FOLDER)
    // =========================================================
    "FOLDER404" -> ApiError.Folder.NotFound(message)
    "FOLDER_PARENT404" -> ApiError.Folder.ParentNotFound(message)
    "FOLDER_CATEGORY404" -> ApiError.Folder.CategoryNotFound(message)
    "FOLDER_CREATE403" -> ApiError.Folder.CreateForbidden(message)
    "FOLDER_UPDATE403" -> ApiError.Folder.UpdateForbidden(message)
    "FOLDER_DELETE403" -> ApiError.Folder.DeleteForbidden(message)
    "FOLDER_ACCESS403" -> ApiError.Folder.AccessForbidden(message)
    "FOLDER_NAME409" -> ApiError.Folder.NameConflict(message)
    "FOLDER_CURSOR400" -> ApiError.Folder.InvalidCursor(message)
    "FOLDER_OWNER500" -> ApiError.Folder.OwnerNotFound(message)
    "FOLDER_PERMISSION404" -> ApiError.Folder.PermissionNotFound(message)
    "FOLDER_TOKEN404" -> ApiError.Folder.InvitationNotFound(message)
    "FOLDER_TOKEN_INVALID404" -> ApiError.Folder.InvitationExpired(message)
    "FOLDER_LINK_INVALID404" -> ApiError.Folder.InvitationLinkNotFound(message)
    "FOLDER_OWNER_403" -> {
        if (message.contains("주인") || message.contains("OWNER")) {
            ApiError.Folder.OwnerUpdateNotAllowed(message)
        } else {
            ApiError.Folder.PermissionNotAllowed(message)
        }
    }

    "FOLDER_CREATOR403" -> ApiError.Folder.InvitationCreatorCannotAccept(message)
    "PERMISSION400" -> ApiError.Folder.InvalidPermissionType(message)
    "FOLDER_BOOKMARK404" -> ApiError.Folder.BookmarkNotFound(message)

    // =========================================================
    // AI Article 에러 (AIARTICLE)
    // =========================================================
    "AIARTICLE4041" -> ApiError.AiArticle.NotFound(message)
    "AIARTICLE500" -> ApiError.AiArticle.InternalServerError(message)

    // =========================================================
    // OpenAI 에러 (OPENAI)
    // =========================================================
    "OPENAI5001" -> ApiError.OpenAi.ParseError(message)
    "OPENAI5002" -> ApiError.OpenAi.InvalidResponse(message)

    // =========================================================
    // 크롤러 에러 (CRAWLER)
    // =========================================================
    "CRAWLER5001" -> ApiError.Crawler.ContentExtractionFailed(message)
    "CRAWLER5002" -> ApiError.Crawler.ContentExtractionProhibited(message)

    // =========================================================
    // Gemini 에러 (GEMINI) - GEMINI5021 중복 코드로 인해 제외
    // =========================================================
    "GEMINI5021" -> ApiError.Gemini.GeminiApiError(message)
    "GEMINI5022" -> ApiError.Gemini.ParseError(message)
    "GEMINI5041" -> ApiError.Gemini.Timeout(message)

    // =========================================================
    // 알림 에러 (ALARM)
    // =========================================================
    "ALARM404" -> ApiError.Alarm.NotFound(message)
    "ALARM403" -> ApiError.Alarm.PermissionDenied(message)
    "ALARM5001" -> ApiError.Alarm.TopicSubscriptionFailed(message)
    "ALARM5002" -> ApiError.Alarm.SendFailed(message)

    // =========================================================
    // 큐레이션 에러 (CURATION)
    // =========================================================
    "CURATION4001" -> ApiError.Curation.InvalidYear(message)
    "CURATION4031" -> ApiError.Curation.AccessForbidden(message)
    "CURATION4041" -> ApiError.Curation.NotFound(message)

    // =========================================================
    // 알 수 없는 에러
    // =========================================================
    else -> ApiError.Unknown(message)
}


/**
 * Retrofit은 2xx가 아닌 응답을 받으면 응답 바디를 파싱하지 않고 곧바로 [HttpException]을 던짐.
 * 그래서 예전엔 [e]의 HTTP 상태코드만 보고 400/401/403/429/5xx 구간별로만 뭉뚱그려 매핑했는데,
 * 서버가 비즈니스 에러를 항상 저 5개 상태코드로만 내려주지 않아서 문제였음 - 예를 들어 이메일 중복
 * (USERS4092)은 409로 내려오는데 위 when엔 409가 없어서 else(Unknown)로 빠졌고, 바디에 있는 진짜
 * 서버 코드/메시지("USERS4092", "중복된 이메일입니다.")는 통째로 버려져서 화면엔 일반 실패 문구만 떴음.
 *
 * 이제 에러 바디를 먼저 파싱해서 서버가 내려준 실제 `code`가 있으면 [mapToApiError]로 위임함
 * (상태코드가 뭐든 상관없이 비즈니스 코드 기준으로 정확히 매핑됨). 바디가 없거나 파싱에 실패한 경우
 * (진짜 순수 HTTP 레벨 오류)에만 상태코드 기반 매핑으로 폴백함.
 */
internal fun mapHttpError(e: HttpException): ApiError {
    val bodyJson = try {
        e.response()?.errorBody()?.string()?.let { JSONObject(it) }
    } catch (_: Exception) {
        null
    }
    val bodyCode = bodyJson?.optString("code")?.takeIf { it.isNotBlank() }

    if (bodyJson != null && bodyCode != null) {
        return mapToApiError(bodyCode, bodyJson.optString("message"))
    }

    val message = e.message() ?: "알 수 없는 오류가 발생했습니다."
    return when (e.code()) {
        400 -> ApiError.Common.BadRequest(message)
        401 -> ApiError.Common.Unauthorized(message)
        403 -> ApiError.Common.Forbidden(message)
        404 -> ApiError.Common.NotFound(message)
        429 -> ApiError.Common.TooManyRequests(message)
        in 500..599 -> ApiError.Common.InternalServer(message)
        else -> ApiError.Unknown(message)
    }
}
