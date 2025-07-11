package com.example.matchyourmunch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchyourmunch.model.FoodSpot
import com.example.matchyourmunch.model.FoodSpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.example.matchyourmunch.helper.FoodSpotViewModelInterface
import kotlinx.coroutines.tasks.await

/**
 * Zugriff und Verwaltung von FoodSpots einer Liste
 * Kommunikation mit FoodSpotRepository
 * @param repository die Datenquelle aller Food-Spots
 * @param listId die ID der aktuellen Liste
 * @param spotId die ID des aktuellen Spots
 */
class FoodSpotViewModel(
    private val repository: FoodSpotRepository,
    private val listId: Int,
    private val spotId: Int?
) : ViewModel(), FoodSpotViewModelInterface {

    //aktueller Spot
    private val _spot = MutableStateFlow<FoodSpot?>(null)
    override val spot: StateFlow<FoodSpot?> = _spot

    //alle Spots der aktuellen Liste
    private val _spots = MutableStateFlow<List<FoodSpot>>(emptyList())
    override val spots: StateFlow<List<FoodSpot>> = _spots

    init {
        loadSpots()
        if (spotId != null && spotId >= 0) {
            viewModelScope.launch {
                _spot.value = repository.getSpotById(spotId)
            }
        }
    }

    /**
     * Spots laden
     */
    override fun loadSpots() {
        viewModelScope.launch {
            _spots.value = repository.getSpotsForList(listId)
        }
    }

    /**
     * Spot hinzufügen
     * @param spot der Spot, der hinzugefügt wird
     */
    override fun addSpot(spot: FoodSpot) {
        viewModelScope.launch {
            repository.insertSpot(spot)
            loadSpots()
        }
    }

    /**
     * alle Spots löschen
     */
    fun deleteAllSpots() {
        viewModelScope.launch {
            repository.deleteAllForList(listId)
            loadSpots()
        }
    }

    /**
     * einen Spot löschen
     * @param spot der Spot, der gelöscht wird
     */
    fun deleteSpot(spot: FoodSpot) {
        viewModelScope.launch {
            repository.deleteSpot(spot)
            loadSpots()
        }
    }

    /**
     * Spot aktualisieren
     * @param updated der Spot, der aktualisiert wird
     */
    override fun updateSpot(updated: FoodSpot) {
        viewModelScope.launch {
            repository.updateSpot(updated)
            _spot.value = updated
            loadSpots()
        }
    }


    /**
     * aktuelle Position des Gerätes zurückgeben
     * @param context der Kontext für den Zugriff auf den Standortdienst des Betriebssystems
     * @return der aktuelle Standort bestehend aus Latitude und Longitude; null wenn kein Standort verfügbar
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedLocationClient.lastLocation.await()
        return if (location != null) {
            Pair(location.latitude, location.longitude)
        } else null
    }
}
