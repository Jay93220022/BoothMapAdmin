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
        db.child("Cities")
            .child(cityName)
            .child(boothName)
            .setValue(updatedBooth)
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
