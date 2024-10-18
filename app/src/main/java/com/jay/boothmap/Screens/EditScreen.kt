package com.jay.boothmap.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Viewmodels.EditViewModel
@Composable
fun EditScreen(
    navController: NavController,
    viewModel: EditViewModel,
    cityName: String,
    boothId: String,
    boothName: String,
    bloName: String,
    bloContact:String,
    district:String,
    taluka:String,
    latitude:Double,
    longitude:Double
) {
    val uiState = viewModel.uiState
    val booth = navController.previousBackStackEntry?.arguments?.getParcelable<Booth>("booth")

    //if (booth == null) {
        //CircularProgressIndicator() // Handle empty state if needed
        //return
    //}

    // State variables
    var name by remember { mutableStateOf(boothName) }
    var id by remember { mutableStateOf(boothId) }
    var bloName by remember { mutableStateOf(bloName) }
    var bloContact by remember { mutableStateOf(bloContact) }
    var district by remember { mutableStateOf(district) }
    var taluka by remember { mutableStateOf(taluka) }
    var city by remember { mutableStateOf(cityName) }
    var isLoading by remember { mutableStateOf(false) }

    // Regular form content
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.updateField("name", it)
            },
            label = { Text("Booth Name") }
        )

        OutlinedTextField(
            value = id,
            onValueChange = {
                id = it
                viewModel.updateField("id", it)
            },
            label = { Text("Booth ID") }
        )

        OutlinedTextField(
            value = bloName,
            onValueChange = {
                bloName = it
                viewModel.updateField("bloName", it)
            },
            label = { Text("BLO Name") }
        )

        OutlinedTextField(
            value = bloContact,
            onValueChange = {
                bloContact = it
                viewModel.updateField("bloContact", it)
            },
            label = { Text("BLO Contact") }
        )

        OutlinedTextField(
            value = district,
            onValueChange = {
                district = it
                viewModel.updateField("district", it)
            },
            label = { Text("District") }
        )

        OutlinedTextField(
            value = taluka,
            onValueChange = {
                taluka = it
                viewModel.updateField("taluka", it)
            },
            label = { Text("Taluka") }
        )

        OutlinedTextField(
            value = city,
            onValueChange = {
                city = it
                viewModel.updateField("city", it)
            },
            label = { Text("City") }
        )

        // Update button
        Button(onClick = {
            // Validate fields
            if (name.isEmpty() || id.isEmpty() || bloName.isEmpty() || bloContact.isEmpty() || district.isEmpty() || taluka.isEmpty() || city.isEmpty()) {
                // Handle error (e.g., show a Toast or Snackbar)
                return@Button
            }

            // Set loading state
            isLoading = true

            // Create the updated Booth object
            val updatedBooth = Booth(
                id = id,
                city = city,
                name = name,
                bloName = bloName,
                bloContact = bloContact,
                district = district,
                taluka = taluka,
                latitude = latitude, // Keep latitude unchanged
                longitude =  longitude // Keep longitude unchanged
            )

            // Call the update function
            viewModel.addBooth(updatedBooth)
            isLoading = false // Reset loading state
            navController.popBackStack() // Navigate back after successful update
        }) {
            if (isLoading) {
                CircularProgressIndicator() // Show loading indicator
            } else {
                Text("Update Booth")
            }
        }
    }
}
