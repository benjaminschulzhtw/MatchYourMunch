package com.example.matchyourmunch.helper

import com.example.matchyourmunch.model.FoodSpotList
import kotlinx.coroutines.flow.StateFlow

/**
 * Schnittstelle für die ViewModels die mit Listen arbeiten
 */
interface ListViewModelInterface {
    val showDialog: StateFlow<Boolean>
    val listName: StateFlow<String>
    val selectedIcon: StateFlow<String>
    val nameError: StateFlow<String?>
    val editMode: StateFlow<Boolean>
    val searchQuery: StateFlow<String>
    val lists: StateFlow<List<FoodSpotList>>

    /**
     * behandelt den Klick auf den FloatingActionButton
     */
    fun handleActionButton()

    /**
     * reagiert auf Änderungen des Suchbegriffs in der Suchleiste
     * @param query der Suchbegriff
     */
    fun onSearchQueryChanged(query: String)

    /**
     * aktualisiert den Namen der Liste
     * @param name der neue Name
     */
    fun updateListName(name: String)

    /**
     * aktualsiiert das Icon der Liste
     * @param icon das neue Icon
     */
    fun updateSelectedIcon(icon: String)

    /**
     * bestätigt die Erstellung oder Bearbeitung der neuen Liste
     */
    fun confirmNewList()

    /**
     * reagiert auf Abbrechen des Erstell-/Bearbeiten-Formulars
     */
    fun cancelDialog()

    /**
     * bereitet den Bearbeitungsmodus der Liste vor
     * @param name der Name der bearbeitet wird
     */
    fun prepareEdit(name: String)

    /**
     * löscht eine Liste
     * @param name der Name der zu löschenden Liste
     */
    fun deleteList(name: String)
}