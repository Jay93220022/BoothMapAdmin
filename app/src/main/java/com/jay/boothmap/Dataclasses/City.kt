package com.jay.boothmap.Dataclasses

data class City(
    val name: String = "",
    var booths: List<Booth> = emptyList(), // Booths of the city
    var isBoothFetched: Boolean = false,   // Flag to indicate if booths are fetched
    var isExpanded: Boolean = false,        // To track whether to show or hide the booths
    val isLoading: Boolean = false
)
