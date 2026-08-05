package com.linku.data.mapper

import com.linku.core.model.search.LinkuSearchInfo
import com.linku.data.api.dto.search.LinkuSearchItemResponseDTO

internal fun LinkuSearchItemResponseDTO.toDomain(): LinkuSearchInfo =
    LinkuSearchInfo(
        userLinkuId = userLinkuId,
        linkuId = linkuId,
        title = title,
        linkuImageUrl = linkuImageUrl,
        tags = tags.orEmpty(),
        domainImageUrl = domainImageUrl,
        domainName = domainName,
    )
