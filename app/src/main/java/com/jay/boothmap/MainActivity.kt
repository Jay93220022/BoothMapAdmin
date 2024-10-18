package com.jay.boothmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jay.boothmap.Navigation.Navigation
import com.jay.boothmap.Repositories.AuthRepository
import com.jay.boothmap.Repositories.BoothRepository
import com.jay.boothmap.ui.theme.BoothMapTheme
import com.jay.boothmap.Viewmodels.AddBoothViewModel
import com.jay.boothmap.Viewmodels.AuthViewModel
import com.jay.boothmap.Viewmodels.EditViewModel
import com.jay.boothmap.Viewmodels.ListViewModel

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BoothMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the initialized view models to the Navigation Composable
                    Navigation(
                        addBoothViewModel = AddBoothViewModel(BoothRepository(FirebaseSource())),
                        editViewModel = EditViewModel(BoothRepository(FirebaseSource())),
                        listViewModel = ListViewModel(BoothRepository(FirebaseSource())),
                        authViewModel = AuthViewModel(AuthRepository())
                    )
                }
            }
        }
    }
}
