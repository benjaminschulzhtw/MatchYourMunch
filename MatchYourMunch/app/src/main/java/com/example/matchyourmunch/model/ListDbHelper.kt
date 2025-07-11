package com.example.matchyourmunch.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Hilfsklasse zur Verarbeitung der SQLite-Datenbank "lists"
 * @context der Anwendungskontext zur Datenbankinitialisierung
 */
class ListDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * legt die Tabelle "lists" mit allen Attributen an
     * @param db die zu initialisierende Datenbank
     */
    override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE $TABLE_NAME (" +
                        "$LIST_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$LIST_NAME TEXT NOT NULL," +
                        "$LIST_OWNER TEXT NOT NULL," +
                        "$LIST_ICON TEXT NOT NULL)"
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
        const val DATABASE_NAME = "lists.db"
        const val DATABASE_VERSION = 9

        const val TABLE_NAME = "lists"
        const val LIST_ID = "id"
        const val LIST_NAME = "name"
        const val LIST_OWNER = "owner"
        const val LIST_ICON = "icon"
    }


}