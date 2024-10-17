package com.jay.boothmap

import com.google.firebase.database.FirebaseDatabase
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Dataclasses.City
import kotlinx.coroutines.tasks.await

class FirebaseSource {
    private val db = FirebaseDatabase.getInstance("https://boothmap-d6a5f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

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

    // Function to update booth data in Realtime Database
    suspend fun updateBooth(cityName: String, boothName: String, updatedBooth: Booth) {
        // Convert Booth object to a Map for updating in Firebase
        val boothMap = mapOf(
            "id" to updatedBooth.id,
            "name" to updatedBooth.name,
            "bloName" to updatedBooth.bloName,
            "bloContact" to updatedBooth.bloContact,
            "district" to updatedBooth.district,
            "taluka" to updatedBooth.taluka,
            "city" to updatedBooth.city,
            "latitude" to updatedBooth.latitude,
            "longitude" to updatedBooth.longitude
        )

        // Update booth data in Firebase using the map
        db.child("Cities")
            .child(cityName)
            .child(boothName)
            .updateChildren(boothMap) // Pass the map here
            .await()
    }
    // Function to get a specific booth by ID from Realtime Database
    suspend fun getBoothById(cityName: String, boothId: String,boothName:String): Booth? {
        val boothSnapshot = db.child("Cities")
            .child(cityName)
            .child(boothName)
            .child(boothId)
            .get()
            .await()

        return boothSnapshot.getValue(Booth::class.java)?.copy(id = boothId, city = cityName)
    }
}
