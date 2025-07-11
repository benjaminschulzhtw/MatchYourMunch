package com.example.matchyourmunch.ViewModelTests

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.matchyourmunch.viewmodel.LocationViewModel
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint

/**
 * Testklasse für LocationViewModel
 */
class LocationViewModelTests {

    private lateinit var application: Application
    private lateinit var context: Context
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: LocationViewModel

    @Before
    fun setup() {
        application = mockk(relaxed = true)
        context = mockk(relaxed = true)
        locationManager = mockk(relaxed = true)

        every { application.applicationContext } returns context

        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager

        mockkStatic(ContextCompat::class)
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } returns PackageManager.PERMISSION_GRANTED

        viewModel = LocationViewModel(application)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    /**
     * Wird bei vorhandener Berechtigung ein Standort-Update angefordert?
     */
    @Test
    fun requestSingleLocationUpdate_requestsLocation_whenPermissionGranted() {
        viewModel.requestSingleLocationUpdate()

        verify {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                any(),
                null
            )
        }
    }

    /**
     * Wird die Verarbeitung eines Standort-Callbacks korrekt durchgeführt?
     */
    @Test
    fun onLocationChanged_setsDeviceLocation_andStopsUpdates() = runTest {
        val latitude = 52.5200
        val longitude = 13.4050
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns latitude
        every { mockLocation.longitude } returns longitude

        viewModel.requestSingleLocationUpdate()

        val slot = slot<LocationListener>()
        verify {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                capture(slot),
                null
            )
        }

        slot.captured.onLocationChanged(mockLocation)

        val expected = GeoPoint(latitude, longitude)
        val actual = viewModel.deviceLocation.value
        assertNotNull(actual)
        assertEquals(expected.latitude, actual!!.latitude, 0.0001)
        assertEquals(expected.longitude, actual.longitude, 0.0001)

        verify { locationManager.removeUpdates(slot.captured) }
    }

    /**
     * Wird bei fehlender Berechtigung korrekt kein Standort-Update angefordert?
     */
    @Test
    fun requestSingleLocationUpdate_doesNothing_whenPermissionDenied() {
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } returns PackageManager.PERMISSION_DENIED

        viewModel.requestSingleLocationUpdate()

        verify(exactly = 0) {
            locationManager.requestSingleUpdate(
                any<String>(),
                any(),
                isNull()
            )
        }

    }
}
