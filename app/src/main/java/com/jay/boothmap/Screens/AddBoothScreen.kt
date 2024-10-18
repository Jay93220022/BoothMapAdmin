package com.jay.boothmap.Screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.R
import com.jay.boothmap.Repositories.PermissionHandler
import com.jay.boothmap.Viewmodels.AddBoothViewModel
import kotlinx.coroutines.launch
import java.io.File

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
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }



    //Taking permissions
    val permissionHandler = remember { PermissionHandler() }
    val context = LocalContext.current

    // Permission request composable
    permissionHandler.RequestPermissions(context)


    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val districts = stringArrayResource(id = R.array.maharashtra_districts)

    val eciOrange = Color(0xFFF26522)
    val eciGreen = Color(0xFF017A3E)
    val eciWhite = Color.White

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }


    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // If the picture was taken successfully, set the temporary URI as the actual image URI
            tempImageUri?.let {
                imageUri = it
                bitmap = null  // Clear any existing bitmap
            }
        }
    }




    // Function to get current location
    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
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
            .background(eciWhite)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add New Booth",
            style = MaterialTheme.typography.headlineMedium,
            color = eciOrange,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(10.dp)
        )

        // Input fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Booth Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Booth Id") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange,

            ),
                    singleLine = true
        )

        OutlinedTextField(
            value = bloname,
            onValueChange = { bloname = it },
            label = { Text("BLO Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = bloContact,
            onValueChange = { bloContact = it },
            label = { Text("BLO Contact") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange
            ), singleLine = true
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
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciOrange,
                    focusedLabelColor = eciOrange,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = eciOrange
                ), singleLine = true
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                districts.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item, color = eciGreen) },
                        onClick = {
                            district = item
                            expanded = false
                        },

                    )
                }
            }
        }

        OutlinedTextField(
            value = taluka,
            onValueChange = { taluka = it },
            label = { Text("Taluka") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange
            ), singleLine = true
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = eciOrange,
                focusedLabelColor = eciOrange,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = eciOrange
            ), singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = {},
                label = { Text("Latitude") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciOrange,
                    focusedLabelColor = eciOrange,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = eciOrange
                ), singleLine = true
            )

            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = {},
                label = { Text("Longitude") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = eciOrange,
                    focusedLabelColor = eciOrange,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = eciOrange
                ), singleLine = true
            )
        }

        // Image selection buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (permissionHandler.hasStoragePermissions(context)) {
                        galleryLauncher.launch("image/*")
                    } else {
                        Toast.makeText(context, "Storage permission required", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = eciGreen)
            ) {
                Text("Select Image", color = eciWhite)
            }

            Button(
                onClick = {
                    if (permissionHandler.hasCameraPermission(context)) {
                        val file = createImageFile(context)
                        file?.let {
                            tempImageUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                it
                            )
                            tempImageUri?.let { uri ->
                                cameraLauncher.launch(uri)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = eciGreen)
            ) {
                Text("Capture Image", color = eciWhite)
            }
        }

// Update the image display code:
        imageUri?.let { uri ->
            LaunchedEffect(uri) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    bitmap = BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    Log.e("AddBoothScreen", "Error decoding image: ${e.message}")
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }

            bitmap?.let { img ->
                Image(
                    bitmap = img.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, eciOrange, RoundedCornerShape(8.dp))
                )
            }
        }


        imageUri?.let { uri ->
            Log.d("AddBoothScreen", "Image URI: $uri")

            // Use LaunchedEffect to handle the image decoding
            LaunchedEffect(uri) {
                bitmap = try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    Log.e("AddBoothScreen", "Error decoding image: ${e.message}")
                    null // Return null if there's an error
                }
            }

            bitmap?.let { img ->
                Image(
                    bitmap = img.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, eciOrange, RoundedCornerShape(8.dp))
                )
            } ?: run {
                Log.e("AddBoothScreen", "Failed to decode image: Bitmap is null")
//                Toast.makeText(context, "Failed to decode image", Toast.LENGTH_SHORT).show()
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                color = eciOrange,
                modifier = Modifier.padding(16.dp)
            )
        }else {
            Button(
                onClick = {
                    if (name.isEmpty() || bloname.isEmpty() || bloContact.isEmpty() || district.isEmpty() ||
                        taluka.isEmpty() || city.isEmpty()
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill in all the required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Show loading indicator
                        scope.launch {
                            isLoading = true
                            try {
                                // Create the new booth
                                val newBooth = Booth(
                                    id = id,
                                    name = name,
                                    bloName = bloname,
                                    bloContact = bloContact,
                                    district = district,
                                    taluka = taluka,
                                    latitude = latitude,
                                    longitude = longitude,
                                    city = city,
                                    imageUri = "" // This will be updated by Firebase
                                )

                                // Add booth with image
                                viewModel.addBoothWithImage(newBooth, imageUri) {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Booth Added Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.ListScreen.route)
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = eciOrange,
                    contentColor = eciWhite
                )
            ) {
                Text("Add Booth", fontSize = 18.sp)
            }
        }
    }
}

// Function to create a temporary image file
fun createImageFile(context: Context): File? {
    val storageDir = context.getExternalFilesDir(null)
    return try {
        File.createTempFile("temp_image_", ".jpg", storageDir).apply {
            createNewFile()
        }
    } catch (e: Exception) {
        Log.e("AddBoothScreen", "Error creating image file: ${e.message}")
        null
    }
}