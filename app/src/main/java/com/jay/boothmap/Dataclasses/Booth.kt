package com.jay.boothmap.Dataclasses
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Booth(
    val bloContact: String = "",
    val bloName: String = "",
    val district: String = "",
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val name: String = "",
    val taluka: String = "",
    val city: String = "",
    val imageUri: String=""
):Parcelable