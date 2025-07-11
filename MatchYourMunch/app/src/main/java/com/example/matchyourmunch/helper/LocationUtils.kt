package com.example.matchyourmunch.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Locale

/**
 * Hilfsklasse für die Arbeit mit Koordinaten und Standortdiensten
 */
object LocationUtils {
    /**
     * ermittelt Koordinaten aus einer Adresse
     * @param context der Anwendungskontext zur initialisierung des Geocoders
     * @param adress die Adresse, aus der die Koordinaten ermittelt werden sollen
     * @return die Standortdaten bestehend aus Latitude und Longitude
     */
    suspend fun getCoordinatesFromAddress(context: Context, address: String): Pair<Double, Double>? {
        return try {
            if (!Geocoder.isPresent()) {
                return null
            }
            val geocoder = Geocoder(context, Locale.getDefault())
            val resultList = withContext(Dispatchers.IO) {
                withTimeout(3000) {
                    geocoder.getFromLocationName(address, 1)
                }
            }
            if (!resultList.isNullOrEmpty()) {
                val location = resultList[0]
                val coords = Pair(location.latitude, location.longitude)
                coords
            } else {
                null
            }
        } catch (e: TimeoutCancellationException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * liefert zuletzt bekannte Geräteposition
     * @param context der Anwendungskontext für den Zugriff auf Standortdienste
     * @return die Standortdaten bestehend aus Latitude und Longitude
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(context: Context): Pair<Double, Double>? {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await()
            if (location == null) {
            }
            location?.let { Pair(it.latitude, it.longitude) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * berechnet die Luftlinien-Entfernung zwischen zwei Koordinatenpunkten in km
     * @param lat1 der Breitengrad des ersten Punktes
     * @param lon1 der Längengrad des ersten Punktes
     * @param lat2 der Breitengrad des zweiten Punktes
     * @param lon2 der Längengrad des zweiten Punktes
     * @return
     */
    fun calculateDistanceInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000.0
    }
}
