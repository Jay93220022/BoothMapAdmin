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
fun EditScreen(navController: NavController, viewModel: EditViewModel, cityName: String, boothId: String, boothName: String) {
    val uiState = viewModel.uiState

    // Initialize fields with existing values
    var name by remember { mutableStateOf(uiState.boothFields.name) }
    var id by remember { mutableStateOf(uiState.boothFields.id) }
    var bloName by remember { mutableStateOf(uiState.boothFields.bloName) }
    var bloContact by remember { mutableStateOf(uiState.boothFields.bloContact) }
    var district by remember { mutableStateOf(uiState.boothFields.district) }
    var taluka by remember { mutableStateOf(uiState.boothFields.taluka) }
    var city by remember { mutableStateOf(uiState.boothFields.city) }

    // Fetch booth details when screen is launched
    LaunchedEffect(Unit) {
        viewModel.fetchBoothById(cityName, boothId, boothName)
    }

    // Handle loading state
    if (uiState.isLoading) {
        CircularProgressIndicator()
        return
    }

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
            label = { Text(text = "Booth ID") }
        )

        OutlinedTextField(
            value = bloName,
            onValueChange = {
                bloName = it
                viewModel.updateField("bloName", it)
            },
            label = { Text(text = "BLO Name") }
        )

        OutlinedTextField(
            value = bloContact,
            onValueChange = {
                bloContact = it
                viewModel.updateField("bloContact", it)
            },
            label = { Text(text = "BLO Contact") }
        )

        OutlinedTextField(
            value = district,
            onValueChange = {
                district = it
                viewModel.updateField("district", it)
            },
            label = { Text(text = "District") }
        )

        OutlinedTextField(
            value = taluka,
            onValueChange = {
                taluka = it
                viewModel.updateField("taluka", it)
            },
            label = { Text(text = "Taluka") }
        )

        OutlinedTextField(
            value = city,
            onValueChange = {
                city = it
                viewModel.updateField("city", it)
            },
            label = { Text(text = "City") }
        )

        // Update button
        Button(onClick = {
            // Create the updated Booth object with current field values
            val updatedBooth = Booth(
                id = id,
                city = city,
                name = name,
                bloName = bloName,
                bloContact = bloContact,
                district = district,
                taluka = taluka,
                latitude = uiState.boothFields.latitude, // Keeping latitude unchanged
                longitude = uiState.boothFields.longitude // Keeping longitude unchanged
            )
            // Call the update function
            viewModel.updateBooth(cityName, boothName, updatedBooth)
            navController.popBackStack() // Navigate back after successful update
        }) {
            Text("Update Booth")
        }
    }
}
