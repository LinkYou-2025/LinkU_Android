package com.linku.core.error

/**
 * 앱 내 모든 도메인/비즈니스 에러의 최상위 규격입니다.
 * UI 레이어에서 일관되게 에러 메시지를 노출할 수 있도록 도와줍니다.
 */
sealed class AppError(
    override val code: String,
    open val displayMessage: String
) : BaseError(code, displayMessage)

/**
 * 네트워크 에러.
 * 서버 응답 없이 클라이언트에서 직접 생성되므로 기본 코드와 메시지를 사용한다.
 */
sealed class NetworkError(
    override val displayMessage: String
) : AppError(code = "NETWORK_ERROR", displayMessage = displayMessage) {

    class NoConnection : NetworkError("네트워크 연결을 확인해주세요.")
    class Timeout : NetworkError("연결 시간이 초과되었습니다.")
}

/**
 * API 요청 및 처리 과정에서 발생하는 에러를 정의하는 봉인 클래스입니다.
 *
 * [BaseError]와 [AppError]를 상속받으며, 서버로부터 전달받은 구체적인 에러 응답 상황을 분류하여 제공합니다.
 * 기본적으로는 서버가 전달한 [serverMessage]를 화면 문구로 사용하며, 필요 시 하위 클래스에서 커스텀 문구로 재정의합니다.
 *
 * 이 클래스는 하위에 다음과 같은 도메인별 에러를 포함합니다:
 * - [Common]: HTTP 상태 코드와 매핑되는 일반적인 서버 에러
 * - [Terms]: 약관 처리 관련 에러
 * - [Auth]: 인증, 토큰, 소셜 로그인 관련 에러
 * - [User]: 사용자 계정, 프로필, 중복 확인 관련 에러
 * - [S3]: 파일 업로드 및 스토리지 서비스 관련 에러
 * - [Linku]: 링크 저장 및 추천 로직 관련 에러
 * - [Resource]: 카테고리, 폴더 등 공통 리소스 조회 에러
 * - [Folder]: 폴더 생성, 수정, 권한 및 공유 관련 에러
 * - [AiArticle], [OpenAi], [Gemini]: AI 요약 및 처리 관련 에러
 * - [Crawler]: 웹 크롤링 관련 에러
 * - [Alarm]: 알림 및 푸시 서비스 관련 에러
 * - [Unknown]: 정의되지 않은 기타 에러
 *
 * @param code 에러를 식별하기 위한 고유 코드
 * @param serverMessage 사용자에게 표시할 에러 메시지
 * @see BaseError
 * @see Exception
 */
sealed class ApiError(
    override val code: String,
    val serverMessage: String
) : AppError(code, serverMessage) {

    override val displayMessage: String = serverMessage

    /**
     * 공통 에러.
     * HTTP 상태 코드(400, 401, 403, 429, 500 등)를 기반으로 매핑되며,
     * 서버에서 전달하는 에러 메시지를 우선적으로 사용합니다.
     *
     * @property BadRequest COMMON400 - 잘못된 요청
     * @property Unauthorized COMMON401 - 인증 필요
     * @property Forbidden COMMON403 - 접근 금지
     * @property TooManyRequests COMMON429 - 요청 과다
     * @property InternalServer COMMON500 - 서버 내부 오류
     */
    sealed class Common(code: String, message: String) : ApiError(code, message) {
        /** COMMON400 - 잘못된 요청 */
        class BadRequest(code: String, message: String) : Common(code, message)

        /** COMMON401 - 인증 필요 */
        class Unauthorized(code: String, message: String) : Common(code, message) {
            /* 프론트 자체적으로 관리 필요한 문구 지정 예시 입니당 */
            override val displayMessage = "로그인 세션이 만료되었습니다. 다시 로그인해주세요."
        }

        /** COMMON403 - 접근 금지 */
        class Forbidden(code: String, message: String) : Common(code, message)

        /** COMMON429 - 요청 과다 */
        class TooManyRequests(code: String, message: String) : Common(code, message)

        /** COMMON500 - 서버 내부 오류 */
        class InternalServer(code: String, message: String) : Common(code, message)
    }



    /**
     * 약관 관련 에러.
     */
    sealed class Terms(code: String, message: String) : ApiError(code, message) {
        /** TERMS4001 - 유효하지 않은 약관 타입 */
        class InvalidTermsType(code: String, message: String) : Terms(code, message)
    }

    /**
     * 인증/OAuth 관련 에러.
     * 소셜 로그인 및 토큰 관련 에러를 포함한다.
     */
    sealed class Auth(code: String, message: String) : ApiError(code, message) {
        /** 토큰 만료 - 재로그인 필요 */
        class TokenExpired(code: String, message: String) : Auth(code, message)

        /** 기기 정보 없음 - 재로그인 필요 */
        class DeviceNotFound(code: String, message: String) : Auth(code, message)

        /** OAUTH4003 - 소셜 로그인 이메일 필요 */
        class SocialEmailRequired(code: String, message: String) : Auth(code, message)

        /** OAUTH4004 - 지원하지 않는 소셜 제공자 */
        class SocialUnsupportedProvider(code: String, message: String) : Auth(code, message)

        /** OAUTH4008 - 유효하지 않거나 만료된 ID 토큰 */
        class InvalidIdToken(code: String, message: String) : Auth(code, message)

        /** OAUTH4009 - 소셜 계정 ID 필요 */
        class SocialExternalIdRequired(code: String, message: String) : Auth(code, message)

        /** OAUTH4010 - 올바르지 않은 이메일 형식 */
        class InvalidEmailFormat(code: String, message: String) : Auth(code, message)

        /** OAUTH5001 - 소셜 계정 연결 실패 */
        class AuthAccountSaveFailed(code: String, message: String) : Auth(code, message)
    }

    /**
     * 사용자 관련 에러.
     * 회원가입, 로그인, 이메일 인증 등 사용자 관련 에러를 포함한다.
     */
    sealed class User(code: String, message: String) : ApiError(code, message) {
        /** USERS4001 - 이미 존재하는 이메일 */
        class AlreadyActiveUser(code: String, message: String) : User(code, message)

        /** USERS4002 - 올바르지 않은 성별 값 */
        class InvalidGender(code: String, message: String) : User(code, message)

        /** USERS4011 - 인증 코드 검증 실패 */
        class VerificationFailed(code: String, message: String) : User(code, message)

        /** USERS4012 - 이메일/비밀번호 불일치 */
        class LoginFailed(code: String, message: String) : User(code, message)

        /** USERS4014 - 소셜 전용 계정 */
        class SocialAccountOnly(code: String, message: String) : User(code, message)

        /** USERS4041 - 사용자를 찾을 수 없음 */
        class NotFound(code: String, message: String) : User(code, message)

        /** USERS4042 - INACTIVE 상태의 사용자 */
        class Inactive(code: String, message: String) : User(code, message)

        /** USERS4091 - 중복된 닉네임 */
        class DuplicateNickname(code: String, message: String) : User(code, message)

        /** USERS4092 - 중복된 이메일 */
        class DuplicateEmail(code: String, message: String) : User(code, message)

        /** USERS5001 - 인증 코드 전송 실패 */
        class SendMailFailed(code: String, message: String) : User(code, message)
    }

    /**
     * S3 파일 관련 에러.
     * 파일 업로드/다운로드/삭제 관련 에러를 포함한다.
     */
    sealed class S3(code: String, message: String) : ApiError(code, message) {
        /** S34001 - 유효하지 않은 파일 */
        class InvalidFile(code: String, message: String) : S3(code, message)

        /** S34002 - 이미지 파일만 업로드 가능 */
        class InvalidImage(code: String, message: String) : S3(code, message)

        /** S34003 - 유효하지 않은 S3 URL */
        class InvalidUrl(code: String, message: String) : S3(code, message)

        /** S34004 - 업로드할 파일 없음 */
        class FileEmpty(code: String, message: String) : S3(code, message)

        /** S34005 - URL에서 파일명 추출 실패 */
        class ExtractUrlFailed(code: String, message: String) : S3(code, message)

        /** S3404 - S3 파일을 찾을 수 없음 */
        class FileNotFound(code: String, message: String) : S3(code, message)

        /** S35001 - S3 파일 업로드 실패 */
        class UploadFailed(code: String, message: String) : S3(code, message)

        /** S35002 - S3 파일 삭제 실패 */
        class DeleteFailed(code: String, message: String) : S3(code, message)
    }

    /**
     * LinkU 링크 관련 에러.
     * 링크 저장, 추천 등 링크 관련 에러를 포함한다.
     */
    sealed class Linku(code: String, message: String) : ApiError(code, message) {
        /** LINKU4001 - 영상 링크 저장 불가 */
        class VideoNotAllowed(code: String, message: String) : Linku(code, message)

        /** LINKU4002 - 유효하지 않은 링크 */
        class InvalidUrl(code: String, message: String) : Linku(code, message)

        /** LINKU4003 - 추천을 위한 링크 부족 */
        class NotEnoughLinks(code: String, message: String) : Linku(code, message)

        /** LINKU4004 - 추천할 링크 없음 */
        class NoRecommendation(code: String, message: String) : Linku(code, message)

        /** LINKU4005 - 신규 사용자 추천 불가 */
        class NewUser(code: String, message: String) : Linku(code, message)

        /** LINKU404 - user_linku 테이블을 찾을 수 없음 */
        class UserLinkuNotFound(code: String, message: String) : Linku(code, message)

        /** LINKU4041 - 링크 정보를 찾을 수 없음 */
        class NotFound(code: String, message: String) : Linku(code, message)
    }

    /**
     * 리소스 관련 에러.
     * 카테고리, 도메인, 감정, 상황 등 공통 리소스 에러를 포함한다.
     */
    sealed class Resource(code: String, message: String) : ApiError(code, message) {
        /** CATEGORY4041 - 카테고리를 찾을 수 없음 */
        class CategoryNotFound(code: String, message: String) : Resource(code, message)

        /** DOMAIN4041 - 도메인을 찾을 수 없음 */
        class DomainNotFound(code: String, message: String) : Resource(code, message)

        /** EMOTION4041 - 감정을 찾을 수 없음 */
        class EmotionNotFound(code: String, message: String) : Resource(code, message)

        /** FOLDER4041 - 폴더를 찾을 수 없음 */
        class FolderNotFound(code: String, message: String) : Resource(code, message)

        /** SITUATION4041 - 상황을 찾을 수 없음 */
        class SituationNotFound(code: String, message: String) : Resource(code, message)
    }

    /**
     * 폴더 관련 에러.
     * 폴더 생성/수정/삭제/공유 관련 에러를 포함한다.
     */
    sealed class Folder(code: String, message: String) : ApiError(code, message) {
        /** FOLDER404 - 폴더를 찾을 수 없음 */
        class NotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_PARENT404 - 부모 폴더 없음 */
        class ParentNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_CATEGORY404 - 폴더 카테고리 없음 */
        class CategoryNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_CREATE403 - 폴더 생성 권한 없음 */
        class CreateForbidden(code: String, message: String) : Folder(code, message)

        /** FOLDER_UPDATE403 - 폴더 수정 권한 없음 */
        class UpdateForbidden(code: String, message: String) : Folder(code, message)

        /** FOLDER_DELETE403 - 폴더 삭제 권한 없음 */
        class DeleteForbidden(code: String, message: String) : Folder(code, message)

        /** FOLDER_ACCESS403 - 폴더 접근 권한 없음 */
        class AccessForbidden(code: String, message: String) : Folder(code, message)

        /** FOLDER_NAME409 - 카테고리명과 동일한 폴더명 */
        class NameConflict(code: String, message: String) : Folder(code, message)

        /** FOLDER_CURSOR400 - 유효하지 않은 커서 값 */
        class InvalidCursor(code: String, message: String) : Folder(code, message)

        /** FOLDER_OWNER500 - 폴더 소유자 정보 없음 */
        class OwnerNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_PERMISSION404 - 폴더 권한 정보 없음 */
        class PermissionNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_TOKEN404 - 공유 폴더 토큰 없음 */
        class InvitationNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_TOKEN_INVALID404 - 공유 폴더 토큰 만료 */
        class InvitationExpired(code: String, message: String) : Folder(code, message)

        /** FOLDER_LINK_INVALID404 - 공유 폴더 링크 유효하지 않음 */
        class InvitationLinkNotFound(code: String, message: String) : Folder(code, message)

        /** FOLDER_OWNER_403 - 폴더 수정 권한 없음 */
        class PermissionNotAllowed(code: String, message: String) : Folder(code, message)

        /** FOLDER_OWNER_403 - 폴더 주인 권한 수정 불가 */
        class OwnerUpdateNotAllowed(code: String, message: String) : Folder(code, message)

        /** FOLDER_CREATOR403 - 초대 생성자 자신의 링크로 참여 불가 */
        class InvitationCreatorCannotAccept(code: String, message: String) : Folder(code, message)

        /** PERMISSION400 - 유효하지 않은 권한 타입 */
        class InvalidPermissionType(code: String, message: String) : Folder(code, message)

        /** FOLDER_BOOKMARK404 - 북마크 정보 없음 */
        class BookmarkNotFound(code: String, message: String) : Folder(code, message)
    }

    /**
     * AI Article 관련 에러.
     */
    sealed class AiArticle(code: String, message: String) : ApiError(code, message) {
        /** AIARTICLE4041 - 해당하는 AI Article을 찾을 수 없음 */
        class NotFound(code: String, message: String) : AiArticle(code, message)

        /** AIARTICLE4091 - 이미 해당 링크로 생성된 AI Article이 존재 */
        class Duplicate(code: String, message: String) : AiArticle(code, message)

        /** AIARTICLE500 - AI 요약 처리 중 오류 */
        class InternalServerError(code: String, message: String) : AiArticle(code, message)
    }

    /**
     * OpenAI 관련 에러.
     */
    sealed class OpenAi(code: String, message: String) : ApiError(code, message) {
        /** OPENAI5001 - AI 응답 파싱 실패 */
        class ParseError(code: String, message: String) : OpenAi(code, message)

        /** OPENAI5002 - AI 응답이 예상한 형식이 아님 */
        class InvalidResponse(code: String, message: String) : OpenAi(code, message)
    }

    /**
     * 크롤러 관련 에러.
     */
    sealed class Crawler(code: String, message: String) : ApiError(code, message) {
        /** CRAWLER5001 - 웹페이지 본문 추출 실패 */
        class ContentExtractionFailed(code: String, message: String) : Crawler(code, message)

        /** CRAWLER5002 - 크롤링이 금지된 웹사이트 */
        class ContentExtractionProhibited(code: String, message: String) : Crawler(code, message)
    }

    /**
     * AI Article / Gemini 에러
     */
    sealed class Gemini(code: String, message: String) : ApiError(code, message) {


        /** GEMINI5021 - Gemini API 호출 중 오류 */
        class GeminiApiError(code: String, message: String) : Gemini(code, message)

        /** GEMINI5022 - AI 응답 JSON 파싱 실패 */
        class ParseError(code: String, message: String) : Gemini(code, message)

        /** GEMINI5041 - Gemini 응답 시간 초과 */
        class Timeout(code: String, message: String) : Gemini(code, message)
    }

    /**
     * 알림 관련 에러.
     * 알림 조회, 권한, 전송 관련 에러를 포함한다.
     */
    sealed class Alarm(code: String, message: String) : ApiError(code, message) {
        /** ALARM_NOT_FOUND - 알람을 찾을 수 없음 */
        class NotFound(code: String, message: String) : Alarm(code, message)

        /** ALARM_PERMISSION_DENIED - 알람 권한 없음 */
        class PermissionDenied(code: String, message: String) : Alarm(code, message)

        /** ALARM5001 - 알림 주제 구독 상태 변경 실패 */
        class TopicSubscriptionFailed(code: String, message: String) : Alarm(code, message)

        /** ALARM5002 - 알림 전송 실패 */
        class SendFailed(code: String, message: String) : Alarm(code, message)
    }

    /**
     * 위의 어느 케이스에도 해당하지 않는 알 수 없는 에러.
     * [code]와 [message]는 서버 응답 또는 기본값을 사용한다.
     */
    class Unknown(
        code: String = "UNKNOWN",
        message: String = "알 수 없는 오류가 발생했습니다."
    ) : ApiError(code, message)
}