package com.example.matchyourmunch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchyourmunch.model.FoodSpotList
import com.example.matchyourmunch.model.ListRepository
import com.example.matchyourmunch.helper.ListViewModelInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Zugriff und Verwaltung von Listen
 * Kommunikation mit ListRepository
 * @param repository die Datenquelle aller Listen
 */
class ListViewModel(private val repository: ListRepository) : ViewModel(), ListViewModelInterface {

    //die Liste der gespeicherten Listen
    private val _lists = MutableStateFlow<List<FoodSpotList>>(emptyList())
    override val lists: StateFlow<List<FoodSpotList>> = _lists

    //true, wenn Formular angezeigt werden soll
    private val _showDialog = MutableStateFlow(false)
    override val showDialog: StateFlow<Boolean> = _showDialog

    //Name der Liste
    private val _listName = MutableStateFlow("")
    override val listName: StateFlow<String> = _listName

    //Fehlertext, wenn Name leer oder vergeben
    private val _nameError = MutableStateFlow<String?>(null)
    override val nameError: StateFlow<String?> = _nameError

    //ausgewähltes Icon der Liste
    private val _selectedIcon = MutableStateFlow("Default")
    override val selectedIcon: StateFlow<String> = _selectedIcon

    //aktuelle Suchanfrage
    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery

    //true, wenn Bearbeitungsmodus aktiv
    private val _editMode = MutableStateFlow(false)
    override val editMode: StateFlow<Boolean> = _editMode

    //Name der Liste, die bearbeitet wird
    private val _listBeingEdited = MutableStateFlow<String?>(null)

    init {
        loadLists()
    }

    /**
     * Icon aktualisieren
     * @param icon das Icon, das aktualisiert wird
     */
    override fun updateSelectedIcon(icon: String) {
        _selectedIcon.value = icon;
    }

    /**
     * Listen laden
     */
    fun loadLists() {
        _lists.value = repository.getAllLists()
    }

    /**
     * Formular zur Erstellung einer neuen Liste öffnen
     */
    override fun handleActionButton() {
        _showDialog.value = true
        _nameError.value = null
    }

    /**
     * Namen einer Liste aktualisieren
     * @param der Name, der aktualisiert wird
     */
    override fun updateListName(name: String) {
        _listName.value = name
        _nameError.value = null
    }

    /**
     * erstellen oder bearbeiten einer neuen Liste
     */
    override fun confirmNewList() {
        viewModelScope.launch {
            val name = _listName.value.trim()
            val icon = _selectedIcon.value
            val deviceName = android.os.Build.MODEL ?: "Unbekannt"

            if (name.isBlank()) {
                _nameError.value = "Du musst dieses Feld ausfüllen!"
                return@launch
            }

            if (!_editMode.value && repository.doesListExist(name)) {
                _nameError.value = "Es existiert bereits eine Liste mit diesem Namen!"
                return@launch
            }

            if (_editMode.value && _listBeingEdited.value != null) {
                repository.updateList(_listBeingEdited.value!!, name, deviceName, icon)
            } else {
                repository.insertList(name, deviceName, icon)
            }

            _listName.value = ""
            _selectedIcon.value = "Default"
            _showDialog.value = false
            _editMode.value = false
            _listBeingEdited.value = null
            _nameError.value = null

            loadLists()
        }
    }

    /**
     * Formular abbrechen
     */
    override fun cancelDialog() {
        _showDialog.value = false
        _listName.value = ""
        _nameError.value = null
    }

    /**
     * Liste löschen
     * @param name der Name der Liste, die gelöscht wird
     */
    override fun deleteList(name: String) {
        viewModelScope.launch {
            repository.deleteListByName(name)
            loadLists()
        }
    }

    /**
     * Bearbeitungsmodus einer Liste öffnen
     * @param name der Name, der im Bearbeitungsmodus ergänzt wird
     */
    override fun prepareEdit(name: String) {
        _listName.value = name
        _listBeingEdited.value = name
        _editMode.value = true
        _showDialog.value = true
    }

    /**
     * Suchbegriff aktualisieren um Listen zu filtern
     * @param query der Suchbegriff
     */
    override fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}