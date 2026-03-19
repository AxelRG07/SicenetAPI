package com.example.sicenetapi.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sicenetapi.ui.screens.MainViewModel
import com.example.sicenetapi.ui.screens.AcademicDataScreen
import com.example.sicenetapi.ui.screens.CalifFinalScreen
import com.example.sicenetapi.ui.screens.CalifUnidadesScreen
import com.example.sicenetapi.ui.screens.CargaAcademicaScreen
import com.example.sicenetapi.ui.screens.KardexScreen
import com.example.sicenetapi.ui.screens.SicenetScreen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
)

@Composable
fun StudentBottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Inicio", HomeDestination.route, Icons.Default.Home),
        BottomNavItem("Horarios", CargaAcademicaDestination.route, Icons.Default.DateRange),
        BottomNavItem("Kardex", KardexDestination.route, Icons.Default.List),
        BottomNavItem("Finales", CalifFinalDestination.route, Icons.Default.Star),
        BottomNavItem("Parciales", CalifUnidadesDestination.route, Icons.Default.Edit)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTopAppBar(
    viewModel: MainViewModel = viewModel(),
    showBackButton: Boolean
) {
    TopAppBar(
        title = { Text("SiceNew") },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { viewModel.sincronizarTodo() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Volver atrás"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun SiceNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val sharedViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
    val startDestination = HomeDestination.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showNavigationBars = currentRoute != HomeDestination.route

    val showBackButton = currentRoute != AcademicDataDestination.route

    Scaffold(
        topBar = {
            if (showNavigationBars) {
                StudentTopAppBar(
                    showBackButton = showBackButton
                )
            }
        },
        bottomBar = {
            if (showNavigationBars) {
                StudentBottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(paddingValues)
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

            composable (
                route = KardexDestination.route
            ) {
                KardexScreen(
                    viewModel = sharedViewModel
                )
            }

            composable (
                route = CalifFinalDestination.route
            ) {
                CalifFinalScreen(
                    viewModel = sharedViewModel
                )
            }

            composable (
                route = CalifUnidadesDestination.route
            ) {
                CalifUnidadesScreen(
                    viewModel = sharedViewModel
                )
            }
        }
    }
}