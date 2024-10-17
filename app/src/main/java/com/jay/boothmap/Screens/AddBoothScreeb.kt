package com.jay.boothmap.Screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.R
import com.jay.boothmap.Viewmodels.AddBoothViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoothScreen(navController: NavController, viewModel: AddBoothViewModel) {
    var name by remember { mutableStateOf("") }
    var bloname by remember { mutableStateOf("") }
    var bloContact by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluka by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var id by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val districts = stringArrayResource(id = R.array.maharashtra_districts)

    // Function to get current location
    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                }
            }
    }

    // Call to get the current location when the composable is first called
    LaunchedEffect(Unit) {
        getCurrentLocation()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Booth Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Booth Id") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bloname,
            onValueChange = { bloname = it },
            label = { Text("BLO Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bloContact,
            onValueChange = { bloContact = it },
            label = { Text("BLO Contact") },
            modifier = Modifier.fillMaxWidth()
        )

        // District Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = district,
                onValueChange = {},
                readOnly = true,
                label = { Text("District") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                districts.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            district = item
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = taluka,
            onValueChange = { taluka = it },
            label = { Text("Taluka") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = {  },
                label = { Text("Latitude") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = {  },
                label = { Text("Longitude") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        Button(
            onClick = {
                val newBooth = Booth(
                    id = id,
                    name = name,
                    bloName = bloname,
                    bloContact = bloContact,
                    district = district,
                    taluka = taluka,
                    latitude = latitude,
                    longitude = longitude,
                    city = city
                )
                viewModel.addBooth(newBooth) {
                    Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.ListScreen.route)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.Black
            )
        ) {
            Text("Add Booth")
        }
    }
}