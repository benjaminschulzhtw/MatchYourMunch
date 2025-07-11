package com.example.matchyourmunch.model

import android.content.ContentValues
import android.content.Context
import com.example.matchyourmunch.helper.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * Repository zur Verwaltung von FoodSpots
 * Zugriff auf SQLite-Datenbank über FoodSpotDbHelper
 * @param context Anwendungskontext für die Datenbankverbindung
 */
class FoodSpotRepository(context: Context) {

    private val dbHelper = FoodSpotDbHelper(context)

    /**
     * neuen FoodSpot in Datenbank einfügen
     * @param spot der FoodSpot, der eingefügt wird
     */
    suspend fun insertSpot(spot: FoodSpot) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(FoodSpotDbHelper.COLUMN_NAME, spot.name)
                put(FoodSpotDbHelper.COLUMN_ADDRESS, spot.address)
                put(FoodSpotDbHelper.COLUMN_RATING, spot.rating)
                put(FoodSpotDbHelper.COLUMN_COMMENT, spot.comment)
                put(FoodSpotDbHelper.COLUMN_DATE_ADDED, spot.dateAdded.format(DateUtils.formatter))
                put(FoodSpotDbHelper.COLUMN_CATEGORY, spot.category)
                put(FoodSpotDbHelper.COLUMN_MENU, spot.menu)
                put(FoodSpotDbHelper.COLUMN_LIST_ID, spot.listId)
            }
            db.insert(FoodSpotDbHelper.TABLE_NAME, null, values)
        }
    }

    /**
     * gibt alle FoodSpots einer Liste wieder
     * @param listId die ID der Liste, deren FoodSpots zurückgegeben werden sollen
     * @return die Liste, die die FoodSpots enthält
     */
    suspend fun getSpotsForList(listId: Int): List<FoodSpot> {
        return withContext(Dispatchers.IO) {
            val db = dbHelper.readableDatabase
            val projection = arrayOf(
                FoodSpotDbHelper.COLUMN_ID,
                FoodSpotDbHelper.COLUMN_NAME,
                FoodSpotDbHelper.COLUMN_ADDRESS,
                FoodSpotDbHelper.COLUMN_RATING,
                FoodSpotDbHelper.COLUMN_COMMENT,
                FoodSpotDbHelper.COLUMN_DATE_ADDED,
                FoodSpotDbHelper.COLUMN_CATEGORY,
                FoodSpotDbHelper.COLUMN_MENU,
                FoodSpotDbHelper.COLUMN_LIST_ID,
            )

            val cursor = db.query(
                FoodSpotDbHelper.TABLE_NAME,
                projection,
                "${FoodSpotDbHelper.COLUMN_LIST_ID} = ?",
                arrayOf(listId.toString()),
                null,
                null,
                null
            )

            val spots = mutableListOf<FoodSpot>()
            with(cursor) {
                while (moveToNext()) {
                    val id = getInt(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_ID))
                    val name = getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_NAME))
                    val address = getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_ADDRESS))
                    val rating =
                        getInt(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_RATING)).toByte()
                    val comment = getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_COMMENT))

                    val dateString =
                        getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_DATE_ADDED))
                    val dateAdded = try {
                        LocalDateTime.parse(dateString, DateUtils.formatter)
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }
                    val category =
                        getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_CATEGORY))
                    val menu = getString(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_MENU))
                    val listId = getInt(getColumnIndexOrThrow(FoodSpotDbHelper.COLUMN_LIST_ID))

                    spots.add(
                        FoodSpot(
                            id,
                            name,
                            address,
                            rating,
                            comment,
                            listId,
                            dateAdded,
                            category,
                            menu
                        )
                    )
                }
                close()
            }
            spots
        }
    }

    /**
     * löscht alle FoodSpots einer Liste aus der Datenbank
     * @param listId die Liste, deren FoodSpots gelöscht werden sollen
     */
    suspend fun deleteAllForList(listId: Int) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete(
                FoodSpotDbHelper.TABLE_NAME,
                "${FoodSpotDbHelper.COLUMN_LIST_ID} = ?",
                arrayOf(listId.toString())
            )
        }
    }

    /**
     * löscht einen FoodSpot
     * @param spot der FoodSpot, der gelöscht werden soll
     */
    fun deleteSpot(spot: FoodSpot) {
        val db = dbHelper.writableDatabase
        db.delete(
            FoodSpotDbHelper.TABLE_NAME,
            "${FoodSpotDbHelper.COLUMN_ID} = ?",
            arrayOf(spot.id.toString())
        )
    }

    /**
     * aktualisiert einen FoodSpot
     * @param spot der FoodSpot, der aktualisiert werden soll
     */
    suspend fun updateSpot(spot: FoodSpot) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(FoodSpotDbHelper.COLUMN_NAME, spot.name)
                put(FoodSpotDbHelper.COLUMN_ADDRESS, spot.address)
                put(FoodSpotDbHelper.COLUMN_RATING, spot.rating)
                put(FoodSpotDbHelper.COLUMN_COMMENT, spot.comment)
                put(FoodSpotDbHelper.COLUMN_DATE_ADDED, spot.dateAdded.format(DateUtils.formatter))
                put(FoodSpotDbHelper.COLUMN_CATEGORY, spot.category)
                put(FoodSpotDbHelper.COLUMN_MENU, spot.menu)
                put(FoodSpotDbHelper.COLUMN_LIST_ID, spot.listId)
            }

            db.update(
                FoodSpotDbHelper.TABLE_NAME,
                values,
                "${FoodSpotDbHelper.COLUMN_ID} = ?",
                arrayOf(spot.id.toString())
            )
        }
    }

    /**
     * liefert einen FoodSpot anhand seiner ID
     * @param id die ID des FoodSpots
     * @return der FoodSpot
     */
    fun getSpotById(id: Int): FoodSpot? {
        return dbHelper.getSpotById(id)
    }
}

