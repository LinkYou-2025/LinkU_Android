package com.linku.core.error

sealed class ApiError : Exception() {

    /** 서버 에러코드 또는 클라이언트 정의 코드 */
    abstract val code: String

    /**
     * 공통 에러.
     * HTTP 상태코드 기반으로 매핑되며 서버 메시지를 그대로 사용한다.
     */
    sealed class Common : ApiError() {
        /** COMMON400 - 잘못된 요청 */
        class BadRequest(
            override val code: String,
            override val message: String
        ) : Common()

        /** COMMON401 - 인증 필요 */
        class Unauthorized(
            override val code: String,
            override val message: String
        ) : Common()

        /** COMMON403 - 접근 금지 */
        class Forbidden(
            override val code: String,
            override val message: String
        ) : Common()

        /** COMMON429 - 요청 과다 */
        class TooManyRequests(
            override val code: String,
            override val message: String
        ) : Common()

        /** COMMON500 - 서버 내부 오류 */
        class InternalServer(
            override val code: String,
            override val message: String
        ) : Common()
    }

    /**
     * 네트워크 에러.
     * 서버 응답 없이 클라이언트에서 직접 생성되므로 기본 코드와 메시지를 사용한다.
     */
    sealed class Network : ApiError() {
        /** 인터넷 연결 없음 (UnknownHostException, IOException) */
        class NoConnection(
            override val code: String = "NETWORK_NO_CONNECTION",
            override val message: String = "네트워크 연결을 확인해주세요."
        ) : Network()

        /** 연결 시간 초과 (SocketTimeoutException) */
        class Timeout(
            override val code: String = "NETWORK_TIMEOUT",
            override val message: String = "연결 시간이 초과되었습니다."
        ) : Network()
    }

    /**
     * 약관 관련 에러.
     */
    sealed class Terms : ApiError() {
        /** TERMS4001 - 유효하지 않은 약관 타입 */
        class InvalidTermsType(
            override val code: String,
            override val message: String
        ) : Terms()
    }


    /**
     * 인증/OAuth 관련 에러.
     * 소셜 로그인 및 토큰 관련 에러를 포함한다.
     */
    sealed class Auth : ApiError() {
        /** 토큰 만료 - 재로그인 필요 */
        class TokenExpired(
            override val code: String,
            override val message: String
        ) : Auth()

        /** 기기 정보 없음 - 재로그인 필요 */
        class DeviceNotFound(
            override val code: String,
            override val message: String
        ) : Auth()


        /** OAUTH4003 - 소셜 로그인 이메일 필요 */
        class SocialEmailRequired(
            override val code: String,
            override val message: String
        ) : Auth()

        /** OAUTH4004 - 지원하지 않는 소셜 제공자 */
        class SocialUnsupportedProvider(
            override val code: String,
            override val message: String
        ) : Auth()

        /** OAUTH4008 - 유효하지 않거나 만료된 ID 토큰 */
        class InvalidIdToken(
            override val code: String,
            override val message: String
        ) : Auth()

        /** OAUTH4009 - 소셜 계정 ID 필요 */
        class SocialExternalIdRequired(
            override val code: String,
            override val message: String
        ) : Auth()

        /** OAUTH4010 - 올바르지 않은 이메일 형식 */
        class InvalidEmailFormat(
            override val code: String,
            override val message: String
        ) : Auth()

        /** OAUTH5001 - 소셜 계정 연결 실패 */
        class AuthAccountSaveFailed(
            override val code: String,
            override val message: String
        ) : Auth()
    }


    /**
     * 사용자 관련 에러.
     * 회원가입, 로그인, 이메일 인증 등 사용자 관련 에러를 포함한다.
     */
    sealed class User : ApiError() {
        /** USERS4001 - 이미 존재하는 이메일 */
        class AlreadyActiveUser(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4002 - 올바르지 않은 성별 값 */
        class InvalidGender(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4011 - 인증 코드 검증 실패 */
        class VerificationFailed(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4012 - 이메일/비밀번호 불일치 */
        class LoginFailed(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4014 - 소셜 전용 계정 */
        class SocialAccountOnly(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4041 - 사용자를 찾을 수 없음 */
        class NotFound(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4042 - INACTIVE 상태의 사용자 */
        class Inactive(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4091 - 중복된 닉네임 */
        class DuplicateNickname(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS4092 - 중복된 이메일 */
        class DuplicateEmail(
            override val code: String,
            override val message: String
        ) : User()

        /** USERS5001 - 인증 코드 전송 실패 */
        class SendMailFailed(
            override val code: String,
            override val message: String
        ) : User()
    }

    /**
     * S3 파일 관련 에러.
     * 파일 업로드/다운로드/삭제 관련 에러를 포함한다.
     */
    sealed class S3 : ApiError() {
        /** S34001 - 유효하지 않은 파일 */
        class InvalidFile(
            override val code: String,
            override val message: String
        ) : S3()

        /** S34002 - 이미지 파일만 업로드 가능 */
        class InvalidImage(
            override val code: String,
            override val message: String
        ) : S3()

        /** S34003 - 유효하지 않은 S3 URL */
        class InvalidUrl(
            override val code: String,
            override val message: String
        ) : S3()

        /** S34004 - 업로드할 파일 없음 */
        class FileEmpty(
            override val code: String,
            override val message: String
        ) : S3()

        /** S34005 - URL에서 파일명 추출 실패 */
        class ExtractUrlFailed(
            override val code: String,
            override val message: String
        ) : S3()

        /** S3404 - S3 파일을 찾을 수 없음 */
        class FileNotFound(
            override val code: String,
            override val message: String
        ) : S3()

        /** S35001 - S3 파일 업로드 실패 */
        class UploadFailed(
            override val code: String,
            override val message: String
        ) : S3()

        /** S35002 - S3 파일 삭제 실패 */
        class DeleteFailed(
            override val code: String,
            override val message: String
        ) : S3()
    }


    /**
     * LinkU 링크 관련 에러.
     * 링크 저장, 추천 등 링크 관련 에러를 포함한다.
     */
    sealed class Linku : ApiError() {
        /** LINKU4001 - 영상 링크 저장 불가 */
        class VideoNotAllowed(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU4002 - 유효하지 않은 링크 */
        class InvalidUrl(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU4003 - 추천을 위한 링크 부족 */
        class NotEnoughLinks(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU4004 - 추천할 링크 없음 */
        class NoRecommendation(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU4005 - 신규 사용자 추천 불가 */
        class NewUser(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU404 - user_linku 테이블을 찾을 수 없음 */
        class UserLinkuNotFound(
            override val code: String,
            override val message: String
        ) : Linku()

        /** LINKU4041 - 링크 정보를 찾을 수 없음 */
        class NotFound(
            override val code: String,
            override val message: String
        ) : Linku()
    }

    /**
     * 리소스 관련 에러.
     * 카테고리, 도메인, 감정, 상황 등 공통 리소스 에러를 포함한다.
     */
    sealed class Resource : ApiError() {
        /** CATEGORY4041 - 카테고리를 찾을 수 없음 */
        class CategoryNotFound(
            override val code: String,
            override val message: String
        ) : Resource()

        /** DOMAIN4041 - 도메인을 찾을 수 없음 */
        class DomainNotFound(
            override val code: String,
            override val message: String
        ) : Resource()

        /** EMOTION4041 - 감정을 찾을 수 없음 */
        class EmotionNotFound(
            override val code: String,
            override val message: String
        ) : Resource()

        /** FOLDER4041 - 폴더를 찾을 수 없음 */
        class FolderNotFound(
            override val code: String,
            override val message: String
        ) : Resource()

        /** SITUATION4041 - 상황을 찾을 수 없음 */
        class SituationNotFound(
            override val code: String,
            override val message: String
        ) : Resource()
    }

    /**
     * 폴더 관련 에러.
     * 폴더 생성/수정/삭제/공유 관련 에러를 포함한다.
     */
    sealed class Folder : ApiError() {
        /** FOLDER404 - 폴더를 찾을 수 없음 */
        class NotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_PARENT404 - 부모 폴더 없음 */
        class ParentNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_CATEGORY404 - 폴더 카테고리 없음 */
        class CategoryNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_CREATE403 - 폴더 생성 권한 없음 */
        class CreateForbidden(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_UPDATE403 - 폴더 수정 권한 없음 */
        class UpdateForbidden(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_DELETE403 - 폴더 삭제 권한 없음 */
        class DeleteForbidden(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_ACCESS403 - 폴더 접근 권한 없음 */
        class AccessForbidden(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_NAME409 - 카테고리명과 동일한 폴더명 */
        class NameConflict(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_CURSOR400 - 유효하지 않은 커서 값 */
        class InvalidCursor(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_OWNER500 - 폴더 소유자 정보 없음 */
        class OwnerNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_PERMISSION404 - 폴더 권한 정보 없음 */
        class PermissionNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_TOKEN404 - 공유 폴더 토큰 없음 */
        class InvitationNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_TOKEN_INVALID404 - 공유 폴더 토큰 만료 */
        class InvitationExpired(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_LINK_INVALID404 - 공유 폴더 링크 유효하지 않음 */
        class InvitationLinkNotFound(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_OWNER_403 - 폴더 수정 권한 없음 */
        class PermissionNotAllowed(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_OWNER_403 - 폴더 주인 권한 수정 불가 */
        class OwnerUpdateNotAllowed(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_CREATOR403 - 초대 생성자 자신의 링크로 참여 불가 */
        class InvitationCreatorCannotAccept(
            override val code: String,
            override val message: String
        ) : Folder()

        /** PERMISSION400 - 유효하지 않은 권한 타입 */
        class InvalidPermissionType(
            override val code: String,
            override val message: String
        ) : Folder()

        /** FOLDER_BOOKMARK404 - 북마크 정보 없음 */
        class BookmarkNotFound(
            override val code: String,
            override val message: String
        ) : Folder()
    }


    /**
     * AI Article 관련 에러.
     */
    sealed class AiArticle : ApiError() {
        /** AIARTICLE4041 - 해당하는 AI Article을 찾을 수 없음 */
        class NotFound(
            override val code: String,
            override val message: String
        ) : AiArticle()

        /** AIARTICLE4091 - 이미 해당 링크로 생성된 AI Article이 존재 */
        class Duplicate(
            override val code: String,
            override val message: String
        ) : AiArticle()

        /** AIARTICLE500 - AI 요약 처리 중 오류 */
        class InternalServerError(
            override val code: String,
            override val message: String
        ) : AiArticle()
    }

    /**
     * OpenAI 관련 에러.
     */
    sealed class OpenAi : ApiError() {
        /** OPENAI5001 - AI 응답 파싱 실패 */
        class ParseError(
            override val code: String,
            override val message: String
        ) : OpenAi()

        /** OPENAI5002 - AI 응답이 예상한 형식이 아님 */
        class InvalidResponse(
            override val code: String,
            override val message: String
        ) : OpenAi()
    }

    /**
     * 크롤러 관련 에러.
     */
    sealed class Crawler : ApiError() {
        /** CRAWLER5001 - 웹페이지 본문 추출 실패 */
        class ContentExtractionFailed(
            override val code: String,
            override val message: String
        ) : Crawler()

        /** CRAWLER5002 - 크롤링이 금지된 웹사이트 */
        class ContentExtractionProhibited(
            override val code: String,
            override val message: String
        ) : Crawler()
    }

    /**
     * AI Article / Gemini 에러
     */
    sealed class Gemini : ApiError() {
        /** GEMINI4291 - AI 요청이 너무 많음 */
        class TooManyRequests(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5001 - AI 처리 중 알 수 없는 오류 */
        class UnknownError(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5002 - AI 응답 형식이 올바르지 않음 */
        class ResponseFormatError(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5021 - 잘못된 AI 요청 */
        class BadRequest(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5021 - Gemini API 호출 중 오류 */
        class ApiError(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5022 - AI 응답 JSON 파싱 실패 */
        class ParseError(
            override val code: String,
            override val message: String
        ) : Gemini()

        /** GEMINI5041 - Gemini 응답 시간 초과 */
        class Timeout(
            override val code: String,
            override val message: String
        ) : Gemini()
    }


    /**
     * 알림 관련 에러.
     * 알림 조회, 권한, 전송 관련 에러를 포함한다.
     */
    sealed class Alarm : ApiError() {
        /** ALARM_NOT_FOUND - 알람을 찾을 수 없음 */
        class NotFound(
            override val code: String,
            override val message: String
        ) : Alarm()

        /** ALARM_PERMISSION_DENIED - 알람 권한 없음 */
        class PermissionDenied(
            override val code: String,
            override val message: String
        ) : Alarm()

        /** ALARM5001 - 알림 주제 구독 상태 변경 실패 */
        class TopicSubscriptionFailed(
            override val code: String,
            override val message: String
        ) : Alarm()

        /** ALARM5002 - 알림 전송 실패 */
        class SendFailed(
            override val code: String,
            override val message: String
        ) : Alarm()
    }

    /**
     * 위의 어느 케이스에도 해당하지 않는 알 수 없는 에러.
     * [code]와 [message]는 서버 응답 또는 기본값을 사용한다.
     */
    class Unknown(
        override val code: String = "UNKNOWN",
        override val message: String = "알 수 없는 오류가 발생했습니다."
    ) : ApiError()
}