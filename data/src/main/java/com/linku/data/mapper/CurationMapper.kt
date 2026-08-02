package com.linku.data.mapper

import com.linku.core.model.curation.CurationDetail
import com.linku.core.model.curation.History
import com.linku.core.model.curation.JobKeyWord
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
        mentReady = mentReady
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
        imageUrl = imageUrl,
        domain = domain,
        domainImageUrl = domainImageUrl,
        categories = categories,
        type = LinkType.from(type)
    )

    fun HistoryDTO.toDomain(): History = History(
        curationId = curationId,
        title = month.toCurationYear(),
        thumbnailUrl = thumbnailUrl
    )

    fun MyLatestCurationDTO.toDomain(): MyLatestCuration = MyLatestCuration(
        curationId = curationId,
        month = month,
        thumbnailUrl = thumbnailUrl
    )

    fun UnreadLinkDTO.toDomain(): UnreadLink = UnreadLink(
        title = title,
        domain = domain,
        domainImageUrl = domainImageUrl,
        linkuImageUrl = linkuImageUrl
    )

    fun MyTopTagDTO.toDomain(): MyTopTag = MyTopTag(
        name = name,
        percent = percent
    )

    fun KeywordDTO.toDomain(): JobKeyWord = JobKeyWord(
        name = name,
        count = count
    )
}
