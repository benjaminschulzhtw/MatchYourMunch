package com.example.matchyourmunch.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

/**
 * Steuert Standortermittlung
 * Verwaltet Zugriff auf Android-Standortdienst
 * @param application der Anwendungskontext für die Standortabfrage
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    //der Anwendungskontext für die Standortabfrage
    private val context = getApplication<Application>().applicationContext

    //Android-Standortdienst
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    //die aktuelle Geräteposition
    private val _deviceLocation = MutableStateFlow<GeoPoint?>(null)
    val deviceLocation: StateFlow<GeoPoint?> = _deviceLocation

    //Listener, der beim Eintreffen von Standortdaten einmal ausgelöst wird
    private val oneTimeLocationListener = object : LocationListener {
        /**
         * wird ausgelöst wenn Standort verfügbar
         * @param location die Position mit den empfangenen Standortdaten
         */
        override fun onLocationChanged(location: Location) {
            _deviceLocation.value = GeoPoint(location.latitude, location.longitude)
            stopLocationUpdates()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * einmalige Standortermittlung
     */
    fun requestSingleLocationUpdate() {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                oneTimeLocationListener,
                null
            )
        }
    }

    /**
     * Standortaktualisierung stoppen
     */
    private fun stopLocationUpdates() {
        locationManager.removeUpdates(oneTimeLocationListener)
    }
}
