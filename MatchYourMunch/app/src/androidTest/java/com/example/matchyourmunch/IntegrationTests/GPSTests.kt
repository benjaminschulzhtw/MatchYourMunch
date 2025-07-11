package com.example.matchyourmunch

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import com.example.matchyourmunch.helper.LocationUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Testklasse zur Überprüfung der Standortfunktionen
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GPSTests {

    private lateinit var context: Context
    @get:Rule
    val composeTestRule = createComposeRule()
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    /**
     * Liefert der Geocoder sinnvolle Koordinaten?
     */
    @Test
    fun geocoding_returnsCoordinates_forValidAddress() = runTest {
        val coords = LocationUtils.getCoordinatesFromAddress(context, "Berlin, Germany")

        assertNotNull(coords)
        assertTrue(coords!!.first in 50.0..55.0)
        assertTrue(coords.second in 10.0..15.0)
    }

    /**
     * Funktioniert die Distanzberechnung?
     */
    @Test
    fun calculateDistance_returnsCorrectDistance() {
        val berlin = Pair(52.5200, 13.4050)
        val hamburg = Pair(53.5511, 9.9937)
        val distance = LocationUtils.calculateDistanceInKm(
            berlin.first, berlin.second,
            hamburg.first, hamburg.second
        )

        assertTrue(distance in 250.0..300.0)
    }

    /**
     * Funktioniert das Abrufen der letzten bekannten Position?
     */
    @Test
    fun lastKnownLocation_doesNotCrash() = runTest {
        val location = LocationUtils.getLastKnownLocation(context)
    }
}
