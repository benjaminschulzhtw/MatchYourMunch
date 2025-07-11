package com.example.matchyourmunch.ViewTests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.matchyourmunch.model.FoodSpot
import com.example.matchyourmunch.view.FoodSpotDetailScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDateTime

/**
 * UI-Tests für FoodSpotDetailScreen
 */
@RunWith(AndroidJUnit4::class)
class FoodSpotViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSpot = FoodSpot(
        id = 1,
        name = "Falafel House",
        address = "Musterstraße 1, 12345 Berlin",
        rating = 4,
        comment = "Sehr lecker und günstig!",
        listId = 1,
        dateAdded = LocalDateTime.now(),
        category = "Orientalisch",
        menu = "www.falafelhouse.de"
    )

    /**
     * Funktioniert der Fallback, wenn Koordinaten nicht zur Verfügung stehen?
     */
    @Test
    fun mapFallback_isDisplayedWhenNoCoordinatesAvailable() {
        composeTestRule.setContent {
            FoodSpotDetailScreen(
                spot = testSpot,
                onBackClick = {},
                navController = rememberNavController(),
                deviceCoordinates = null
            )
        }
        composeTestRule.onNodeWithText(
            "Standort konnte nicht geladen werden.\nBitte Internetverbindung prüfen."
        ).assertIsDisplayed()
    }

    /**
     * Wird das Bearbeiten-Menü richtig angezeigt?
     */
    @Test
    fun editMenu_opensAndShowsOptions() {
        composeTestRule.setContent {
            FoodSpotDetailScreen(
                spot = testSpot,
                onBackClick = {},
                navController = rememberNavController()
            )
        }

        composeTestRule.onNodeWithContentDescription("Menü").performClick()

        composeTestRule.onNodeWithText("Bearbeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Löschen").assertIsDisplayed()
    }

    /**
     * Erscheint der Dialog wenn man "Löschen" klickt?
     */
    @Test
    fun deleteDialog_appearsAndConfirms() {
        var deleteConfirmed = false

        composeTestRule.setContent {
            FoodSpotDetailScreen(
                spot = testSpot,
                onBackClick = {},
                navController = rememberNavController(),
                onDeleteClick = { deleteConfirmed = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Menü").performClick()
        composeTestRule.onNodeWithText("Löschen").performClick()
        composeTestRule.onNodeWithText("Food-Spot löschen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Möchtest du diesen Spot wirklich löschen?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Löschen").performClick()

        assert(deleteConfirmed)
    }
}
