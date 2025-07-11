package com.example.matchyourmunch.ViewModelTests

import com.example.matchyourmunch.model.FoodSpot
import com.example.matchyourmunch.model.FoodSpotRepository
import com.example.matchyourmunch.viewmodel.FoodSpotViewModel
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

/**
 * Testklasse für FoodSpotViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FoodSpotViewModelTests {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var repository: FoodSpotRepository
    private lateinit var viewModel: FoodSpotViewModel

    private val listId = 1
    private val spotId = 42

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        coEvery { repository.getSpotsForList(any()) } returns emptyList()
        coEvery { repository.getSpotById(any()) } returns null

        viewModel = FoodSpotViewModel(repository, listId, spotId)
    }

    /**
     * Wird die Funktion "getSpotsForList" korrekt nach Initialisierung aufgerufen?
     */
    @Test
    fun loadSpotsCalledOnInit() = runTest {
        advanceUntilIdle()
        coVerify { repository.getSpotsForList(listId) }
    }

    /**
     * Wird ein FoodSpot beim Hinzufügen korrekt gespeichert?
     */
    @Test
    fun addSpotInsertsAndReloads() = runTest {
        val spot = createTestSpot()
        viewModel.addSpot(spot)

        advanceUntilIdle()

        coVerify {
            repository.insertSpot(spot)
            repository.getSpotsForList(listId)
        }
    }

    /**
     * Löscht "deleteAllSpots" korrekt alle FoodSpots einer Liste und lädt diese Liste dann neu?
     */
    @Test
    fun deleteAllSpotsClearsListAndReloads() = runTest {
        viewModel.deleteAllSpots()

        advanceUntilIdle()

        coVerify {
            repository.deleteAllForList(listId)
            repository.getSpotsForList(listId)
        }
    }

    /**
     * Löscht "deleteSpot" korrekt einen Spot aus der Liste und lädt diese dann neu?
     */
    @Test
    fun deleteSpotRemovesAndReloads() = runTest {
        val spot = createTestSpot()
        viewModel.deleteSpot(spot)

        advanceUntilIdle()

        coVerify {
            repository.deleteSpot(spot)
            repository.getSpotsForList(listId)
        }
    }

    /**
     * Korrekte Funktion von "updateSpot"?
     */
    @Test
    fun updateSpotUpdatesAndReloads() = runTest {
        val spot = createTestSpot()
        viewModel.updateSpot(spot)

        advanceUntilIdle()

        coVerify {
            repository.updateSpot(spot)
            repository.getSpotsForList(listId)
        }

        assertEquals(spot, viewModel.spot.value)
    }

    private fun createTestSpot(): FoodSpot {
        return FoodSpot(
            name = "Test Spot",
            address = "Teststraße 1",
            rating = 4,
            comment = "Testkommentar",
            listId = listId,
            dateAdded = LocalDateTime.now(),
            category = "TestKategorie",
            menu = "Testmenü"
        )
    }
}
