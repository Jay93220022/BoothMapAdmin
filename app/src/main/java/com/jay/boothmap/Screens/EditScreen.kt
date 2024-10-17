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
fun EditScreen(navController: NavController, viewModel: EditViewModel, cityName: String, boothId: String,boothName:String) {
    val uiState = viewModel.uiState
    var name by remember { mutableStateOf("") }
    var bloname by remember { mutableStateOf("") }
    var bloContact by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluka by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var id by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchBoothById(cityName, boothId,boothName)
    }

    // Handle loading state
    if (uiState.isLoading) {
        CircularProgressIndicator()
        return
    }

    // Handle error state


    // Regular form content
    Column {
        OutlinedTextField(
            value = uiState.boothFields.name,
            onValueChange = { viewModel.updateField("name", it) },
            label = { Text("Booth Name") }
        )

        OutlinedTextField(
            value=uiState.boothFields.id,
            onValueChange = { viewModel.updateField("id",it)},
            label = { Text(text = "Booth ID") }
        )

       OutlinedTextField(
           value=uiState.boothFields.bloName,
           onValueChange = { viewModel.updateField("bloName",it)},
           label = { Text(text = "BLO Name") }
       )

        OutlinedTextField(
            value=uiState.boothFields.bloContact,
            onValueChange = { viewModel.updateField("bloContact",it)},
            label = { Text(text = "BLO Contact") }
        )

        OutlinedTextField(
            value=uiState.boothFields.district,
            onValueChange = { viewModel.updateField("district",it)},
            label = { Text(text = "District") }
        )

        OutlinedTextField(
            value=uiState.boothFields.taluka,
            onValueChange = { viewModel.updateField("taluka",it)},
            label = { Text(text = "Taluka") }
        )
        OutlinedTextField(
            value=uiState.boothFields.city,
            onValueChange = { viewModel.updateField("city",it)},
            label = { Text(text = "City") }
        )



    }
}