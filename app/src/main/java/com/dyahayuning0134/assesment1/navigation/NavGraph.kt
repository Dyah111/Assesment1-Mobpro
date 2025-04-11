package com.dyahayuning0134.assesment1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dyahayuning0134.assesment1.MainScreen
import com.dyahayuning0134.assesment1.screen.AboutScreen
import com.dyahayuning0134.assesment1.screen.ProfileScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}