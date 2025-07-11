package com.example.matchyourmunch.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchyourmunch.model.FoodSpotRepository
import com.example.matchyourmunch.viewmodel.FoodSpotViewModel

/**
 * Factory zur Erzeugung eines FoodSpotViewModel
 * @param repository das Repository für den Zugriff auf die FoodSpots
 * @param listId die ID der Liste, in welcher der Spot enthalten ist
 * @param spotId die ID des Spots
 */
class FoodSpotViewModelFactory(
    private val repository: FoodSpotRepository,
    private val listId: Int,
    private val spotId: Int?
) : ViewModelProvider.Factory {

    /**
     * erstellt ein neues FoodSpotViewModel
     * @param modelClass die Klasse des ViewModels, das erzeugt werden soll
     * @return das neue ViewModel
     * @throws IllegalArgumentException wenn die ViewModel-Klasse nicht unterstützt wird
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodSpotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodSpotViewModel(repository, listId, spotId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}