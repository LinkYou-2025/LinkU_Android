package com.linku.core.error

/**
 * 앱 내 모든 도메인/비즈니스 에러의 최상위 규격입니다.
 * UI 레이어에서 일관되게 에러 메시지를 노출할 수 있도록 도와줍니다.
 */
sealed class AppError(
    open val displayMessage: String
) : BaseError(displayMessage)

/**
 * 네트워크 에러.
 * 서버 응답 없이 클라이언트에서 직접 생성되므로 기본 코드와 메시지를 사용한다.
 */
sealed class NetworkError(
    override val displayMessage: String
) : AppError(displayMessage = displayMessage) {

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
    val serverMessage: String
) : AppError(serverMessage) {

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
    sealed class Common(message: String) : ApiError(message) {
        /** COMMON400 - 잘못된 요청 */
        class BadRequest(message: String) : Common(message)

        /** COMMON401 - 인증 필요 */
        class Unauthorized(message: String) : Common(message) {
            /* 프론트 자체적으로 관리 필요한 문구 지정 예시 입니당 */
            override val displayMessage = "로그인 세션이 만료되었습니다. 다시 로그인해주세요."
        }

        /** COMMON403 - 접근 금지 */
        class Forbidden(message: String) : Common(message)

        /** COMMON429 - 요청 과다 */
        class TooManyRequests(message: String) : Common(message)

        /** COMMON500 - 서버 내부 오류 */
        class InternalServer(message: String) : Common(message)
    }

    /**
     * S3 파일 관련 에러.
     * 파일 업로드/다운로드/삭제 관련 에러를 포함한다.
     */
    sealed class S3(message: String) : ApiError(message) {
        /** S34001 - 유효하지 않은 파일 */
        class InvalidFile(message: String) : S3(message)

        /** S34002 - 이미지 파일만 업로드 가능 */
        class InvalidImage(message: String) : S3(message)

        /** S34003 - 유효하지 않은 S3 URL */
        class InvalidUrl(message: String) : S3(message)

        /** S34004 - 업로드할 파일 없음 */
        class FileEmpty(message: String) : S3(message)

        /** S34005 - URL에서 파일명 추출 실패 */
        class ExtractUrlFailed(message: String) : S3(message)

        /** S3404 - S3 파일을 찾을 수 없음 */
        class FileNotFound(message: String) : S3(message)

        /** S35001 - S3 파일 업로드 실패 */
        class UploadFailed(message: String) : S3(message)

        /** S35002 - S3 파일 삭제 실패 */
        class DeleteFailed(message: String) : S3(message)
    }


    /**
     * 인증/OAuth 관련 에러.
     * 소셜 로그인 및 토큰 관련 에러를 포함한다.
     */
    sealed class Auth(message: String) : ApiError(message) {
        /** AUTH4001 - 인증이 필요합니다. */
        class Unauthorized(message: String) : Auth(message)

        /** AUTH4011 - 잘못된 토큰입니다. */
        class InvalidToken(message: String) : Auth(message)

        /** AUTH4002 - 만료된 토큰입니다. */
        class TokenExpired(message: String) : Auth(message)

        /** AUTH4003 - 권한이 없습니다. */
        class PermissionDenied(message: String) : Auth(message)

        /** OAUTH4003 - 소셜 로그인에 이메일이 필요합니다. */
        class SocialEmailRequired(message: String) : Auth(message)

        /** OAUTH4004 - 지원하지 않는 소셜 제공자입니다. */
        class SocialUnsupportedProvider(message: String) : Auth(message)

        /** OAUTH4008 - 유효하지 않거나 만료된 ID 토큰 */
        class InvalidIdToken(message: String) : Auth(message)

        /** OAUTH4009 - 소셜 계정 ID가 필요합니다. */
        class SocialExternalIdRequired(message: String) : Auth(message)

        /** OAUTH4010 - 올바른 이메일 형식이 아닙니다. */
        class InvalidEmailFormat(message: String) : Auth(message)

        /** OAUTH5001 - 소셜 계정 연결에 실패했습니다. */
        class AuthAccountSaveFailed(message: String) : Auth(message)
    }

    /**
     * 사용자 관련 에러.
     * 회원가입, 로그인, 이메일 인증 등 사용자 관련 에러를 포함한다.
     */
    sealed class User(message: String) : ApiError(message) {
        /** USERS4001 - 이미 존재하는 이메일 */
        class AlreadyActiveUser(message: String) : User(message)

        /** USERS4002 - 올바르지 않은 성별 값 */
        class InvalidGender(message: String) : User(message)

        /** USERS4004 - 인증 코드가 만료되었습니다. */
        class ExpiredVerificationCode(message: String) : User(message)

        /** USERS4005 - 잘못된 비밀번호입니다. */
        class InvalidPassword(message: String) : User(message)

        /** USERS4006 - 비밀번호와 비밀번호 확인이 일치하지 않습니다. */
        class PasswordMismatch(message: String) : User(message)

        /** USERS4011 - 인증 코드 검증 실패 */
        class VerificationFailed(message: String) : User(message)

        /** USERS4012 - 이메일 주소 또는 비밀번호 불일치 */
        class LoginFailed(message: String) : User(message)

        /** USERS4014 - 소셜 전용 계정 */
        class SocialAccountOnly(message: String) : User(message)

        /** USERS4041 - 사용자를 찾을 수 없음 */
        class NotFound(message: String) : User(message)

        /** USERS4042 - 임시 회원탈퇴 상태 */
        class Inactive(message: String) : User(message)

        /** USERS4091 - 중복된 닉네임 */
        class DuplicateNickname(message: String) : User(message)

        /** USERS4092 - 중복된 이메일 */
        class DuplicateEmail(message: String) : User(message)

        /** USERS5001 - 인증 코드 전송 실패 */
        class SendMailFailed(message: String) : User(message)
    }

    sealed class Terms(message: String) : ApiError(message) {
        /** TERMS4001 - 유효하지 않은 약관 타입 */
        class InvalidTermsType(message: String) : Terms(message)
    }

    /**
     * LinkU 링크 관련 에러.
     * 링크 저장, 추천 등 링크 관련 에러를 포함한다.
     */
    sealed class Linku(message: String) : ApiError(message) {
        /** LINKU4001 - 영상 링크 저장 불가 */
        class VideoNotAllowed(message: String) : Linku(message)

        /** LINKU4002 - 유효하지 않은 링크 */
        class InvalidUrl(message: String) : Linku(message)

        /** LINKU4003 - 추천을 위한 링크 부족 */
        class NotEnoughLinks(message: String) : Linku(message)

        /** LINKU4004 - 추천할 링크 없음 */
        class NoRecommendation(message: String) : Linku(message)

        /** LINKU4005 - 신규 사용자 추천 불가 */
        class NewUser(message: String) : Linku(message)

        /** LINKU404 - user_linku 테이블을 찾을 수 없음 */
        class UserLinkuNotFound(message: String) : Linku(message)

        /** LINKU4041 - 링크 정보를 찾을 수 없음 */
        class NotFound(message: String) : Linku(message)
    }

    /**
     * 리소스 관련 에러.
     * 카테고리, 도메인, 감정, 상황 등 공통 리소스 에러를 포함한다.
     */
    sealed class Resource(message: String) : ApiError(message) {
        /** CATEGORY4041 - 카테고리를 찾을 수 없음 */
        class CategoryNotFound(message: String) : Resource(message)

        /** DOMAIN4041 - 도메인을 찾을 수 없음 */
        class DomainNotFound(message: String) : Resource(message)

        /** EMOTION4041 - 감정을 찾을 수 없음 */
        class EmotionNotFound(message: String) : Resource(message)

        /** FOLDER4041 - 폴더를 찾을 수 없음 */
        class FolderNotFound(message: String) : Resource(message)

        /** SITUATION4041 - 상황을 찾을 수 없음 */
        class SituationNotFound(message: String) : Resource(message)
    }

    /**
     * 폴더 관련 에러.
     * 폴더 생성/수정/삭제/공유 관련 에러를 포함한다.
     */
    sealed class Folder(message: String) : ApiError(message) {
        /** FOLDER404 - 해당하는 폴더를 찾을 수 없습니다. */
        class NotFound(message: String) : Folder(message)

        /** FOLDER_PARENT404 - 폴더의 부모 폴더가 없습니다. */
        class ParentNotFound(message: String) : Folder(message)

        /** FOLDER_CATEGORY404 - 폴더의 카테고리가 없습니다. */
        class CategoryNotFound(message: String) : Folder(message)

        /** FOLDER_CREATE403 - 해당하는 폴더를 생성할 권한이 없습니다. */
        class CreateForbidden(message: String) : Folder(message)

        /** FOLDER_UPDATE403 - 해당하는 폴더의 수정 권한이 없습니다. */
        class UpdateForbidden(message: String) : Folder(message)

        /** FOLDER_DELETE403 - 해당하는 폴더의 삭제 권한이 없습니다. */
        class DeleteForbidden(message: String) : Folder(message)

        /** FOLDER_ACCESS403 - 해당 폴더에 접근 권한이 없습니다. */
        class AccessForbidden(message: String) : Folder(message)

        /** FOLDER_NAME409 - 카테고리명과 동일한 폴더명은 사용할 수 없습니다. */
        class NameConflict(message: String) : Folder(message)

        /** FOLDER_CURSOR400 - 유효하지 않은 커서 값입니다. */
        class InvalidCursor(message: String) : Folder(message)

        /** FOLDER_OWNER500 - 폴더의 소유자 정보를 찾을 수 없습니다. */
        class OwnerNotFound(message: String) : Folder(message)

        /** FOLDER_PERMISSION404 - 해당 유저의 폴더 권한 정보를 찾을 수 없습니다. */
        class PermissionNotFound(message: String) : Folder(message)

        /** FOLDER_TOKEN404 - 공유 폴더 토큰을 찾을 수 없습니다. */
        class InvitationNotFound(message: String) : Folder(message)

        /** FOLDER_TOKEN_INVALID404 - 공유 폴더 토큰이 유효하지 않습니다. */
        class InvitationExpired(message: String) : Folder(message)

        /** FOLDER_LINK_INVALID404 - 공유 폴더 링크가 유효하지 않습니다. */
        class InvitationLinkNotFound(message: String) : Folder(message)

        /** FOLDER_OWNER_403 - 폴더 수정 권한을 가지고 있지 않습니다. */
        class PermissionNotAllowed(message: String) : Folder(message)

        /** FOLDER_OWNER_403 - 폴더 주인의 권한은 수정할 수 없습니다. */
        class OwnerUpdateNotAllowed(message: String) : Folder(message)

        /** FOLDER_CREATOR403 - 초대 생성자는 자신의 링크로 참여할 수 없습니다. */
        class InvitationCreatorCannotAccept(message: String) : Folder(message)

        /** PERMISSION400 - 유효하지 않은 권한 타입입니다. */
        class InvalidPermissionType(message: String) : Folder(message)

        /** FOLDER_BOOKMARK404 - 해당 유저의 북마크 정보가 존재하지 않습니다. */
        class BookmarkNotFound(message: String) : Folder(message)
    }

    /**
     * AI Article 관련 에러.
     */
    sealed class AiArticle(message: String) : ApiError(message) {
        /** AIARTICLE4041 - 해당하는 AI Article을 찾을 수 없음 */
        class NotFound(message: String) : AiArticle(message)

        /** AIARTICLE500 - AI 요약 처리 중 오류 */
        class InternalServerError(message: String) : AiArticle(message)
    }

    /**
     * OpenAI 관련 에러.
     */
    sealed class OpenAi(message: String) : ApiError(message) {
        /** OPENAI5001 - AI 응답 파싱 실패 */
        class ParseError(message: String) : OpenAi(message)

        /** OPENAI5002 - AI 응답이 예상한 형식이 아님 */
        class InvalidResponse(message: String) : OpenAi(message)
    }

    /**
     * 크롤러 관련 에러.
     */
    sealed class Crawler(message: String) : ApiError(message) {
        /** CRAWLER5001 - 웹페이지 본문 추출 실패 */
        class ContentExtractionFailed(message: String) : Crawler(message)

        /** CRAWLER5002 - 크롤링이 금지된 웹사이트 */
        class ContentExtractionProhibited(message: String) : Crawler(message)
    }

    /**
     * AI Article / Gemini 에러
     */
    sealed class Gemini(message: String) : ApiError(message) {

        /** GEMINI5021 - Gemini API 호출 중 오류 */
        class GeminiApiError(message: String) : Gemini(message)

        /** GEMINI5022 - AI 응답 JSON 파싱 실패 */
        class ParseError(message: String) : Gemini(message)

        /** GEMINI5041 - Gemini 응답 시간 초과 */
        class Timeout(message: String) : Gemini(message)
    }

    /**
     * 알림 관련 에러.
     * 알림 조회, 권한, 전송 관련 에러를 포함한다.
     */
    sealed class Alarm(message: String) : ApiError(message) {
        /** ALARM404 - 알람을 찾을 수 없음 */
        class NotFound(message: String) : Alarm(message)

        /** ALARM403 - 알람 권한 없음 */
        class PermissionDenied(message: String) : Alarm(message)

        /** ALARM5001 - 알림 주제 구독 상태 변경 실패 */
        class TopicSubscriptionFailed(message: String) : Alarm(message)

        /** ALARM5002 - 알림 전송 실패 */
        class SendFailed(message: String) : Alarm(message)
    }

    /**
     *  큐레이션 관련 에러
     */
    sealed class Curation(message: String) : ApiError(message) {
        /** * CURATION4001 - 2025년부터 조회 가능합니다.
         * (year 파라미터가 2025 미만인 경우 발생)
         */
        class InvalidYear(message: String) : Curation(message)

        /** * CURATION4031 - 해당 큐레이션에 접근할 권한이 없습니다.
         * (본인의 큐레이션이 아닌 경우 발생)
         */
        class AccessForbidden(message: String) : Curation(message)

        /** * CURATION4041 - 해당 큐레이션을 찾을 수 없습니다.
         * (존재하지 않는 큐레이션 ID 조회 시 발생)
         */
        class NotFound(message: String) : Curation(message)
    }

    /**
     * 위의 어느 케이스에도 해당하지 않는 알 수 없는 에러.
     * [code]와 [message]는 서버 응답 또는 기본값을 사용한다.
     */
    class Unknown(
        message: String = "알 수 없는 오류가 발생했습니다."
    ) : ApiError(message)
}