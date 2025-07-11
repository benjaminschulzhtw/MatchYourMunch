package com.example.matchyourmunch.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchyourmunch.model.FoodSpotRepository
import com.example.matchyourmunch.viewmodel.FoodSpotViewModel

/**
 * stellt ein FoodSpotViewModel bereit, das innerhalb einer Composable oder Tests verwendet werden kann
 * @param listId die ID der Liste, in der der FoodSpot enthalten ist
 * @param spotId die ID des FoodSpots
 * @return das FoodSpotViewModel
 */
@Composable
fun provideFoodSpotViewModel(listId: Int, spotId: Int?): FoodSpotViewModel {
    val context = LocalContext.current
    val repository = remember { FoodSpotRepository(context) }
    return viewModel(factory = FoodSpotViewModelFactory(repository, listId, spotId))
}


