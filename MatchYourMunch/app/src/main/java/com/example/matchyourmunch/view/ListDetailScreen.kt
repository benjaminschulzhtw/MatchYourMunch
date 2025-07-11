package com.example.matchyourmunch.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.time.LocalDateTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.matchyourmunch.model.FoodSpot
import androidx.compose.foundation.lazy.items
import com.example.matchyourmunch.model.FoodSpotRepository
import com.example.matchyourmunch.helper.FoodSpotViewModelFactory
import com.example.matchyourmunch.model.ListRepository
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.matchyourmunch.helper.FoodSpotViewModelInterface
import com.example.matchyourmunch.helper.LocationUtils.getCoordinatesFromAddress
import com.example.matchyourmunch.helper.LocationUtils.getLastKnownLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * die Detaillansicht einer Liste
 * @param navController der Navigationscontroller zur Navigation zwischen den Bildschirmen
 * @param viewModel das ViewModel zur Verwaltung der Spots
 * @param listName der Name der aktuellen Liste
 * @param onBackClick Callback beim Klick auf "Zurück"
 * @param onFabClick Callback beim Klick auf den FloatingActionButton
 * @param onSpotClick Callback beim Klick auf einen Spot
 * @param onDeleteConfirmed Callback beim erfolgreichen Löschen
 * @param overrideListId überschreibt geladene ID der Liste
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    navController: NavController,
    viewModel: FoodSpotViewModelInterface,
    listName: String,
    onBackClick: () -> Unit,
    onFabClick: () -> Unit = {},
    onSpotClick: (FoodSpot) -> Unit,
    onDeleteConfirmed: () -> Unit = {},
    overrideListId: Int? = null
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val actualListId = overrideListId ?: remember { ListRepository(context).getListIdByName(listName) ?: -1 }
    if (actualListId == -1) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Liste '$listName' nicht gefunden", color = Color.Red)
        }
        return
    }

    val spots by viewModel.spots.collectAsState()

    var deviceCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    LaunchedEffect(Unit) {
        val coords = getLastKnownLocation(context)
        Log.d("StandortCheck", "Aktueller Standort: $coords")
        deviceCoordinates = coords
    }

    val repository = ListRepository(context)

    val listId = actualListId
    if (listId == -1) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Liste '$listName' nicht gefunden", color = Color.Red)
        }
        return
    }

    val viewModelFactory = remember {
        FoodSpotViewModelFactory(FoodSpotRepository(context), listId, -1)
    }

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    LaunchedEffect(currentBackStackEntry) {
        val shouldReload = currentBackStackEntry
            ?.savedStateHandle
            ?.get<Boolean>("reload_spots") ?: false

        if (shouldReload) {
            viewModel.loadSpots()
            currentBackStackEntry
                ?.savedStateHandle
                ?.set("reload_spots", false)
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
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFFff8000)
                        )
                    }
                    Text(
                        text = listName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        floatingActionButton = {
            var showSpotDialog by remember { mutableStateOf(false) }
            FloatingActionButton(
                onClick = { showSpotDialog = true },
                modifier = Modifier
                    .padding(16.dp),
                containerColor = Color(0xFFff8000),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Neuen Spot hinzufügen")
            }
            if (showSpotDialog) {
                FoodSpotDialog(
                    listId = listId,
                    onDismiss = { showSpotDialog = false },
                    onSave = { newSpot ->
                        viewModel.addSpot(newSpot)
                    }
                )
            }
        },
        content = { innerPadding ->
            val spots by viewModel.spots.collectAsState()
            var searchQuery by remember { mutableStateOf("") }

            val filteredSpots = remember(spots, searchQuery) {
                if (searchQuery.isBlank()) spots
                else spots.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Spots suchen...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSpots) { spot ->
                        FoodSpotCard(
                            spot = spot,
                            deviceCoordinates = deviceCoordinates,
                            onClick = { onSpotClick(spot) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(96.dp))
                    }
                }
            }
        }

    )

}

/**
 * die Karte, die einen FoodSpot darstellt
 * @param spot der darzustellende FoodSpot
 * @param deviceCoordinates die Geräteposition
 * @param modifier Modifier zur Positionierung der UI
 * @param onClick Callback bei Klick auf FoodSpot
 */
@Composable
fun FoodSpotCard(
    spot: FoodSpot,
    deviceCoordinates: Pair<Double, Double>?, // ← NEU
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var distanceKm by remember { mutableStateOf<Double?>(null) }
    LaunchedEffect(spot.address, deviceCoordinates) {
        if (deviceCoordinates != null) {
            val spotCoords = getCoordinatesFromAddress(context, spot.address)
            if (spotCoords != null) {
                val results = FloatArray(1)
                android.location.Location.distanceBetween(
                    deviceCoordinates.first, deviceCoordinates.second,
                    spotCoords.first, spotCoords.second,
                    results
                )
                distanceKm = results[0] / 1000.0
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = spot.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("📍" + spot.address),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⭐ ${spot.rating}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFA500)
            )
            if (distanceKm != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(" ~ %.2f km entfernt", distanceKm),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * gibt das aktuelle Datum zurück
 * @return das aktuelle Datum
 */
fun getCurrentDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(Date())
}

/**
 * Formular zur Erstellung eines neuen FoodSpots
 * @param listId die ID des neuen FoodSpot
 * @param onDismiss Callback bei Klick auf "Abbrechen"
 * @param onSave Callback bei Klick auf "Speichern"
 */
@Composable
fun FoodSpotDialog(
    listId: Int,
    onDismiss: () -> Unit,
    onSave: (FoodSpot) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var menu by remember { mutableStateOf("") }
    val dateAdded = remember { getCurrentDate() }

    var nameError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var ratingError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Neuen Food-Spot hinzufügen") },
        text = {
            Column {
                TextField(value = name, onValueChange = {
                    name = it
                    nameError = false
                }, label = { Text("Name*") }, isError = nameError)

                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = address, onValueChange = {
                    address = it
                    addressError = false
                }, label = { Text("Adresse*") }, isError = addressError)

                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = rating, onValueChange = {
                    rating = it
                    ratingError = false
                }, label = { Text("Bewertung*") }, isError = ratingError)

                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = comments, onValueChange = { comments = it }, label = { Text("Kommentare") })
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = category, onValueChange = { category = it }, label = { Text("Kategorie") })
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = menu, onValueChange = { menu = it }, label = { Text("Speisekarte / Link") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank() || address.isBlank() || rating.isBlank()) {
                    // Fehleranzeige
                    nameError = name.isBlank()
                    addressError = address.isBlank()
                    ratingError = rating.isBlank()
                    return@Button
                }

                val ratingByte = rating.toByteOrNull()
                if (ratingByte == null || ratingByte !in 1..5) {
                    ratingError = true
                    return@Button
                }

                val dateAdded = LocalDateTime.now()

                val newSpot = FoodSpot(
                    name = name,
                    address = address,
                    rating = ratingByte,
                    comment = comments,
                    listId = listId,
                    dateAdded = dateAdded,
                    category = category,
                    menu = menu
                )


                onSave(newSpot)
                onDismiss()  // ← Dialog schließen
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}


