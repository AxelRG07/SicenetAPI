package com.example.sicenetapi.navigation

import android.R.attr.type
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sicenetapi.ui.MainViewModel
import com.example.sicenetapi.ui.screens.AcademicDataScreen
import com.example.sicenetapi.ui.screens.SicenetScreen

@Composable
fun SiceNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val sharedViewModel: MainViewModel = viewModel()
    val startDestination = HomeDestination.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            SicenetScreen(
                viewModel = sharedViewModel,
                onLoginSuccess = {
                    navController.navigate(AcademicDataDestination.route)
                }
            )
        }

        composable (
            route = AcademicDataDestination.route
        ){

            AcademicDataScreen(viewModel = sharedViewModel)

        }

    }
}