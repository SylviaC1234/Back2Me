package com.sylvia.sokohub.ui.screens.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sylvia.back2me.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.navigation.ROUTE_REGISTER
import com.sylvia.back2me.ui.theme.newBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController){

    val coroutinescope = rememberCoroutineScope ()
    coroutinescope.launch {
        delay(3000)
        navController.navigate(ROUTE_REGISTER)




    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(newBlue),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center



    )
    {Image(
        painter = painterResource(R.drawable.icon),
        contentDescription = "img",
        modifier = Modifier.size(200.dp)
    )





    }


}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen(rememberNavController())
}