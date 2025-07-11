package com.example.matchyourmunch.helper

import com.example.matchyourmunch.model.FoodSpot
import kotlinx.coroutines.flow.StateFlow

/**
 * Schnittstelle für die ViewModels die mit FoodSpots arbeiten
 */
interface FoodSpotViewModelInterface {
    val spots: StateFlow<List<FoodSpot>>
    val spot: StateFlow<FoodSpot?>

    /**
     * lädt alle FoodSpots einer zugewiesenen Liste
     */
    fun loadSpots()

    /**
     * fügt einen neuen FoodSpot zur Liste hinzu
     */
    fun addSpot(spot: FoodSpot)

    /**
     * aktualisiert einen bestehenden FoodSpot in der Liste
     */
    fun updateSpot(updated: FoodSpot)
}