package com.example.matchyourmunch.ViewModelTests

import com.example.matchyourmunch.model.ListRepository
import com.example.matchyourmunch.viewmodel.ListViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.Rule
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Testklasse für ListViewModel
 */
@ExperimentalCoroutinesApi
class ListViewModelTests {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: ListRepository
    private lateinit var viewModel: ListViewModel


    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        every { repository.getAllLists() } returns emptyList()
        viewModel = ListViewModel(repository)
    }

    /**
     * Korrekter Fehler bei einem leeren Listennamen?
     */
    @Test
    fun leererListenname() = runTest {
        viewModel.updateListName("  ")
        viewModel.confirmNewList()

        advanceUntilIdle()

        assertEquals("Du musst dieses Feld ausfüllen!", viewModel.nameError.value)
        coVerify(exactly = 0) { repository.insertList(any(), any(), any()) }
    }

    /**
     * Korrekter Fehler bei einem gleichen Listennamen?
     */
    @Test
    fun doppelterListenname() = runTest {
        every { repository.doesListExist("Meine Liste") } returns true
        viewModel.updateListName("Meine Liste")
        viewModel.confirmNewList()

        advanceUntilIdle()

        assertEquals("Es existiert bereits eine Liste mit diesem Namen!", viewModel.nameError.value)
        coVerify(exactly = 0) { repository.insertList(any(), any(), any()) }
    }

    /**
     * Korrekte Speicherung bei einem gültigen und neuen Listennamen?
     */
    @Test
    fun gueltigerUndNeuerListenname() = runTest {
        every { repository.doesListExist("Neue Liste") } returns false
        viewModel.updateListName("Neue Liste")
        viewModel.confirmNewList()

        advanceUntilIdle()

        assertEquals(null, viewModel.nameError.value)
        coVerify(exactly = 1) { repository.insertList("Neue Liste", any(), any()) }
    }

    /**
     * Setzt "prepareEdit" die Zustände korrekt, wenn ein Element bearbeitet wird?
     */
    @Test
    fun korrekteFelderPrepareEdit() {
        viewModel.prepareEdit("Pizza-Liste")

        assertTrue(viewModel.editMode.value)
        assertEquals("Pizza-Liste", viewModel.listName.value)
        assertTrue(viewModel.showDialog.value)
    }

    /**
     * Setzt "cancelDialog" alle relevanten States wieder zurück?
     */
    @Test
    fun cancelDialogZustandZuruecksetzen() {
        viewModel.updateListName("Test")
        viewModel.cancelDialog()

        assertEquals("", viewModel.listName.value)
        assertFalse(viewModel.showDialog.value)
        assertEquals(null, viewModel.nameError.value)
    }

    /**
     * Ruft "deleteList" das Löschen im Repository korrekt auf und lädt die Liste neu?
     */
    @Test
    fun deleteList() = runTest {
        viewModel.deleteList("TestListe")

        advanceUntilIdle()

        coVerify { repository.deleteListByName("TestListe") }
        verify { repository.getAllLists() }
    }

    /**
     * Wird das Icon korrekt aktualisiert?
     */
    @Test
    fun updateSelectedIcon() {
        viewModel.updateSelectedIcon("Home")
        assertEquals("Home", viewModel.selectedIcon.value)
    }

    /**
     * Wird die Suchanfrage korrekt aktualisiert?
     */
    @Test
    fun onSearchQueryChange() {
        viewModel.onSearchQueryChanged("Sushi")
        assertEquals("Sushi", viewModel.searchQuery.value)
    }
}

