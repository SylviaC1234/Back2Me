package com.sylvia.back2me

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sylvia.back2me.data.CloudinaryManager
import com.sylvia.back2me.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CloudinaryManager.init(this)
        enableEdgeToEdge()
        setContent {
            AppNavHost()


        }
    }
}





