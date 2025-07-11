package com.example.matchyourmunch.model
import java.time.LocalDateTime

/**
 * Datenklasse für einen FoodSpot
 * @param id die ID des FoodSpot
 * @param name der Name des FoodSpot
 * @param address die Adresse des FoodSpot
 * @param rating die Bewertung des FoodSpot
 * @param comment der Kommentar des FoodSpot
 * @param listId die ID der Liste, in der der FoodSpot enthalten ist
 * @param dateAdded das Erstelldatum des FoodSpot
 * @param category die Kategorie des FoodSpot
 * @param menu die Speisekarte des FoodSpot
 */
data class FoodSpot(
    val id: Int = 0,
    val name: String,
    val address: String,
    val rating: Byte,
    val comment: String = "",
    val listId: Int,
    val dateAdded: LocalDateTime,
    val category: String,
    val menu: String
)

