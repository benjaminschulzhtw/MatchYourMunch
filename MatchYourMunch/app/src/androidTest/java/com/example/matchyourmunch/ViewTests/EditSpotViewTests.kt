package com.example.matchyourmunch.ViewTests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.matchyourmunch.model.FoodSpot
import com.example.matchyourmunch.view.EditSpotScreen
import com.example.matchyourmunch.helper.FoodSpotViewModelInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDateTime
import org.junit.Assert.*

/**
 * UI-Tests für EditSpotScreen
 */
@RunWith(AndroidJUnit4::class)
class EditSpotViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeFoodSpotViewModel : FoodSpotViewModelInterface {
        private val _spotFlow = MutableStateFlow<FoodSpot?>(
            FoodSpot(
                id = 1,
                name = "Test Spot",
                address = "Teststraße 1",
                rating = 3,
                comment = "Kommentar",
                listId = 1,
                dateAdded = LocalDateTime.now(),
                category = "Test Kategorie",
                menu = "www.test.de"
            )
        )

        override val spot: StateFlow<FoodSpot?> = _spotFlow
        override val spots: StateFlow<List<FoodSpot>> = MutableStateFlow(emptyList())

        var updated: FoodSpot? = null

        override fun updateSpot(updated: FoodSpot) {
            this.updated = updated
            _spotFlow.value = updated
        }

        override fun addSpot(spot: FoodSpot) {}
        override fun loadSpots() {}
    }

    private val fakeViewModel = FakeFoodSpotViewModel()

    /**
     * Werden alle Eingabefelder korrekt befüllt?
     */
    @Test
    fun spotFields_areDisplayedCorrectly() {
        composeTestRule.setContent {
            EditSpotScreen(
                spotId = 1,
                viewModel = fakeViewModel,
                onBack = {}
            )
        }

        composeTestRule.onNodeWithTag("nameInput").assertTextContains("Test Spot")
        composeTestRule.onNodeWithTag("addressInput").assertTextContains("Teststraße 1")
        composeTestRule.onNodeWithTag("commentInput").assertTextContains("Kommentar")
        composeTestRule.onNodeWithTag("categoryInput").assertTextContains("Test Kategorie")
        composeTestRule.onNodeWithTag("menuInput").assertTextContains("www.test.de")
    }

    /**
     * Werden Fehlermeldungen richtig angezeigt?
     */
    @Test
    fun showsError_onEmptyFields() {
        composeTestRule.setContent {
            EditSpotScreen(
                spotId = 1,
                viewModel = fakeViewModel,
                onBack = {}
            )
        }

        composeTestRule.onNodeWithTag("nameInput").performTextClearance()
        composeTestRule.onNodeWithTag("addressInput").performTextClearance()
        composeTestRule.onNodeWithText("Speichern").performClick()
        composeTestRule.onNodeWithText("Name darf nicht leer sein").assertIsDisplayed()
        composeTestRule.onNodeWithText("Adresse darf nicht leer sein").assertIsDisplayed()
    }

    /**
     * Funktioniert die Bewertung durch Klicken der Sterne?
     */
    @Test
    fun rating_canBeChangedViaStars() {
        composeTestRule.setContent {
            EditSpotScreen(
                spotId = 1,
                viewModel = fakeViewModel,
                onBack = {}
            )
        }

        composeTestRule.onAllNodesWithContentDescription("Stern 5")[0].performClick()
        composeTestRule.onNodeWithText("5.0").assertIsDisplayed()
    }

    /**
     * Funktioniert die Bearbeitung korrekt?
     */
    @Test
    fun updatedSpot_isSaved_and_onBack_isCalled() {
        var onBackCalled = false

        composeTestRule.setContent {
            EditSpotScreen(
                spotId = 1,
                viewModel = fakeViewModel,
                onBack = { onBackCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("nameInput").performTextReplacement("Geändert")
        composeTestRule.onNodeWithTag("commentInput").performTextReplacement("Geänderter Kommentar")
        composeTestRule.onNodeWithText("Speichern").performClick()

        val updated = fakeViewModel.updated
        assertNotNull(updated)
        assertEquals("Geändert", updated?.name)
        assertEquals("Geänderter Kommentar", updated?.comment)
        assertTrue(onBackCalled)
    }
}
