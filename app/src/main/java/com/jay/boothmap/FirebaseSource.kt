package com.jay.boothmap

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Dataclasses.City
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseSource {
    private val db = FirebaseDatabase.getInstance("https://boothmap-d6a5f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val storage = FirebaseStorage.getInstance().reference
    // Function to get cities and booths from Realtime Database
    suspend fun getCities(): List<City> {
        val citiesSnapshot = db.child("Cities").get().await() // Change to use Realtime Database
        val cities = mutableListOf<City>()

        for (citySnapshot in citiesSnapshot.children) {
            val booths = mutableListOf<Booth>()
            val boothSnapshot = citySnapshot.child("booths") // Assuming you have a subcollection of booths under each city

            for (booth in boothSnapshot.children) {
                booths.add(booth.getValue(Booth::class.java)!!.copy(id = booth.key!!, city = citySnapshot.key!!))
            }

            cities.add(City(name = citySnapshot.key!!, booths = booths))
        }

        return cities
    }

    // Function to add a new booth to Realtime Database
    suspend fun addBooth(newBooth: Booth) {
        db.child("Cities")
            .child(newBooth.city)
            .child(newBooth.name)
            .setValue(newBooth)
            .await()
    }

    // Modified addBooth function to handle image upload
    suspend fun addBoothWithImage(newBooth: Booth, imageUri: Uri?): String {
        // First upload the image if provided
        val imageUrl = imageUri?.let {
            uploadImageToFirebaseStorage(it)
        } ?: ""

        // Create booth with image URL
        val boothWithImage = newBooth.copy(imageUri = imageUrl)

        // Add to realtime database
        db.child("Cities")
            .child(boothWithImage.city)
            .child(boothWithImage.name)
            .setValue(boothWithImage)
            .await()

        return imageUrl
    }

    // Modified update function to handle image
    suspend fun updateBooth(cityName: String, boothName: String, updatedBooth: Booth, newImageUri: Uri?) {
        // Upload new image if provided
        val imageUrl = newImageUri?.let {
            uploadImageToFirebaseStorage(it)
        } ?: updatedBooth.imageUri // Keep existing image URL if no new image

        // Create updated booth map with image URL
        val boothMap = mapOf(
            "id" to updatedBooth.id,
            "name" to updatedBooth.name,
            "bloName" to updatedBooth.bloName,
            "bloContact" to updatedBooth.bloContact,
            "district" to updatedBooth.district,
            "taluka" to updatedBooth.taluka,
            "city" to updatedBooth.city,
            "latitude" to updatedBooth.latitude,
            "longitude" to updatedBooth.longitude,
            "imageUri" to imageUrl
        )

        // Update booth data in Firebase
        db.child("Cities")
            .child(cityName)
            .child(boothName)
            .updateChildren(boothMap)
            .await()
    }

    // Function to get a specific booth by ID from Realtime Database
    suspend fun getBoothByName(cityName: String, boothName: String): Booth? {
        val boothQuerySnapshot = db.child("Cities")
            .child(cityName)
            .orderByChild(boothName)  // Query by the 'name' field, which corresponds to the boothName
            .equalTo(boothName)
            .get()
            .await()

        for (snapshot in boothQuerySnapshot.children) {
            val booth = snapshot.getValue(Booth::class.java)
            if (booth != null) {
                return booth.copy(id = snapshot.key ?: "", city = cityName)
            }
        }
        return null  // Return null if no booth is found
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun uploadImageToFirebaseStorage(imageUri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val timestamp = System.currentTimeMillis()
            val imageRef: StorageReference = storage.child("booths/booth_image_$timestamp.jpg")

            // Upload the image
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        continuation.resume(uri.toString())
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}
