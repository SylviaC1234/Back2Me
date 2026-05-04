package com.sylvia.back2me.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.ui.screens.add.AddItemScreen
import com.sylvia.back2me.ui.screens.auth.LoginScreen
import com.sylvia.back2me.ui.screens.auth.RegisterScreen
import com.sylvia.back2me.ui.screens.details.ItemDetailScreen
import com.sylvia.back2me.ui.screens.home.HomeScreen
import com.sylvia.back2me.ui.screens.posts.PostScreen
import com.sylvia.back2me.ui.screens.profile.ProfileScreen
import com.sylvia.sokohub.ui.screens.splash.SplashScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_HOME) {
            HomeScreen(navController)
        }

        composable(ROUTE_ADD_ITEM) {
            AddItemScreen(navController) }


        composable(ROUTE_SPLASH) {
            SplashScreen(navController) }


        composable(ROUTE_POST) {
            PostScreen(navController)
        }

        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }

        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }

        composable(ROUTE_PROFILE) {
            ProfileScreen(navController)
        }

        composable("$ROUTE_ITEM_DETAILS/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(navController, itemId)
        }









    }
}