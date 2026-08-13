package com.linku.data.mapper

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.KeyWord
import com.linku.core.model.curation.LinkType
import com.linku.core.model.curation.MyLatestCuration
import com.linku.core.model.curation.MyTopTag
import com.linku.core.model.curation.RecommendLink
import com.linku.core.model.curation.SectionItem
import com.linku.core.model.curation.UnreadLink
import com.linku.data.api.dto.server.curation.CurationDetailDTO
import com.linku.data.api.dto.server.curation.HistoryDTO
import com.linku.data.api.dto.server.curation.KeywordDTO
import com.linku.data.api.dto.server.curation.MyLatestCurationDTO
import com.linku.data.api.dto.server.curation.MyTopTagDTO
import com.linku.data.api.dto.server.curation.RecommendLinkDTO
import com.linku.data.api.dto.server.curation.SectionDTO
import com.linku.data.api.dto.server.curation.UnreadLinkDTO

object CurationMapper {

    fun CurationDetailDTO.toDomain(): CurationDetail = CurationDetail(
        curationId = curationId,
        title = month.toCurationTitle(),
        headerMent = headerMent,
        footerMent = footerMent,
    )

    fun SectionDTO.toDomain(): SectionItem = SectionItem(
        section = section,
        title = title,
        description = description,
        imageUrl = imageUrl
    )

    fun RecommendLinkDTO.toDomain(): RecommendLink = RecommendLink(
        userLinkuId = userLinkuId,
        title = title,
        url = url,
        imageUrl = imageUrl.orEmpty(),
        domain = domain.orEmpty(),
        domainImageUrl = domainImageUrl.orEmpty(),
        categories = categories.orEmpty(),
        type = if (type == "EXTERNAL") {
            LinkType.External(url)
        } else {
            LinkType.Internal(
                requireNotNull(userLinkuId) {
                    "내부 큐레이션 링크 응답에 userLinkuId가 없습니다."
                }
            )
        }
    )

    fun HistoryDTO.toDomain(): History = History(
        curationId = curationId,
        month = month,
        thumbnailUrl = thumbnailUrl
    )

    fun MyLatestCurationDTO.toDomain(): MyLatestCuration = MyLatestCuration(
        curationId = curationId,
        month = month,
        thumbnailUrl = thumbnailUrl
    )

    fun UnreadLinkDTO.toDomain(): UnreadLink = UnreadLink(
        userLinkuId = userLinkuId,
        title = title,
        domain = domain,
        domainImageUrl = domainImageUrl,
        linkuImageUrl = linkuImageUrl
    )

    fun MyTopTagDTO.toDomain(): MyTopTag = MyTopTag(
        name = name,
        percent = percent
    )

    fun KeywordDTO.toDomain(): KeyWord = KeyWord(
        name = name,
        count = count
    )
}
