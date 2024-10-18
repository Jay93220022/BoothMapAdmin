package com.jay.boothmap.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Dataclasses.City
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.Viewmodels.ListViewModel

// Define ECI colors
val EciOrange = Color(0xFFF26522)
val EciGreen = Color(0xFF017A3E)
val EciWhite = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController, viewModel: ListViewModel) {
    Scaffold(
        topBar = {
            ListScreenTopBar(
                searchQuery = viewModel.searchQuery.value,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onRefresh = viewModel::refreshData
            )
        },
        bottomBar = {
            AddBoothButton(navController)
        },
        containerColor = EciWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                viewModel.isLoading.value -> {
                    LoadingIndicator()
                }
                viewModel.errorMessage.value != null -> {
                    ErrorMessage(
                        message = viewModel.errorMessage.value ?: "",
                        onRetry = viewModel::refreshData
                    )
                }
                viewModel.filteredCities.value.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    CitiesList(
                        cities = viewModel.filteredCities.value,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListScreenTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = { Text("Booth Map", color = EciWhite) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),

        actions = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search cities or booths", color = EciOrange.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(

                    cursorColor = EciGreen,
                    focusedBorderColor = EciGreen,
                    unfocusedBorderColor = EciGreen.copy(alpha = 0.7f)
                ),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Clear search", tint = EciWhite)
                        }
                    }
                }
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, "Refresh", tint = Color.Black)
            }
        },
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    )
}

@Composable
private fun AddBoothButton(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = { navController.navigate(Screen.AddBoothScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EciOrange)
        ) {
            Text("Add New Booth", color = EciWhite)
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = EciOrange)
    }
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Clear,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = EciOrange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = EciOrange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = EciGreen)
        ) {
            Text("Retry", color = EciWhite)
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No booths found",
            style = MaterialTheme.typography.headlineMedium,
            color = EciOrange
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add a new booth to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = EciGreen
        )
    }
}

@Composable
private fun CitiesList(
    cities: List<City>,
    navController: NavController,
    viewModel: ListViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(cities) { city ->
            CityCard(
                city = city,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun CityCard(city: City, navController: NavController, viewModel: ListViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EciWhite),
        shape = RoundedCornerShape(8.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = EciGreen
                )
                TextButton(
                    onClick = {
                        expanded = !expanded
                        if (expanded && !city.isBoothFetched) {
                            viewModel.fetchBoothsForCity(city.name)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = EciOrange)
                ) {
                    Text(if (expanded) "Hide Booths" else "Show Booths")
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when {
                        city.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = EciOrange
                            )
                        }
                        !city.isBoothFetched -> {
                            viewModel.fetchBoothsForCity(city.name)
                        }
                        else -> {
                            city.booths.forEach { booth ->
                                BoothItem(city = city.name, booth = booth, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoothItem(city: String, booth: Booth, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EciWhite),
        shape = RoundedCornerShape(8.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = booth.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = EciGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "BLO: ${booth.bloName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Contact: ${booth.bloContact}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                if (booth.latitude != 0.0 && booth.longitude != 0.0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp),
                            tint = EciOrange
                        )
                        Text(
                            text = "${booth.latitude}, ${booth.longitude}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            TextButton(
                onClick = {
                    navController.navigate("editScreen?city=${city}&boothId=${booth.id}&boothName=${booth.name}&bloName=${booth.bloName}&bloContact=${booth.bloContact}&district=${booth.district}&taluka=${booth.taluka}&latitude=${booth.latitude}&longitude=${booth.longitude}")
                },
                colors = ButtonDefaults.textButtonColors(contentColor = EciOrange)
            ) {
                Text("Edit")
            }
        }
    }
}