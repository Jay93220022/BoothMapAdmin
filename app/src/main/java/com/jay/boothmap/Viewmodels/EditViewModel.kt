package com.jay.boothmap.Viewmodels

import android.util.Log
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

    fun addBooth(newBooth: Booth, ) {
        viewModelScope.launch {
            try {
                boothRepository.addBooth(newBooth)

            } catch (e: Exception) {
                Log.e("ListViewModel", "Error adding booth: ${e.message}")
                // Handle the error (show a toast or update UI state)
            }
        }
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
