package com.example.matchyourmunch.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.matchyourmunch.helper.FoodSpotViewModelInterface

/**
 * Ansicht zur Bearbeitung eines FoodSpots
 * @param spotId die ID des aktuell zu bearbeitenden FoodSpot
 * @param viewModel das ViewModel zur Verwaltung des FoodSpot
 * @param onBack Callback zurücknavigieren
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSpotScreen(
    spotId: Int,
    viewModel: FoodSpotViewModelInterface,
    onBack: () -> Unit
) {
    val spot by viewModel.spot.collectAsState()
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var category by remember { mutableStateOf("") }
    var menu by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var ratingError by remember { mutableStateOf(false) }

    LaunchedEffect(spot) {
        spot?.let {
            name = it.name
            address = it.address
            comment = it.comment
            rating = it.rating.toFloat()
            category = it.category
            menu = it.menu
        }
    }
    if (spot == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lade Daten...")
        }
        return
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
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = Color(0xFFD2D200)
                        )
                    }
                    Text(
                        text = "Spot bearbeiten",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📍 Karte wird automatisch geladen",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Name", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                isError = nameError,
                modifier = Modifier.fillMaxWidth()
                    .testTag("nameInput"),
                singleLine = true
            )
            if (nameError) {
                Text("Name darf nicht leer sein", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Adresse", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = false
                },
                isError = addressError,
                modifier = Modifier.fillMaxWidth()
                    .testTag("addressInput"),
                singleLine = true
            )
            if (addressError) {
                Text("Adresse darf nicht leer sein", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Kommentar", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("commentInput")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Bewertung", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { i ->
                    Icon(
                        imageVector = if (rating >= i) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Stern $i",
                        tint = if (rating >= i) Color(0xFFFFA500) else Color.Gray,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { rating = i.toFloat() }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = rating.toString(),
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Kategorie", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = category, onValueChange = { category = it },
                modifier = Modifier.fillMaxWidth()
                    .testTag("categoryInput"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Speisekarte", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            OutlinedTextField(
                value = menu, onValueChange = { menu = it },
                modifier = Modifier.fillMaxWidth()
                    .testTag("menuInput"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    nameError = name.isBlank()
                    addressError = address.isBlank()
                    ratingError = rating < 1f || rating > 5f
                    if (nameError || addressError || ratingError) {
                        return@Button
                    }
                    spot?.let {
                        val updatedSpot = it.copy(
                            name = name,
                            address = address,
                            comment = comment,
                            rating = rating.coerceIn(1f, 5f).toInt().toByte(),
                            category = category,
                            menu = menu
                        )
                        viewModel.updateSpot(updatedSpot)
                        onBack()
                    }
                }
                ,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Speichern")
            }
        }
    }
}