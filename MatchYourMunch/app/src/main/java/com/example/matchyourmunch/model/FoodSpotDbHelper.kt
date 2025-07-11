package com.example.matchyourmunch.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.matchyourmunch.helper.DateUtils
import java.time.LocalDateTime

/**
 * Hilfsklasse zur Verarbeitung der SQLite-Datenbank "foodspots"
 * @param context der Anwendungskontext zur Datenbankinitialisierung
 */
class FoodSpotDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    /**
     * legt die Tabelle "foodspots" mit allen Attributen an
     * @param db die zu initialisierende Datenbank
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_ADDRESS TEXT NOT NULL, " +
                    "$COLUMN_RATING INTEGER NOT NULL, " +
                    "$COLUMN_COMMENT TEXT, " +
                    "$COLUMN_DATE_ADDED TEXT, " +
                    "$COLUMN_CATEGORY TEXT, " +
                    "$COLUMN_MENU TEXT, " +
                    "$COLUMN_LIST_ID INTEGER NOT NULL, " +
                    "FOREIGN KEY($COLUMN_LIST_ID) REFERENCES ${ListDbHelper.TABLE_NAME}(${ListDbHelper.LIST_ID}) ON DELETE CASCADE)"
        )

    }

    /**
     * aktualisiert die Datenbank, indem die alte Version gelöscht und als neue Version erstellt wird
     * @param db die Datenbank
     * @param oldVersion die alte Version
     * @param newVersion die neue Version
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "foodspots.db"
        const val DATABASE_VERSION = 5

        const val TABLE_NAME = "foodspots"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_RATING = "rating"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_LIST_ID = "list_id"
        const val COLUMN_DATE_ADDED = "date_added"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_MENU = "menu"

    }

    /**
     * liefert einen FoodSpot anhand seiner ID
     * @param id die ID des FoodSpots
     * @return der FoodSpot mit der ID
     */
    fun getSpotById(id: Int): FoodSpot? {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var spot: FoodSpot? = null

        with(cursor) {
            if (moveToFirst()) {
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val address = getString(getColumnIndexOrThrow(COLUMN_ADDRESS))
                val rating = getInt(getColumnIndexOrThrow(COLUMN_RATING)).toByte()
                val comment = getString(getColumnIndexOrThrow(COLUMN_COMMENT))
                val listId = getInt(getColumnIndexOrThrow(COLUMN_LIST_ID))
                val dateAddedString = getString(getColumnIndexOrThrow(COLUMN_DATE_ADDED))
                val dateAdded = try {
                    LocalDateTime.parse(dateAddedString, DateUtils.formatter)
                } catch (e: Exception) {
                    LocalDateTime.now()
                }
                val category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY))
                val menu = getString(getColumnIndexOrThrow(COLUMN_MENU))
                spot = FoodSpot(id, name, address, rating, comment, listId, dateAdded, category, menu)
            }
            close()
        }

        return spot
    }

}
