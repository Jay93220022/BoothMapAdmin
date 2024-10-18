package com.jay.boothmap.Navigation

sealed class Screen(val route:String) {
    object AddBoothScreen : Screen("HomeScreen")
    object ListScreen : Screen("listscreen")
    object EditScreen : Screen("editScreen/{cityName}/{boothId}/{boothName}")
        object  SplashScren:Screen("splash")// Updated route with parameters

}