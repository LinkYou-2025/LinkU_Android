package com.linku.data.api

import com.linku.core.error.ApiError
import retrofit2.HttpException


internal fun mapToApiError(code: String, message: String): ApiError = when (code) {

    // =========================================================
    // 공통 에러 (COMMON)
    // =========================================================
    "COMMON400" -> ApiError.Common.BadRequest(code, message)
    "COMMON401" -> ApiError.Common.Unauthorized(code, message)
    "COMMON403" -> ApiError.Common.Forbidden(code, message)
    "COMMON429" -> ApiError.Common.TooManyRequests(code, message)
    "COMMON500" -> ApiError.Common.InternalServer(code, message)

    "TERMS4001" -> ApiError.Terms.InvalidTermsType(code, message)

    // =========================================================
    // 인증/OAuth 에러 (OAUTH)
    // =========================================================
    "OAUTH4003" -> ApiError.Auth.SocialEmailRequired(code, message)
    "OAUTH4004" -> ApiError.Auth.SocialUnsupportedProvider(code, message)
    "OAUTH4008" -> ApiError.Auth.InvalidIdToken(code, message)
    "OAUTH4009" -> ApiError.Auth.SocialExternalIdRequired(code, message)
    "OAUTH4010" -> ApiError.Auth.InvalidEmailFormat(code, message)
    "OAUTH5001" -> ApiError.Auth.AuthAccountSaveFailed(code, message)

    // =========================================================
    // 사용자 에러 (USERS)
    // =========================================================
    "USERS4001" -> ApiError.User.AlreadyActiveUser(code, message)
    "USERS4002" -> ApiError.User.InvalidGender(code, message)
    "USERS4003" -> ApiError.Auth.TokenExpired(code, message)
    "USERS4011" -> ApiError.User.VerificationFailed(code, message)
    "USERS4012" -> ApiError.User.LoginFailed(code, message)
    "USERS4014" -> ApiError.User.SocialAccountOnly(code, message)
    "USERS4041" -> ApiError.User.NotFound(code, message)
    "USERS4042" -> ApiError.User.Inactive(code, message)
    "USERS4091" -> ApiError.User.DuplicateNickname(code, message)
    "USERS4092" -> ApiError.User.DuplicateEmail(code, message)
    "USERS5001" -> ApiError.User.SendMailFailed(code, message)

    // =========================================================
    // S3 파일 에러 (S3)
    // =========================================================
    "S34001" -> ApiError.S3.InvalidFile(code, message)
    "S34002" -> ApiError.S3.InvalidImage(code, message)
    "S34003" -> ApiError.S3.InvalidUrl(code, message)
    "S34004" -> ApiError.S3.FileEmpty(code, message)
    "S34005" -> ApiError.S3.ExtractUrlFailed(code, message)
    "S3404" -> ApiError.S3.FileNotFound(code, message)
    "S35001" -> ApiError.S3.UploadFailed(code, message)
    "S35002" -> ApiError.S3.DeleteFailed(code, message)

    // =========================================================
    // LinkU 링크 에러 (LINKU)
    // =========================================================
    "LINKU4001" -> ApiError.Linku.VideoNotAllowed(code, message)
    "LINKU4002" -> ApiError.Linku.InvalidUrl(code, message)
    "LINKU4003" -> ApiError.Linku.NotEnoughLinks(code, message)
    "LINKU4004" -> ApiError.Linku.NoRecommendation(code, message)
    "LINKU4005" -> ApiError.Linku.NewUser(code, message)
    "LINKU404" -> ApiError.Linku.UserLinkuNotFound(code, message)
    "LINKU4041" -> ApiError.Linku.NotFound(code, message)

    // =========================================================
    // 카테고리/도메인/감정/상황 에러
    // =========================================================
    "CATEGORY4041" -> ApiError.Resource.CategoryNotFound(code, message)
    "DOMAIN4041" -> ApiError.Resource.DomainNotFound(code, message)
    "EMOTION4041" -> ApiError.Resource.EmotionNotFound(code, message)
    "FOLDER4041" -> ApiError.Resource.FolderNotFound(code, message)
    "SITUATION4041" -> ApiError.Resource.SituationNotFound(code, message)

    // =========================================================
    // 폴더 에러 (FOLDER)
    // =========================================================
    "FOLDER404" -> ApiError.Folder.NotFound(code, message)
    "FOLDER_PARENT404" -> ApiError.Folder.ParentNotFound(code, message)
    "FOLDER_CATEGORY404" -> ApiError.Folder.CategoryNotFound(code, message)
    "FOLDER_CREATE403" -> ApiError.Folder.CreateForbidden(code, message)
    "FOLDER_UPDATE403" -> ApiError.Folder.UpdateForbidden(code, message)
    "FOLDER_DELETE403" -> ApiError.Folder.DeleteForbidden(code, message)
    "FOLDER_ACCESS403" -> ApiError.Folder.AccessForbidden(code, message)
    "FOLDER_NAME409" -> ApiError.Folder.NameConflict(code, message)
    "FOLDER_CURSOR400" -> ApiError.Folder.InvalidCursor(code, message)
    "FOLDER_OWNER500" -> ApiError.Folder.OwnerNotFound(code, message)
    "FOLDER_PERMISSION404" -> ApiError.Folder.PermissionNotFound(code, message)
    "FOLDER_TOKEN404" -> ApiError.Folder.InvitationNotFound(code, message)
    "FOLDER_TOKEN_INVALID404" -> ApiError.Folder.InvitationExpired(code, message)
    "FOLDER_LINK_INVALID404" -> ApiError.Folder.InvitationLinkNotFound(code, message)
    "FOLDER_OWNER_403" -> ApiError.Folder.PermissionNotAllowed(code, message)
    "FOLDER_CREATOR403" -> ApiError.Folder.InvitationCreatorCannotAccept(code, message)
    "PERMISSION400" -> ApiError.Folder.InvalidPermissionType(code, message)
    "FOLDER_BOOKMARK404" -> ApiError.Folder.BookmarkNotFound(code, message)

    // =========================================================
    // AI Article 에러 (AIARTICLE)
    // =========================================================
    "AIARTICLE4041" -> ApiError.AiArticle.NotFound(code, message)
    "AIARTICLE4091" -> ApiError.AiArticle.Duplicate(code, message)
    "AIARTICLE500" -> ApiError.AiArticle.InternalServerError(code, message)

    // =========================================================
    // OpenAI 에러 (OPENAI)
    // =========================================================
    "OPENAI5001" -> ApiError.OpenAi.ParseError(code, message)
    "OPENAI5002" -> ApiError.OpenAi.InvalidResponse(code, message)

    // =========================================================
    // 크롤러 에러 (CRAWLER)
    // =========================================================
    "CRAWLER5001" -> ApiError.Crawler.ContentExtractionFailed(code, message)
    "CRAWLER5002" -> ApiError.Crawler.ContentExtractionProhibited(code, message)

    // =========================================================
    // Gemini 에러 (GEMINI) - GEMINI5021 중복 코드로 인해 제외
    // =========================================================
    "GEMINI4291" -> ApiError.Gemini.TooManyRequests(code, message)
    "GEMINI5001" -> ApiError.Gemini.UnknownError(code, message)
    "GEMINI5002" -> ApiError.Gemini.ResponseFormatError(code, message)
    "GEMINI5022" -> ApiError.Gemini.ParseError(code, message)
    "GEMINI5041" -> ApiError.Gemini.Timeout(code, message)

    // =========================================================
    // 알림 에러 (ALARM)
    // =========================================================
    "ALARM_NOT_FOUND" -> ApiError.Alarm.NotFound(code, message)
    "ALARM_PERMISSION_DENIED" -> ApiError.Alarm.PermissionDenied(code, message)
    "ALARM5001" -> ApiError.Alarm.TopicSubscriptionFailed(code, message)
    "ALARM5002" -> ApiError.Alarm.SendFailed(code, message)

    // =========================================================
    // 알 수 없는 에러
    // =========================================================
    else -> ApiError.Unknown(code, message)
}


internal fun mapHttpError(e: HttpException): ApiError {
    val code = e.code().toString()
    val message = e.message() ?: "알 수 없는 오류가 발생했습니다."
    return when (e.code()) {
        400 -> ApiError.Common.BadRequest(code, message)
        401 -> ApiError.Common.Unauthorized(code, message)
        403 -> ApiError.Common.Forbidden(code, message)
        429 -> ApiError.Common.TooManyRequests(code, message)
        in 500..599 -> ApiError.Common.InternalServer(code, message)
        else -> ApiError.Unknown(code, message)
    }
}