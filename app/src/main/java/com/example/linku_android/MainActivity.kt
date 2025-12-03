package com.example.linku_android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.login.auth.AnimatedLoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        intent?.data?.let { Log.d("DEEPLINK", "onCreate uri = $it") }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //enableEdgeToEdge()
        setContent {
            MainApp(
                viewModel = hiltViewModel()
            )
        }


    }
}