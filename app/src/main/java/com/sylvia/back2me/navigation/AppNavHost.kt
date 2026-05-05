package com.sylvia.back2me.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sylvia.back2me.data.LostItemViewModel
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
    // 1. Create the shared ViewModel at the top level
    val itemViewModel: LostItemViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 2. Updated Home Route to use the shared ViewModel
        composable(ROUTE_HOME) {
            HomeScreen(navController, itemViewModel)
        }

        // 3. Added the Detail Route with the ID argument
        composable(
            route = "$ROUTE_ITEM_DETAILS/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(navController, itemId, itemViewModel)
        }

        // --- The rest of your routes remain exactly as they were ---
        composable(ROUTE_ADD_ITEM) {
            AddItemScreen(navController)
        }

        composable(ROUTE_SPLASH) {
            SplashScreen(navController)
        }

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
    }
}