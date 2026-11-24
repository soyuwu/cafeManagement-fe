package com.se114.cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import com.se114.cafe.navigation.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(Modifier.safeDrawingPadding()) {
                AppNavGraph()
            }
        }
    }
}
