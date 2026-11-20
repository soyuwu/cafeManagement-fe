package com.se114.cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.se114.cafe.navigation.AppNavGraph
import com.se114.cafe.ui.theme.CafeAppBg
import com.se114.cafe.ui.theme.CafeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CafeAppBg
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
