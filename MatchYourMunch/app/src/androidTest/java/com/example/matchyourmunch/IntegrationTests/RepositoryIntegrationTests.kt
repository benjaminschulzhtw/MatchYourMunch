package com.example.matchyourmunch

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.matchyourmunch.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Integrationstests für die Repositories
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryIntegrationTests {

    private lateinit var context: Context
    private lateinit var listRepo: ListRepository
    private lateinit var spotRepo: FoodSpotRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        listRepo = ListRepository(context)
        spotRepo = FoodSpotRepository(context)
        listRepo.deleteAll()
    }

    @After
    fun tearDown() {
        listRepo.deleteAll()
    }

    /**
     * Funktioniert das Einfügen einer Liste in die Datenbank?
     */
    @Test
    fun insertList_and_fetchItFromDb() {
        val rowId = listRepo.insertList("Testliste", "Benja", "test_icon.png")
        assertTrue(rowId != -1L)
        val lists = listRepo.getAllLists()
        assertEquals(1, lists.size)
        assertEquals("Testliste", lists.first().name)
    }

    /**
     * Funktioniert das Aktualisieren einer Liste?
     */
    @Test
    fun updateList_and_verifyChanges() {
        listRepo.insertList("Altname", "Benja", "icon_alt")
        val updateCount = listRepo.updateList("Altname", "Neuname", "Benja", "icon_neu")

        assertEquals(1, updateCount)
        val lists = listRepo.getAllLists()
        assertEquals("Neuname", lists.first().name)
    }

    /**
     * Funktioniert das Löschen einer Liste?
     */
    @Test
    fun deleteList_removesListFromDb() = runTest {
        listRepo.insertList("Löschmich", "Benja", "icon")
        listRepo.deleteListByName("Löschmich")

        val lists = listRepo.getAllLists()
        assertTrue(lists.none { it.name == "Löschmich" })
    }

    /**
     * Funktioniert das Einfügen eines FoodSpots in eine bereits existierende Liste?
     */
    @Test
    fun insertFoodSpot_and_fetchItByListId() = runTest {
        val listId = listRepo.insertList("Testliste", "Benja", "test_icon.png")
        val listIdFetched = listRepo.getListIdByName("Testliste")!!
        assertEquals(true, listRepo.doesListExist("Testliste"))

        val spot = FoodSpot(
            name = "Test Spot",
            address = "Teststraße 123",
            rating = 4,
            comment = "Super Laden",
            listId = listIdFetched,
            dateAdded = LocalDateTime.now(),
            category = "Asiatisch",
            menu = "Testspeisekarte"
        )

        spotRepo.insertSpot(spot)
        val spots = spotRepo.getSpotsForList(listIdFetched)
        assertEquals(1, spots.size)
        assertEquals("Test Spot", spots.first().name)
    }

    /**
     * Funktioniert das Aktualisieren eines FoodSpots?
     */
    @Test
    fun updateFoodSpot_and_verifyChanges() = runTest {
        val listId = listRepo.insertList("Testliste", "Benja", "test_icon.png")
        val listIdFetched = listRepo.getListIdByName("Testliste")!!

        val spot = FoodSpot(
            name = "Original Spot",
            address = "Originalstraße",
            rating = 3,
            comment = "Alt",
            listId = listIdFetched,
            dateAdded = LocalDateTime.now(),
            category = "Burger",
            menu = "menu.com"
        )

        spotRepo.insertSpot(spot)

        val inserted = spotRepo.getSpotsForList(listIdFetched).first()
        val updated = inserted.copy(name = "Neuer Spot", comment = "Jetzt besser")

        spotRepo.updateSpot(updated)

        val reloaded = spotRepo.getSpotById(updated.id)
        assertEquals("Neuer Spot", reloaded?.name)
        assertEquals("Jetzt besser", reloaded?.comment)
    }

    /**
     * Funktioniert das Löschen eines FoodSpots?
     */
    @Test
    fun deleteFoodSpot_removesOnlyThatSpot() = runTest {
        val listId = listRepo.insertList("Testliste", "Benja", "icon")
        val spot = FoodSpot(
            name = "A",
            address = "Addr",
            rating = 5.toByte(),
            comment = "c",
            listId = listId.toInt(),
            dateAdded = LocalDateTime.now(),
            category = "Cat",
            menu = "Menu"
        )
        spotRepo.insertSpot(spot)

        val inserted = spotRepo.getSpotsForList(listId.toInt()).first()
        spotRepo.deleteSpot(inserted)

        val spotsAfter = spotRepo.getSpotsForList(listId.toInt())
        assertTrue(spotsAfter.isEmpty())
    }

    /**
     * Bleibt ein FoodSpot bestehen, wenn ein gleichnamiger gelöscht wird?
     */
    @Test
    fun deletingOneOfDuplicateNamedSpots_doesNotDeleteOthers() = runTest {
        val listId = listRepo.insertList("Liste", "Benja", "icon").toInt()
        val spot1 = FoodSpot(
            name = "Doppelt",
            address = "A",
            rating = 4.toByte(),
            comment = "x",
            listId = listId,
            dateAdded = LocalDateTime.now(),
            category = "K",
            menu = "M"
        )
        val spot2 = FoodSpot(
            name = "Doppelt",
            address = "B",
            rating = 5.toByte(),
            comment = "y",
            listId = listId,
            dateAdded = LocalDateTime.now(),
            category = "K",
            menu = "M"
        )

        spotRepo.insertSpot(spot1)
        spotRepo.insertSpot(spot2)

        val all = spotRepo.getSpotsForList(listId)
        spotRepo.deleteSpot(all.first())

        val remaining = spotRepo.getSpotsForList(listId)
        assertEquals(1, remaining.size)
        assertEquals("Doppelt", remaining.first().name)
    }
}
