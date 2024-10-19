package com.jay.boothmap.Viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.boothmap.Dataclasses.Booth
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

    private val _isUploading = mutableStateOf(false)
    val isUploading: State<Boolean> = _isUploading

    private val _uploadProgress = mutableFloatStateOf(0f)
    val uploadProgress: State<Float> = _uploadProgress

    private val _uploadMessage = mutableStateOf("")
    val uploadMessage: State<String> = _uploadMessage

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

    private val _deleteStatus = MutableLiveData<Result<Boolean>>()
    val deleteStatus: LiveData<Result<Boolean>> = _deleteStatus

    // Existing methods...

    fun deleteBooth(city: String, boothName: String) {
        viewModelScope.launch {
            try {
                repository.deleteBooth(city, boothName)
                _deleteStatus.value = Result.success(true)
                refreshData()
            } catch (e: Exception) {
                _deleteStatus.value = Result.failure(e)
            }
        }
    }


    fun uploadBoothsFromExcel(booths: List<Booth>, context: Context) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                _uploadMessage.value = "Preparing to upload ${booths.size} booths..."

                repository.uploadBoothsFromExcel(booths) { progress, message ->
                    _uploadProgress.value = progress
                    _uploadMessage.value = message
                }

                Toast.makeText(context, "Booths uploaded successfully", Toast.LENGTH_SHORT).show()
                refreshData()
            } catch (e: Exception) {
                Toast.makeText(context, "Error uploading booths: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isUploading.value = false
                _uploadProgress.value = 0f
                _uploadMessage.value = ""
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