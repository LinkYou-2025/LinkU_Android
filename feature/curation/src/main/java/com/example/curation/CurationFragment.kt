package com.example.curation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.curation.ui.CurationScreen

//Jetpack Compose 기반의 큐레이션 화면을 띄우는 Fragment
//CurationFragment.kt
//역할: Jetpack Compose 기반의 큐레이션 화면을 띄우는 Fragment
//
//기능: ComposeView를 통해 CurationScreen()을 보여줌
//
//추후 연동: ViewModel, Navigation Graph 등에 연결

class CurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CurationScreen()
            }
        }
    }
}