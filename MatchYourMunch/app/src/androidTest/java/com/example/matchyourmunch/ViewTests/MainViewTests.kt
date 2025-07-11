package com.example.matchyourmunch.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matchyourmunch.model.FoodSpotList
import com.example.matchyourmunch.helper.ListViewModelInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

/**
 * UI-Tests für MainScreen
 */
@RunWith(AndroidJUnit4::class)
class MainViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeListViewModel : ListViewModelInterface {

        override val showDialog = MutableStateFlow(false)
        override val listName = MutableStateFlow("")
        override val selectedIcon = MutableStateFlow("Default")
        override val nameError = MutableStateFlow<String?>(null)
        override val editMode = MutableStateFlow(false)
        override val searchQuery = MutableStateFlow("")
        override val lists: StateFlow<List<FoodSpotList>> = MutableStateFlow(
            listOf(
                FoodSpotList("Pizza Orte", "Benja", "Favorite", emptyList()),
                FoodSpotList("Sushi Tipps", "Benja", "Star", emptyList())
            )
        )

        var handleActionCalled = false
        var lastQuery: String? = null

        override fun handleActionButton() {
            handleActionCalled = true
            showDialog.value = true
        }

        override fun onSearchQueryChanged(query: String) {
            lastQuery = query
            searchQuery.value = query
        }

        override fun updateListName(name: String) {}
        override fun updateSelectedIcon(icon: String) {}
        override fun confirmNewList() {}
        override fun cancelDialog() {}
        override fun prepareEdit(name: String) {}
        override fun deleteList(name: String) {}
    }

    private val viewModel = FakeListViewModel()

    /**
     * Wird das Formular beim Erstellen einer neuen Liste korrekt geöffnet?
     */
    @Test
    fun fabClick_opensDialog_callsHandleAction() {
        composeTestRule.setContent {
            MainScreen(viewModel = viewModel, onListClick = {})
        }

        composeTestRule
            .onNodeWithContentDescription("AddListButton")
            .performClick()

        assertTrue(viewModel.handleActionCalled)
    }

    /**
     * Löst das Suchfeld korrekt eine Aktualisierung im ViewModel aus?
     */
    @Test
    fun searchField_updatesQueryInViewModel() {
        composeTestRule.setContent {
            MainScreen(viewModel = viewModel, onListClick = {})
        }

        val input = "Sushi"
        composeTestRule
            .onNodeWithTag("SearchField")
            .performTextInput(input)

        assertEquals(input, viewModel.lastQuery)
    }

    /**
     * Werden Listen korrekt angezeigt?
     */
    @Test
    fun lists_areDisplayedCorrectly() {
        composeTestRule.setContent {
            MainScreen(viewModel = viewModel, onListClick = {})
        }

        composeTestRule.onNodeWithText("Pizza Orte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sushi Tipps").assertIsDisplayed()
    }

    /**
     * Öffnet sich der Dialog beim Klick auf das Menü korrekt?
     */
    @Test
    fun listCard_showsOptions_onMenuClick() {
        composeTestRule.setContent {
            MainScreen(viewModel = viewModel, onListClick = {})
        }

        composeTestRule
            .onAllNodesWithContentDescription("ListCardOptions")[0]
            .performClick()

        composeTestRule.onNodeWithText("Bearbeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Löschen").assertIsDisplayed()
    }
}
