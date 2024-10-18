package com.jay.boothmap.Viewmodels


import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.FirebaseSource
import com.jay.boothmap.Repositories.BoothRepository

import kotlinx.coroutines.launch

class AddBoothViewModel(private val boothRepository: BoothRepository) : ViewModel() {
    private val firebaseSource = FirebaseSource()

    fun addBoothWithImage(booth: Booth, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                firebaseSource.addBoothWithImage(booth, imageUri)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
                Log.e("AddBoothViewModel", "Error adding booth: ${e.message}")
                throw e
            }
        }
    }
}
