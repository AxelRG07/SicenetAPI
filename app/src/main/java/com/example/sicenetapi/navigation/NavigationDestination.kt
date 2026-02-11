package com.example.sicenetapi.navigation

interface NavigationDestination {
    val route: String
    val titleRes: String
}

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = "Home"
}

object AcademicDataDestination : NavigationDestination {
    override val route = "academic_data"
    override val titleRes = "Academic Data"
}