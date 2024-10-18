package com.jay.boothmap.Repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.jay.boothmap.Dataclasses.Booth
import com.jay.boothmap.Dataclasses.City
import com.jay.boothmap.FirebaseSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BoothRepository(private val firebaseSource: FirebaseSource) {
    suspend fun fetchCities(): List<City> {
        return firebaseSource.getCities()
    }

    private val database = FirebaseDatabase.getInstance().getReference("Cities")
    private val cityRef = Firebase.database.getReference("Cities")

    suspend fun fetchBoothsForCity(cityName: String): List<Booth> = suspendCancellableCoroutine { continuation ->
        Log.d("Testing", "Inside fetchBooths $cityName")
        // Changed to specifically target the Booth node
        cityRef.child(cityName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Testing", "Inside onDataChange")
                Log.d("Testing", "Snapshot exists: ${snapshot.exists()}")
                Log.d("Testing", "Snapshot value: ${snapshot.value}")

                try {
                    val booths = mutableListOf<Booth>()

                    if (snapshot.exists()) {
                        for (boothSnapshot in snapshot.children) {
                            Log.d("Testing", "Processing booth: ${boothSnapshot.key}")
                            try {
                                val booth = boothSnapshot.getValue(Booth::class.java)
                                Log.d("Testing", "Parsed booth: $booth")
                                booth?.let {
                                    // Ensure city is set in the booth object
                                    booths.add(it.copy(city = cityName))
                                }
                            } catch (e: Exception) {
                                Log.e("Testing", "Error parsing booth: ${e.message}")
                                // Try manual parsing if automatic parsing fails
                                val map = boothSnapshot.value as? Map<String, Any>
                                if (map != null) {
                                    val booth = Booth(
                                        id = map["id"]?.toString() ?: "",
                                        name = map["name"]?.toString() ?: "",
                                        bloName = map["bloName"]?.toString() ?: "",
                                        bloContact = map["bloContact"]?.toString() ?: "",
                                        district = map["district"]?.toString() ?: cityName,
                                        taluka = map["taluka"]?.toString() ?: "",
                                        latitude = (map["latitude"] as? Number)?.toDouble() ?: 0.0,
                                        longitude = (map["longitude"] as? Number)?.toDouble() ?: 0.0,
                                        city = cityName
                                    )
                                    booths.add(booth)
                                }
                            }
                        }
                    }

                    Log.d("Testing", "Resuming with ${booths.size} booths")
                    continuation.resume(booths)

                } catch (e: Exception) {
                    Log.e("Testing", "Error in onDataChange: ${e.message}")
                    continuation.resumeWithException(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Testing", "Database error: ${error.message}")
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getBoothByName(cityName: String, boothName: String): Booth? {
        return firebaseSource.getBoothByName(cityName, boothName)
    }

    suspend fun addBooth(newBooth: Booth) {
        firebaseSource.addBooth(newBooth)
    }


    suspend fun updateBooth(cityName: String, boothId: String, updatedBooth: Booth) {
        firebaseSource.updateBooth(cityName, boothId, updatedBooth)
    }
}