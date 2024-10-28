package com.example.opsc_poe_part_2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory : SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    //below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        //below is a sqlite query, where column names
        // along with their data tupes is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                TITLE + " TEXT," +
                CONTENT + " TEXT," +
                DATE + " TEXT," +
                EMOJI + " TEXT" + ")")

        // WE ARE CALLING SQLite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun deleteTable(table: String){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + table)
    }

    //this method is for adding data in our database
    fun addDiary(title: String, content : String, date: String, emoji: String){

        //below we are creating
        // a content values variable
        val values = ContentValues()

        //we are inserting our values
        // in the form of key-value pair
        values.put(TITLE, title)
        values.put(CONTENT, content)
        values.put(DATE, date)
        values.put(EMOJI, emoji)

        // here we are creating a
        // writable variable of
        // our database as we want to
        //insert value in our database
        val db = this.writableDatabase

        // all values are inserted into databse
        db.insert(TABLE_NAME, null, values)

        //close database
        db.close()
    }

    //below method is to get
    // all data from our database
    fun getEntries(): Cursor? {

        // here we are creating a readable
        /// variable of our database
        // as we want to read value from t
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    companion object{
        // here we have defined variables for our database

        //below is variable for database name
        private val DATABASE_NAME = "SQL_LIGHT"

        //below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "diary_table"

        //below is the variable for id column
        val TITLE = "title"

        // below is the variable for name column
        val CONTENT = "content"

        // below is the variable for age column
        val DATE = "date"

        // below is the variable for age column
        val EMOJI = "emoji"

        //below is the variable for id column
        val ID_COL = "id"
    }
}