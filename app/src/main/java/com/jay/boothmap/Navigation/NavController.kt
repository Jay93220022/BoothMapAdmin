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
        composable(Screen.ListScreen.route) {
            ListScreen(navController, listViewModel)
        }

        composable("editScreen?city={city}&boothId={boothId}&boothName={boothName}&bloName={bloName}&bloContact={bloContact}&district={district}&taluka={taluka}&latitude={latitude}&longitude={longitude}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("city") ?: ""
            val boothId = backStackEntry.arguments?.getString("boothId") ?: ""
            val boothName = backStackEntry.arguments?.getString("boothName") ?: ""
            val bloName = backStackEntry.arguments?.getString("bloName") ?: ""
            val bloContact = backStackEntry.arguments?.getString("bloContact") ?: ""
            val district = backStackEntry.arguments?.getString("district") ?: ""
            val taluka = backStackEntry.arguments?.getString("taluka") ?: ""
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0

            EditScreen(navController, editViewModel, cityName, boothId, boothName, bloName, bloContact, district, taluka, latitude, longitude)
        }


        composable(Screen.AddBoothScreen.route) {
            AddBoothScreen(navController, addBoothViewModel)
        }
    }

}
