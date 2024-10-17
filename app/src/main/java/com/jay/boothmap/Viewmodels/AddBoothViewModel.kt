package com.jay.boothmap.Viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Repositories.BoothRepository

import kotlinx.coroutines.launch

class AddBoothViewModel(private val boothRepository: BoothRepository) : ViewModel() {
    fun addBooth(newBooth: Booth, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                boothRepository.addBooth(newBooth)
                onComplete()
            } catch (e: Exception) {
                Log.e("ListViewModel", "Error adding booth: ${e.message}")
                // Handle the error (show a toast or update UI state)
            }
        }
    }
}
