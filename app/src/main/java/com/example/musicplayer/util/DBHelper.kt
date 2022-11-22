package com.example.musicplayer.util

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.musicplayer.data.Music

class DBHelper(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE musicTBL(id TEXT PRIMARY KEY, title TEXT, artist TEXT, albumId TEXT, duration INTEGER, likes INTEGER)
        """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = """DROP TABLE musicTBL""".trimIndent()
        db?.execSQL(query)
        onCreate(db)
    }

    fun selectAll(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf()
        var cursor: Cursor? = null

        val query = """SELECT * FROM musicTBL""".trimIndent()
        val database = this.readableDatabase
        try {
            cursor = database.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)

                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("musicplayer", "DBHelper selectAll : ${e.printStackTrace()}")
        }
        cursor?.close()
        return musicList
    }

    fun selectLikes(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf()
        var cursor: Cursor? = null
        val query = """SELECT * FROM musicTBL WHERE LIKES = 1""".trimIndent()
        val database = this.readableDatabase
        try {
            cursor = database.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)

                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("musicplayer", "DBHelper selectLikes : ${e.printStackTrace()}")
        }
        cursor?.close()
        return musicList
    }

    fun insertAllRecord(music: Music): Boolean {
        val flag: Boolean
        val query =
            """INSERT INTO musicTBL(id, title, artist, albumId, duration, likes) VALUES('${music.id}', '${music.title}', '${music.artist}', '${music.albumId}', '${music.duration}', '${music.likes}')""".trimIndent()
        val database = this.writableDatabase
        flag = try {
            database.execSQL(query)
            true
        } catch (e: Exception) {
            Log.d("musicplayer", "DBHelper insertAllRecord : ${e.printStackTrace()}")
            false
        }
        return flag
    }

    fun updateLikes(music: Music): Boolean {
        val flag: Boolean
        val query =
            """UPDATE musicTBL SET likes = ${music.likes} WHERE id = '${music.id}'""".trimIndent()
        val database = this.writableDatabase
        flag = try {
            database.execSQL(query)
            true
        } catch (e: Exception) {
            Log.d("musicplayer", "DBHelper updateLikes : ${e.printStackTrace()}")
            false
        }
        return flag
    }

    fun searchMusic(queries: String): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf()
        var cursor: Cursor? = null
        val query =
            """SELECT * FROM musicTBL WHERE title LIKE '${queries}%' OR artist LIKE '${queries}%'""".trimIndent()
        val database = this.readableDatabase
        try {
            cursor = database.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)

                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("musicplayer", "DBHelper searchMusic : ${e.printStackTrace()}")
        }
        cursor?.close()
        return musicList
    }
}