package com.jay.boothmap.Navigation

sealed class Screen(val route:String) {
    object AddBoothScreen : Screen("HomeScreen")
    object ListScreen : Screen("listscreen")
    object EditScreen : Screen("editscreen")

}