package com.linku.curation

import androidx.lifecycle.ViewModel
import com.linku.core.repository.CurationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurationDetailViewModel @Inject constructor(
    private val repo: CurationRepository
) : ViewModel() {

}
