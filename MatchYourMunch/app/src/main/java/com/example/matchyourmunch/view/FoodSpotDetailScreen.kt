package com.example.matchyourmunch.view

import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.matchyourmunch.model.FoodSpot
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.matchyourmunch.helper.LocationUtils.getCoordinatesFromAddress
import java.time.format.DateTimeFormatter

/**
 * Detailansicht eines FoodSpot
 * @param spot der aktuelle FoodSpot
 * @param onBackClick Callback bei Klick auf "Zurück"
 * @param onEditClick Callback bei Klick auf "Bearbeiten"
 * @param onDeleteClick Callback bei Klick auf "Löschen"
 * @param navController der Navigationscontroller zur Navigation zwischen den Bildschirmen
 * @param deviceCoordinates die Gerätepsotion
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSpotDetailScreen(
    spot: FoodSpot,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: (FoodSpot) -> Unit = {},
    navController: NavController,
    deviceCoordinates: Pair<Double, Double>? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var spotCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var rating by remember { mutableStateOf(spot.rating.toFloat()) }
    val formattedDate = spot.dateAdded.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

    var distanceKm by remember { mutableStateOf<Double?>(null) }
    LaunchedEffect(spot.address, deviceCoordinates) {
        val coords = getCoordinatesFromAddress(context, spot.address)
        spotCoordinates = coords
        Log.d("KoordinatenCheck", "Spot-Koordinaten: $coords")

        if (coords != null && deviceCoordinates != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                deviceCoordinates.first, deviceCoordinates.second,
                coords.first, coords.second,
                results
            )
            distanceKm = results[0] / 1000.0
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF2E0))
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .height(64.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    IconButton(onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("reload_spots", true)
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFFD2D200)
                        )
                    }
                    Text(
                        text = spot.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Menü",
                                tint = Color(0xFFD2D200)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Bearbeiten") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("edit_spot/${spot.id}")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Löschen") },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                if (spotCoordinates != null) {
                    SpotMapPreview(
                        spotLat = spotCoordinates!!.first,
                        spotLng = spotCoordinates!!.second,
                        deviceLat = deviceCoordinates?.first,
                        deviceLng = deviceCoordinates?.second,
                        spotName = spot.name
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Standort konnte nicht geladen werden.\nBitte Internetverbindung prüfen.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Adresse", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = spot.address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Standort-Infos", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = "Spot-Koordinaten: ${
                        spotCoordinates?.let { "${it.first}, ${it.second}" } ?: "Nicht gefunden"
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Dein Standort: ${
                        deviceCoordinates?.let { "${it.first}, ${it.second}" } ?: "Nicht verfügbar"
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Entfernung (ungefähr): ${
                        distanceKm?.let { String.format("%.2f km", it) } ?: "Nicht verfügbar"
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Bewertung", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { i ->
                        Icon(
                            imageVector = if (rating >= i) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Stern $i",
                            tint = if (rating >= i) Color(0xFFFFA500) else Color.Gray,
                            modifier = Modifier
                                .padding(end = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = rating.toString(),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kommentare", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = spot.comment.ifBlank { "Keine Kommentare" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Weitere Informationen",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFff8000),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                InfoRow(label = "Hinzugefügt am", value = formattedDate)
                InfoRow(label = "Kategorie", value = spot.category.ifBlank { "Keine Angabe" })
                InfoRow(label = "Speisekarte", value = spot.menu.ifBlank { "Keine Angabe" })
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Food-Spot löschen") },
            text = { Text("Möchtest du diesen Spot wirklich löschen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick(spot)
                        onBackClick()
                    }
                ) {
                    Text("Löschen", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

/**
 * Hilfsfunktion zur Anzeige einer Textzeile für "Weitere Informationen"
 * @param label die Beschriftung
 * @param value der anzuzeigende Eintrag
 */
@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

