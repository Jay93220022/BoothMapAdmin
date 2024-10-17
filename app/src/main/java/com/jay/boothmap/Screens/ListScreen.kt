package com.jay.boothmap.Screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear

import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Dataclasses.City
import com.jay.boothmap.FirebaseSource
import com.jay.boothmap.Navigation.Screen
import com.jay.boothmap.Repositories.BoothRepository
import com.jay.boothmap.Viewmodels.ListViewModel

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
        }
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
        title = { Text("Booth Map") },
        actions = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search cities or booths") },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                }
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AddBoothButton(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Button(
            onClick = { navController.navigate(Screen.AddBoothScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Add New Booth")
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
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
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add a new booth to get started",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CitiesList(
    cities: List<City>,
    navController: NavController,
    viewModel: ListViewModel  // Add viewModel parameter
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
                viewModel = viewModel  // Pass the existing viewModel
            )
        }
    }
}
@Composable
private fun CityCard(city: City, navController: NavController, viewModel: ListViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = {
                        expanded = !expanded
                        if (expanded && !city.isBoothFetched) {
                            viewModel.fetchBoothsForCity(city.name)
                        }
                    }
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
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        !city.isBoothFetched -> {
                            viewModel.fetchBoothsForCity(city.name)
                        }
                        else -> {
                            city.booths.forEach { booth ->
                                BoothItem(booth = booth, navController = navController)
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
private fun BoothItem(booth: Booth, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            navController.navigate("${Screen.EditScreen.route}/${booth.city}/${booth.name}")
        }
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "BLO: ${booth.bloName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Contact: ${booth.bloContact}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (booth.latitude != 0.0 && booth.longitude != 0.0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${booth.latitude}, ${booth.longitude}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    navController.navigate("${Screen.EditScreen.route}/${booth.city}/${booth.id}")
                }
            ) {
                Text("Edit")
            }
        }
    }
}