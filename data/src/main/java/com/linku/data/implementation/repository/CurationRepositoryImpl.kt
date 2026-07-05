package com.linku.data.implementation.repository

import com.linku.core.repository.CurationRepository
import com.linku.data.api.CurationApi
import javax.inject.Inject


/** princeHw 작업 공간  */
class CurationRepositoryImpl @Inject constructor(
    private val curationApi: CurationApi
) : CurationRepository {


}
