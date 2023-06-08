package fr.epf.min1.android_project

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper (context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        val query = ("CREATE TABLE " + TABLE_NAME + " (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                TITLE_COl + " TEXT," +
                RATING_COL + " FLOAT," +
                POSTER_PATH_COL + " TEXT" +
                ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun deleteFavoriteByMovieId(id:Int): Int {
        val db = this.writableDatabase
        val i = db.delete(TABLE_NAME, "$ID_COL=?",null)
        db.close()
        return i
    }

    fun findFavoriteByMovieId(id:Int): Cursor? {
        val db = this.readableDatabase
        val selectionArgs :Array<String> = arrayOf(id.toString())
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL = ?", selectionArgs)
    }

    fun addFilm(id:Int, title: String, rating: Double, poster_path:String ){

        val values = ContentValues()

        values.put(ID_COL, id)
        values.put(TITLE_COl, title)
        values.put(RATING_COL, rating)
        values.put(POSTER_PATH_COL, poster_path)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getFavorites(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    companion object{
        private val DATABASE_NAME = "TMDb_PROJECT"

        private val DATABASE_VERSION = 1

        val TABLE_NAME = "favorites_table"

        const val ID_COL = "id"
        const val TITLE_COl = "title"
        const val RATING_COL = "rating"
        const val POSTER_PATH_COL = "poster_path"
    }
}