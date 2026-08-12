package com.linku.core.model

/**
 * 상위 폴더 목록에 적용할 서버 정렬 기준입니다.
 *
 * @property query `parentFolders` API의 `sort` 쿼리에 전달할 안정적인 값입니다.
 */
enum class ParentFolderSort(
    val query: String,
) {
    /** 폴더 이름의 가나다 순으로 정렬합니다. */
    NAME("name"),

    /** 폴더의 최근 수정 시각 순으로 정렬합니다. */
    UPDATED_AT("updatedAt"),
    ;

    companion object {
        /**
         * 저장된 API 쿼리 값을 정렬 기준으로 복원합니다.
         *
         * @param query 기기에 저장된 `sort` 쿼리 값입니다.
         * @return 일치하는 정렬 기준이며, 값이 없거나 알 수 없으면 [NAME]입니다.
         */
        fun fromQuery(query: String?): ParentFolderSort =
            entries.firstOrNull { it.query == query } ?: NAME
    }
}
