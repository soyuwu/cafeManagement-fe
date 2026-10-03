package com.se114.cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.se114.cafe.feature.auth.LoginScreen
import com.se114.cafe.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }
}