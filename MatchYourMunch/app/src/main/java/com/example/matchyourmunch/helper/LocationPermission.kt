package com.example.matchyourmunch.helper

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*

/**
 * Hilfsfunktion zur Anforderung der Standortberechtigungen
 * @param onGranted Callback bei erteilter Berechtigung
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper(
    onGranted: @Composable (deviceCoordinates: Pair<Double, Double>?) -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val hasRequestedLocation = remember { mutableStateOf(false) }
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted && !hasRequestedLocation.value) {
            hasRequestedLocation.value = true
            location = LocationUtils.getLastKnownLocation(context)
        } else if (!permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        onGranted(location)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Bitte erlaube der App den Zugriff auf deinen Standort.",
                color = Color.Gray
            )
        }
    }
}
