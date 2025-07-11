package com.example.matchyourmunch.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository zur Verwaltung von FoodSpotListen
 * Zugriff auf SQLite-Datenbank über ListDbHelper
 * @param context Anwendungskontext für die Datenbankverbindung
 */
class ListRepository(context: Context) {

    private val dbHelper = ListDbHelper(context)

    /**
     * fügt eine Liste zur Datenbank hinzu
     * @param name der Name der Liste
     * @param owner der Ersteller der Liste
     * @param icon das Icon der Liste
     * @return die ID der hinzugefügten Liste
     */
    fun insertList(name: String, owner: String, icon: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ListDbHelper.LIST_NAME, name)
            put(ListDbHelper.LIST_OWNER, owner)
            put(ListDbHelper.LIST_ICON, icon)
        }
        return db.insert(ListDbHelper.TABLE_NAME, null, values)
    }

    /**
     * gibt alle Listen zurück
     * @return eine Liste aller gespeicherten Listen
     */
    fun getAllLists(): List<FoodSpotList> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            ListDbHelper.LIST_NAME,
            ListDbHelper.LIST_OWNER,
            ListDbHelper.LIST_ICON
        )

        val cursor: Cursor = db.query(
            ListDbHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val items = mutableListOf<FoodSpotList>()

        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(ListDbHelper.LIST_NAME))
                val owner = getString(getColumnIndexOrThrow(ListDbHelper.LIST_OWNER))
                val iconName = getString(getColumnIndexOrThrow(ListDbHelper.LIST_ICON))

                items.add(FoodSpotList(name, owner, iconName, emptyList()))
            }
            close()
        }
        return items
    }

    /**
     * löscht alle Listen aus der Datenbank
     */
    fun deleteAll() {
        val db = dbHelper.writableDatabase
        db.delete(ListDbHelper.TABLE_NAME, null, null)
    }

    /**
     * löscht eine Liste anhand ihres Namens
     * @param name der Name der Liste, die gelöscht werden soll
     */
    suspend fun deleteListByName(name: String) {
        withContext(Dispatchers.IO) {
            val db = dbHelper.writableDatabase
            db.delete("lists", "name = ?", arrayOf(name))
        }
    }

    /**
     * aktualisiert eine Liste
     * @param oldName der alte Name der Liste
     * @param newName der neue Name der Liste
     * @param owner der Ersteller der Liste
     * @param icon das Icon der Liste
     * @return die Anzahl der aktualisierten Attribute
     */
    fun updateList(oldName: String, newName: String, owner: String, icon: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ListDbHelper.LIST_NAME, newName)
            put(ListDbHelper.LIST_OWNER, owner)
            put(ListDbHelper.LIST_ICON, icon)
        }

        return db.update(
            ListDbHelper.TABLE_NAME,
            values,
            "${ListDbHelper.LIST_NAME} = ?",
            arrayOf(oldName)
        )
    }

    /**
     * prüft ob eine Liste mit dem angegebenen Namen bereits existiert
     * @param name der Name, der geprüft wird
     * @return true, wenn die Liste bereits existiert
     */
    fun doesListExist(name: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ListDbHelper.TABLE_NAME,
            arrayOf(ListDbHelper.LIST_NAME),
            "${ListDbHelper.LIST_NAME} = ?",
            arrayOf(name),
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * gibt die ID einer Liste anhand ihres Namen zurück
     * @param name der Name der Liste
     * @return die ID der Liste
     */
    fun getListIdByName(name: String): Int? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ListDbHelper.TABLE_NAME,
            arrayOf(ListDbHelper.LIST_ID),
            "${ListDbHelper.LIST_NAME} = ?",
            arrayOf(name),
            null,
            null,
            null
        )
        var id: Int? = null
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ListDbHelper.LIST_ID))
        }
        cursor.close()
        return id
    }

}