package com.finnvek.dropandhold.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finnvek.dropandhold.ui.navigation.DropAndHoldNavHost
import com.finnvek.dropandhold.ui.theme.DropAndHoldTheme
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
