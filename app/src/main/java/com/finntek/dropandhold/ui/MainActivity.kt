package com.finntek.dropandhold.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finntek.dropandhold.ui.navigation.DropAndHoldNavHost
import com.finntek.dropandhold.ui.theme.DropAndHoldTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DropAndHoldTheme {
                DropAndHoldNavHost()
            }
        }
    }
}
