package com.example.matchyourmunch.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.platform.testTag
import com.example.matchyourmunch.helper.ListViewModelInterface

/**
 * Hauptbildschirm: zeigt alle Listen
 * @param viewModel das ViewModel zur Listenverwaltung
 * @param onListClick Callback beim Klicken auf eine Liste
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun MainScreen(viewModel: ListViewModelInterface, onListClick: (String) -> Unit) {
        //State-Objekte aus dem ViewModel
        val showDialog by viewModel.showDialog.collectAsState()
        val listName by viewModel.listName.collectAsState()
        val query by viewModel.searchQuery.collectAsState()
        val lists by viewModel.lists.collectAsState()
        val nameError by viewModel.nameError.collectAsState()
        val editMode by viewModel.editMode.collectAsState()

        //Suchlisten-Filter
        val filteredLists = remember(lists, query) {
        if (query.isBlank()) lists
        else lists.filter { it.name.contains(query, ignoreCase = true) }
        }
        //Icons
        val availableIcons = listOf("Default", "Star", "Favorite", "Home", "Person")
        //Bestätigungsdialog beim Löschen einer Liste
        var showDeleteDialog by remember { mutableStateOf(false) }
        //die zu löschende Liste
        var listToDelete by remember { mutableStateOf<String?>(null) }

    if (showDeleteDialog && listToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    listToDelete = null
                },
                title = { Text("Liste löschen") },
                text = { Text("Möchtest du die Liste wirklich löschen?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteList(listToDelete!!)
                            showDeleteDialog = false
                            listToDelete = null
                        }
                    ) {
                        Text("Löschen", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            listToDelete = null
                        }
                    ) {
                        Text("Abbrechen")
                    }
                }
            )
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(top = 12.dp, bottom = 8.dp)
                        ) {
                            Text(
                                text = "Deine Listen",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFFff8000),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hier findest du alle deine gespeicherten Listen",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextField(
                                value = query,
                                onValueChange = { viewModel.onSearchQueryChanged(it) },
                                placeholder = { Text("Listen suchen...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .testTag("SearchField"),
                                singleLine = true
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.LightGray)
                        )
                    }
                }

                if (lists.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mode,
                                contentDescription = "Leerer Zustand",
                                tint = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Noch ziemlich leer hier...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(filteredLists) { _, listItem ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ListCard(
                                name = listItem.name,
                                owner = listItem.owner,
                                iconName = listItem.icon,
                                onClick = { onListClick(listItem.name) },
                                onEditClick = { viewModel.prepareEdit(it) },
                                onDeleteClick = {
                                    listToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(96.dp)) // ca. Höhe des FAB + extra Luft
                    }
                }
            }
            FloatingActionButton(
                onClick = { viewModel.handleActionButton() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFFff8000),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "AddListButton")
            }
            if (showDialog) {
                val selectedIcon by viewModel.selectedIcon.collectAsState()

                CreateListDialog(
                    name = listName,
                    onNameChange = { viewModel.updateListName(it) },
                    selectedIcon = selectedIcon,
                    onIconSelect = { viewModel.updateSelectedIcon(it) },
                    nameError = nameError,
                    editMode = editMode,
                    availableIcons = availableIcons,
                    onConfirm = { viewModel.confirmNewList() },
                    onDismiss = { viewModel.cancelDialog() }
                )
            }

        }
    }


/**
 * die Karte zur Darstellung Liste
 * @param name der Name der darzustellenden Liste
 * @param owner der Ersteller der darzustellenden Liste
 * @param iconName der Name des Icons der darzustellenden Liste
 * @param onClick der Klick auf die Karte
 * @param onEditClick der Klick auf "Bearbeiten"
 * @param onDeleteClick der Klick auf "Löschen"
 */
@Composable
    fun ListCard(
        name: String,
        owner: String,
        iconName: String,
        onClick: () -> Unit,
        onEditClick: (String) -> Unit,
        onDeleteClick: (String) -> Unit // <- NEU
    ) {
        val icon = when (iconName) {
            "Favorite" -> Icons.Default.Favorite
            "Home" -> Icons.Default.Home
            "Person" -> Icons.Default.Person
            "Star" -> Icons.Default.Star
            "Default" -> Icons.Default.List
            else -> Icons.Default.List
        }

        var menuExpanded by remember { mutableStateOf(false) }

        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Icon(icon, contentDescription = "List-Icon", tint = Color(0xFFF0F0F0))
                    Text(text = name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Von: $owner",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = "Listen-Icon",
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                )
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "ListCardOptions"
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
                                onEditClick(name)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Löschen") },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick(name)
                            }
                        )
                    }
                }
            }
        }
    }

/**
 * das Formular zum Erstellen oder Bearbeiten einer Liste
 * @param name der aktuelle Name der Liste
 * @param onNameChange Callback zur Aktualisierung des Namens der Liste
 * @param selectedIcon das aktuell ausgewählte Icon
 * @param onIconSelect Callback beim Auswählen eines neuen Icons
 * @param nameError Fehlermeldung, falls Name der Liste ungültig ist
 * @param editMode gibt an, ob sich die Liste im Bearbeitungsmodus befindet
 * @param availableIcons die Liste der auszuwählenden Icons der Liste
 * @param onConfirm der Klick auf "Speichern"
 * @param onDismiss der Klick auf "Abbrechen"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListDialog(
    name: String,
    onNameChange: (String) -> Unit,
    selectedIcon: String,
    onIconSelect: (String) -> Unit,
    nameError: String?,
    editMode: Boolean,
    availableIcons: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neue Liste erstellen") },
        text = {
            Column {
                Text("Wie soll die Liste heißen?")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.testTag("ListNameInput"),
                    placeholder = { Text("z.B. Favorites") },
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let {
                            Text(text = it, color = Color.Red)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Wähle ein Icon:")
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedIcon,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Icon auswählen") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableIcons.forEach { icon ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (icon) {
                                                "Favorite" -> Icons.Default.Favorite
                                                "Home" -> Icons.Default.Home
                                                "Person" -> Icons.Default.Person
                                                "Star" -> Icons.Default.Star
                                                else -> Icons.Default.List
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(icon)
                                    }
                                },
                                onClick = {
                                    onIconSelect(icon)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("ConfirmListButton"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff8000))
            ) {
                Text(if (editMode) "Speichern" else "Erstellen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFff8000))
            ) {
                Text("Abbrechen")
            }
        }
    )
}

