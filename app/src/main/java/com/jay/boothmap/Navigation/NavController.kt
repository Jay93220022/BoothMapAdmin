package com.jay.boothmap.Navigation



import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jay.boothmap.Screens.AddBoothScreen
import com.jay.boothmap.Screens.EditScreen
import com.jay.boothmap.Screens.ListScreen
import com.jay.boothmap.Viewmodels.AddBoothViewModel
import com.jay.boothmap.Viewmodels.EditViewModel
import com.jay.boothmap.Viewmodels.ListViewModel



@Composable
fun Navigation(
    addBoothViewModel: AddBoothViewModel,
    editViewModel: EditViewModel,
    listViewModel: ListViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ListScreen.route) {
        composable(Screen.ListScreen.route) { ListScreen(navController, listViewModel) }
        composable(
            "${Screen.EditScreen.route}/{cityName}/{boothId}",
            arguments = listOf(
                navArgument("cityName") { type = NavType.StringType },
                navArgument("boothId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val boothId = backStackEntry.arguments?.getString("boothId") ?: ""
            val  boothName:String =backStackEntry.arguments?.getString("name")?:""
            EditScreen(navController, editViewModel, boothId, cityName, boothName)
        }
        composable(Screen.AddBoothScreen.route) { AddBoothScreen(navController, addBoothViewModel) }

    }
}
