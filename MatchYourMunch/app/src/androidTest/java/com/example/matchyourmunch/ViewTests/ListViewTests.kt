package com.example.matchyourmunch.ViewTests

import com.example.matchyourmunch.helper.FoodSpotViewModelInterface
import com.example.matchyourmunch.view.ListDetailScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.matchyourmunch.model.FoodSpot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

/**
 * UI-Tests für ListDetailScreen
 */
@RunWith(AndroidJUnit4::class)
class ListViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeFoodSpotViewModel : FoodSpotViewModelInterface {
        private val _spots = MutableStateFlow<List<FoodSpot>>(
            listOf(
                FoodSpot(
                    name = "Pizza Planet",
                    address = "Berlin",
                    rating = 4,
                    comment = "Lecker!",
                    listId = 1,
                    dateAdded = java.time.LocalDateTime.now(),
                    category = "Fast Food",
                    menu = "www.pizza.de"
                ),
                FoodSpot(
                    name = "Sushi Samba",
                    address = "Hamburg",
                    rating = 5,
                    comment = "",
                    listId = 1,
                    dateAdded = java.time.LocalDateTime.now(),
                    category = "Sushi",
                    menu = ""
                )
            )
        )
        override val spots: StateFlow<List<FoodSpot>> = _spots
        override val spot: StateFlow<FoodSpot?> = MutableStateFlow(null)


        var spotAdded: FoodSpot? = null

        override fun addSpot(spot: FoodSpot) {
            spotAdded = spot
            _spots.value = _spots.value + spot
        }

        override fun loadSpots() {
        }

        override fun updateSpot(spot: FoodSpot) {
        }
    }

    private val fakeViewModel = FakeFoodSpotViewModel()

    /**
     * Werden vorhandene Spots korrekt angezeigt?
     */
    @Test
    fun spots_areDisplayed() {
        composeTestRule.waitForIdle()
        composeTestRule.setContent {
            ListDetailScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel,
                listName = "TestListe",
                overrideListId = 1,
                onBackClick = {},
                onSpotClick = {}
            )
        }

        composeTestRule.onNodeWithText("Pizza Planet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sushi Samba").assertIsDisplayed()
    }

    /**
     * Öffnet sich das Formular wenn man einen FoodSpot erstellt?
     */
    @Test
    fun fab_opensDialog_and_addsSpot() {
        composeTestRule.waitForIdle()
        composeTestRule.setContent {
            ListDetailScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel,
                listName = "TestListe",
                overrideListId = 1,
                onBackClick = {},
                onSpotClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Neuen Spot hinzufügen").performClick()

        composeTestRule.onNodeWithText("Name*").performTextInput("Falafel King")
        composeTestRule.onNodeWithText("Adresse*").performTextInput("Kreuzberg")
        composeTestRule.onNodeWithText("Bewertung*").performTextInput("5")

        composeTestRule.onNodeWithText("Speichern").performClick()

        assertEquals("Falafel King", fakeViewModel.spotAdded?.name)
    }

    /**
     * Filtert die Suchfunktion korrekt?
     */
    @Test
    fun search_filtersSpots() {
        composeTestRule.waitForIdle()
        composeTestRule.setContent {
            ListDetailScreen(
                navController = rememberNavController(),
                viewModel = fakeViewModel,
                listName = "TestListe",
                overrideListId = 1,
                onBackClick = {},
                onSpotClick = {}
            )
        }

        composeTestRule.onNodeWithText("Spots suchen...").performTextInput("Sushi")

        composeTestRule.onNodeWithText("Sushi Samba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pizza Planet").assertDoesNotExist()
    }
}
