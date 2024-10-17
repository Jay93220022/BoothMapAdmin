package com.jay.boothmap.Viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.boothmap.Dataclasses.City
import com.jay.boothmap.Repositories.BoothRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel(private val repository: BoothRepository) : ViewModel() {
    private val _cities = mutableStateOf<List<City>>(emptyList())
    val cities: State<List<City>> = _cities

    private val _filteredCities = mutableStateOf<List<City>>(emptyList())
    val filteredCities: State<List<City>> = _filteredCities

    private var _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private var searchJob: Job? = null

    init {
        fetchCities()
    }

    private fun fetchCities() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val fetchedCities = repository.fetchCities()
                _cities.value = fetchedCities
                _filteredCities.value = fetchedCities
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch cities. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBoothsForCity(cityName: String) {
        viewModelScope.launch {
            try {
                val currentCities = _cities.value.toMutableList()
                val cityIndex = currentCities.indexOfFirst { it.name == cityName }

                if (cityIndex != -1) {
                    // Set loading state
                    currentCities[cityIndex] = currentCities[cityIndex].copy(
                        isBoothFetched = false,
                        isLoading = true  // Set loading to true
                    )
                    _cities.value = currentCities.toList()
                    _filteredCities.value = filterCitiesByQuery(_searchQuery.value)

                    // Fetch booths
                    val fetchedBooths = repository.fetchBoothsForCity(cityName)

                    // Update with fetched booths
                    val updatedCities = _cities.value.toMutableList()
                    updatedCities[cityIndex] = updatedCities[cityIndex].copy(
                        booths = fetchedBooths,
                        isBoothFetched = true,
                        isLoading = false  // Set loading to false
                    )
                    _cities.value = updatedCities.toList()
                    _filteredCities.value = filterCitiesByQuery(_searchQuery.value)
                }
            } catch (e: Exception) {
                // Handle error and reset loading state
                val currentCities = _cities.value.toMutableList()
                val cityIndex = currentCities.indexOfFirst { it.name == cityName }
                if (cityIndex != -1) {
                    currentCities[cityIndex] = currentCities[cityIndex].copy(
                        isLoading = false
                    )
                    _cities.value = currentCities.toList()
                    _filteredCities.value = filterCitiesByQuery(_searchQuery.value)
                }
                _errorMessage.value = "Failed to fetch booths. Please try again."
            }
        }
    }

    private fun filterCitiesByQuery(query: String): List<City> {
        val trimmedQuery = query.trim().lowercase()
        return if (trimmedQuery.isEmpty()) {
            _cities.value
        } else {
            _cities.value.filter { city ->
                city.name.lowercase().contains(trimmedQuery) ||
                        city.booths.any { booth ->
                            booth.name.lowercase().contains(trimmedQuery) ||
                                    booth.bloName.lowercase().contains(trimmedQuery) ||
                                    booth.district.lowercase().contains(trimmedQuery) ||
                                    booth.taluka.lowercase().contains(trimmedQuery)
                        }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _filteredCities.value = filterCitiesByQuery(newQuery)
        }
    }

    fun refreshData() {
        fetchCities()
    }
}