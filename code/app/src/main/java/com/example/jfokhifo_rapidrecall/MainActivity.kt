package com.example.jfokhifo_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.jfokhifo_rapidrecall.ui.theme.JfokhifoRapidRecallTheme

/**
Android launcher activity for RapidRecall. Sets up edge-to-edge
display, applies the app theme, and hosts the root Compose UI
while leaving game flow to RapidRecallApp and RapidRecallController.
No known outstanding issues.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JfokhifoRapidRecallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RapidRecallApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}