package com.example.sicenetapi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sicenetapi.ui.screens.MainViewModel
import com.example.sicenetapi.ui.screens.AcademicDataScreen
import com.example.sicenetapi.ui.screens.CargaAcademicaScreen
import com.example.sicenetapi.ui.screens.SicenetScreen

@Composable
fun SiceNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val sharedViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
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
                    navController.navigate(AcademicDataDestination.route) {
                        popUpTo(HomeDestination.route) { inclusive = true }
                    }

                }
            )
        }

        composable (
            route = AcademicDataDestination.route
        ){
            AcademicDataScreen(
                viewModel = sharedViewModel,
                onLogOut = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(AcademicDataDestination.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable (
            route = CargaAcademicaDestination.route
        ) {
            CargaAcademicaScreen(
                viewModel = sharedViewModel
            )
        }

    }
}