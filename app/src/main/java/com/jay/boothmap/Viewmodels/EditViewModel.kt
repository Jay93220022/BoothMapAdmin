package com.jay.boothmap.Viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Repositories.BoothRepository
import kotlinx.coroutines.launch

class EditViewModel(private val boothRepository: BoothRepository) : ViewModel() {

    // UI State
    var uiState by mutableStateOf(EditBoothUiState())
        private set

    // Fetch booth details
    fun fetchBoothById(cityName: String, boothId: String,boothName:String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                val fetchedBooth = boothRepository.getBoothById(cityName, boothId,boothName)

                if (fetchedBooth != null) {
                    uiState = uiState.copy(
                        booth = fetchedBooth,
                        boothFields = Booth(
                            name = fetchedBooth.name,
                            bloName = fetchedBooth.bloName,
                            bloContact = fetchedBooth.bloContact,
                            district = fetchedBooth.district,
                            taluka = fetchedBooth.taluka,
                            latitude = fetchedBooth.latitude,
                            longitude = fetchedBooth.longitude
                        ),
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        error = "Booth not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "Failed to fetch booth: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // Update booth field values
    fun updateField(field: String, value: String) {
        uiState = uiState.copy(
            boothFields = when (field) {
                "name" -> uiState.boothFields.copy(name = value)
                "bloName" -> uiState.boothFields.copy(bloName = value)
                "bloContact" -> uiState.boothFields.copy(bloContact = value)
                "district" -> uiState.boothFields.copy(district = value)
                "taluka" -> uiState.boothFields.copy(taluka = value)
//                "latitude" -> uiState.boothFields.copy(latitude = value)
//                "longitude" -> uiState.boothFields.copy(longitude = value)
                else -> uiState.boothFields
            }
        )
    }

    // Validate booth fields
    private fun validateFields(): Boolean {
        val fields = uiState.boothFields
        return fields.name.isNotBlank() &&
                fields.bloName.isNotBlank() &&
                fields.bloContact.isNotBlank() &&
                fields.district.isNotBlank() &&
                fields.taluka.isNotBlank()
    }

    // Save booth updates
    fun updateBooth(cityName: String, boothId: String, onComplete: () -> Unit) {
        if (!validateFields()) {
            uiState = uiState.copy(error = "Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)

                val updatedBooth = Booth(
                    id = boothId,
                    city = cityName,
                    name = uiState.boothFields.name,
                    bloName = uiState.boothFields.bloName,
                    bloContact = uiState.boothFields.bloContact,
                    district = uiState.boothFields.district,
                    taluka = uiState.boothFields.taluka,
                    latitude = uiState.boothFields.latitude,
                    longitude = uiState.boothFields.longitude
                )

                boothRepository.updateBooth(cityName, boothId, updatedBooth)
                uiState = uiState.copy(isLoading = false, saveSuccess = true)
                onComplete()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "Failed to update booth: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // Clear error message
    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    // Reset save success flag
    fun resetSaveSuccess() {
        uiState = uiState.copy(saveSuccess = false)
    }
}

// UI State data class
data class EditBoothUiState(
    val booth: Booth? = null,
    val boothFields: Booth = Booth(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

// Booth fields data class for form state
