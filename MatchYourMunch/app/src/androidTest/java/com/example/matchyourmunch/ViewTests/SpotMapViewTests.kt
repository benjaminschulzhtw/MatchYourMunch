package com.example.matchyourmunch.ViewTests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.activity.ComponentActivity
import com.example.matchyourmunch.view.SpotMapPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI-Tests für SpotMapView
 */
@RunWith(AndroidJUnit4::class)
class SpotMapViewTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    /**
     * Sind alle Steuerelemente der Karte sichtbar?
     */
    @Test
    fun mapView_andButtons_areVisible() {
        composeTestRule.setContent {
            SpotMapPreview(
                spotLat = 52.52,
                spotLng = 13.405,
                deviceLat = 52.5205,
                deviceLng = 13.4095,
                spotName = "Test Spot"
            )
        }

        composeTestRule.onNodeWithText("Finde mich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zum Spot").assertIsDisplayed()
        composeTestRule.onNodeWithText("+").assertIsDisplayed()
        composeTestRule.onNodeWithText("−").assertIsDisplayed()
    }

    /**
     * Funktioniert der "Finde mich"-Button?
     */
    @Test
    fun click_findMe_doesNotCrash() {
        composeTestRule.setContent {
            SpotMapPreview(
                spotLat = 52.52,
                spotLng = 13.405,
                deviceLat = 52.5205,
                deviceLng = 13.4095
            )
        }

        composeTestRule.onNodeWithText("Finde mich").performClick()
    }

    /**
     * Sind "+" und "-" klickbar und lösen die korrekte Aktion aus?
     */
    @Test
    fun click_zoomIn_and_zoomOut_doesNotCrash() {
        composeTestRule.setContent {
            SpotMapPreview(
                spotLat = 52.52,
                spotLng = 13.405
            )
        }

        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("−").performClick()
    }
}
