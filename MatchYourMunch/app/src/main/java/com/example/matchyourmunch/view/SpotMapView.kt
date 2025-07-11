package com.example.matchyourmunch.view

import android.content.Context
import org.osmdroid.config.Configuration
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun SpotMapPreview(
    spotLat: Double,
    spotLng: Double,
    deviceLat: Double? = null,
    deviceLng: Double? = null,
    spotName: String = "FoodSpot"
) {
    val context = LocalContext.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mapViewState.value?.onDetach()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .shadow(6.dp, RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            factory = {
                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences("osm", Context.MODE_PRIVATE)
                )

                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setMultiTouchControls(true)
                    setUseDataConnection(true)
                    setBuiltInZoomControls(false)

                    controller.setZoom(14.0)
                    controller.setCenter(GeoPoint(spotLat, spotLng))

                    val spotMarker = Marker(this).apply {
                        position = GeoPoint(spotLat, spotLng)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = spotName
                    }
                    overlays.add(spotMarker)

                    mapViewState.value = this

                    setOnTouchListener { _, _ ->
                        overlays.forEach {
                            if (it is Marker) it.closeInfoWindow()
                        }
                        false
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),

            // 🔁 WICHTIG: User-Marker nachträglich setzen, wenn device-Koordinaten erst später kommen
            update = { map ->
                if (deviceLat != null && deviceLng != null) {
                    val existingUserMarker = map.overlays.find {
                        it is Marker && it.title == "Du bist hier"
                    }
                    if (existingUserMarker == null) {
                        val userMarker = Marker(map).apply {
                            position = GeoPoint(deviceLat, deviceLng)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Du bist hier"
                        }
                        map.overlays.add(userMarker)
                        map.invalidate() // 🟡 Karte neu zeichnen
                    }
                }
            }
        )

        // 🔍 Zoom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            ZoomButton("+") { mapViewState.value?.controller?.zoomIn() }
            Spacer(modifier = Modifier.height(8.dp))
            ZoomButton("−") { mapViewState.value?.controller?.zoomOut() }
        }

        // 📍 "Finde mich"-Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable {
                    if (deviceLat != null && deviceLng != null) {
                        mapViewState.value?.controller?.animateTo(GeoPoint(deviceLat, deviceLng))
                        mapViewState.value?.controller?.setZoom(16.0)
                    }
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Finde mich", color = Color.Black)
        }

        // "Zum Spot" Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable {
                    mapViewState.value?.controller?.animateTo(GeoPoint(spotLat, spotLng))
                    mapViewState.value?.controller?.setZoom(16.0)
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Zum Spot", color = Color.Black)
        }
    }
}

@Composable
private fun ZoomButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Text(text = label, color = Color.Black)
    }
}
