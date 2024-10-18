package com.jay.boothmap.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Viewmodels.EditViewModel
import androidx.compose.ui.res.stringArrayResource
import com.jay.boothmap.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    viewModel: EditViewModel,
    cityName: String,
    boothId: String,
    boothName: String,
    bloName: String,
    bloContact: String,
    district: String,
    taluka: String,
    latitude: Double,
    longitude: Double
) {
    val uiState = viewModel.uiState

    // State variables
    var name by remember { mutableStateOf(boothName) }
    var id by remember { mutableStateOf(boothId) }
    var blo by remember { mutableStateOf(bloName) }
    var contact by remember { mutableStateOf(bloContact) }
    var dist by remember { mutableStateOf(district) }
    var tal by remember { mutableStateOf(taluka) }
    var city by remember { mutableStateOf(cityName) }
    var isLoading by remember { mutableStateOf(false) }

    // State for district dropdown
    var expanded by remember { mutableStateOf(false) }
    val districts = stringArrayResource(id = R.array.maharashtra_districts)
    val eciOrange = Color(0xFFF26522)
    val eciGreen = Color(0xFF017A3E)
    val eciWhite = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(eciWhite)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Edit Booth",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = eciOrange
            ),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        @Composable
        fun StyledOutlinedTextField(
            value: String,
            onValueChange: (String) -> Unit,
            label: String,
            modifier: Modifier = Modifier,
            readOnly: Boolean
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    viewModel.updateField(label.lowercase(), it)
                },
                label = { Text(label) },
                modifier = modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciOrange,
                    focusedLabelColor = eciOrange,
                    unfocusedBorderColor = eciGreen,
                    unfocusedLabelColor = eciGreen,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = eciOrange
                ),
                shape = RoundedCornerShape(8.dp),
                readOnly = readOnly
            )
        }

        // Other fields
        StyledOutlinedTextField(name, { name = it }, "Booth Name", readOnly = true)
        StyledOutlinedTextField(id, { id = it }, "Booth ID", readOnly = false)
        StyledOutlinedTextField(blo, { blo = it }, "BLO Name", readOnly = false)
        StyledOutlinedTextField(contact, { contact = it }, "BLO Contact", readOnly = false)

        // District Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = dist,
                onValueChange = { },
                label = { Text("District") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciOrange,
                    focusedLabelColor = eciOrange,
                    unfocusedBorderColor = eciGreen,
                    unfocusedLabelColor = eciGreen,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                districts.forEach { district ->
                    DropdownMenuItem(
                        text = { Text(district, color = eciGreen) },
                        onClick = {
                            dist = district
                            expanded = false
                        }
                    )
                }
            }
        }

        StyledOutlinedTextField(tal, { tal = it }, "Taluka", readOnly = false)
        StyledOutlinedTextField(city, { city = it }, "City", readOnly = false)

        // Location display (read-only)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = { },
                label = { Text("Latitude") },
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciGreen,
                    unfocusedBorderColor = eciGreen,
                    focusedLabelColor = eciGreen,
                    unfocusedLabelColor = eciGreen,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = { },
                label = { Text("Longitude") },
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciGreen,
                    unfocusedBorderColor = eciGreen,
                    focusedLabelColor = eciGreen,
                    unfocusedLabelColor = eciGreen,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Update button
        Button(
            onClick = {
                if (name.isEmpty() || id.isEmpty() || blo.isEmpty() || contact.isEmpty() ||
                    dist.isEmpty() || tal.isEmpty() || city.isEmpty()) {
                    // Show error message (e.g., using Snackbar or Toast)
                    return@Button
                }

                isLoading = true
                val updatedBooth = Booth(
                    id = id,
                    city = city,
                    name = name,
                    bloName = blo,
                    bloContact = contact,
                    district = dist,
                    taluka = tal,
                    latitude = latitude,
                    longitude = longitude
                )

                viewModel.addBooth(updatedBooth)
                isLoading = false
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = eciOrange,
                contentColor = eciWhite
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = eciWhite)
            } else {
                Text("Update Booth", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
