package com.example.matchyourmunch.model

/**
 * Datenklasse für eine Liste
 * @param name der Name der Liste
 * @param owner der Ersteller der Liste
 * @param icon das Icon der Liste
 * @param spots die Liste der enthaltenen Spots der Liste
 */
data class FoodSpotList(
    val name: String,
    val owner: String,
    val icon: String,
    val spots: List<FoodSpot>
)