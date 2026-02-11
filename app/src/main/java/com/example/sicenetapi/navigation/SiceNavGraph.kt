package com.example.sicenetapi.navigation

import android.R.attr.type
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sicenetapi.ui.screens.AcademicDataScreen
import com.example.sicenetapi.ui.screens.SicenetScreen

@Composable
fun SiceNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val startDestination = HomeDestination.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            SicenetScreen(navController = navController)
        }

        composable (
            route = "${AcademicDataDestination.route}/{matricula}/{password}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ){ backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val passwordEncoded = backStackEntry.arguments?.getString("password") ?: ""

            val password = try {
                Log.e("contra", "decodificando pass la contraseña")
                String(
                    Base64.decode(passwordEncoded, Base64.NO_WRAP or Base64.URL_SAFE)
                )
            } catch ( e: Exception ) {
                ""
                //Log.e("MainActivity", "Error al decodificar la contraseña", e)
            }

            AcademicDataScreen(matricula, password)

        }

    }
}