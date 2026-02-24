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

object CargaAcademicaDestination : NavigationDestination {
    override val route = "carga_academica"
    override val titleRes = "Carga Académica"

}

object KardexDestination : NavigationDestination {
    override val route = "kardex"
    override val titleRes = "Kardex"
}

object CalifFinalDestination : NavigationDestination {
    override val route = "calif_final"
    override val titleRes = "Calificaciones Finales"
}