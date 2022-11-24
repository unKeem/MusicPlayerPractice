package com.example.musicplayer.view.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.data.DBHelper
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.view.adapter.MusicRecyclerAdapter
import com.example.musicplayer.view.decoration.Decoration

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var musicRecyclerAdapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf()
    private val database: DBHelper by lazy {
        DBHelper(this, DATABASE_NAME, DATABASE_VERSION)
    }

    companion object {
        const val REQUEST_PERMISSION = 1004
        const val DATABASE_NAME = "musicDB"
        const val DATABASE_VERSION = 1
        const val LIKES_EMPTY = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            startMusicPlayer()
        }
    }

    private fun startMusicPlayer() {
        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)
        }
    }

    /*permission*/
    private fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
    }

    /*request permission*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startProcess()
        } else {
            Toast.makeText(this, "Requires permission to use the app", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*loading music file from DB / setting recyclerView*/
    private fun startProcess() {
        musicList = database.selectAll()
        if (musicList.isNullOrEmpty()) {
            val tempMusicList = getMusicList()
            if (tempMusicList?.isNotEmpty() == true) {
                for (i in 0 until tempMusicList.size) {
                    val music = tempMusicList[i]
                    database.insertAllRecord(music)
                }
                musicList = tempMusicList
            } else {
                Toast.makeText(this, "Music files do not exist", Toast.LENGTH_SHORT).show()
            }
        }
        musicRecyclerAdapter = MusicRecyclerAdapter(this, musicList)

        binding.recyclerView.apply {
            adapter = musicRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            /*decoration*/
            addItemDecoration(Decoration())
        }
    }

    /*put the information of musicFiles in list*/
    private fun getMusicList(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf()

        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = contentResolver.query(musicUri, projection, null, null, null)

        if (cursor?.count!! > 0) {
            while (cursor!!.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)

                val music = Music(id, title, artist, albumId, duration, LIKES_EMPTY)
                musicList?.add(music)
            }
        } else {
            musicList = null
        }
        cursor.close()
        return musicList
    }

    /*setting actionBar menu items*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_likes -> {
                musicList?.clear()
                database.selectLikes()?.let { musicList?.addAll(it) }
                musicRecyclerAdapter.notifyDataSetChanged()
            }
            R.id.menu_list -> {
                musicList?.clear()
                database.selectAll()?.let { musicList?.addAll(it) }
                musicRecyclerAdapter.notifyDataSetChanged()
            }
            R.id.menu_search -> Log.d("musicplayer", "MainActivity onOptionItemSelected()")
        }
        return super.onOptionsItemSelected(item)
    }

    /*get the list of searched music from the database & notifydatasetchanged*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_music, menu)
        val searchMenu = menu?.findItem(R.id.menu_search)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    musicList?.clear()
                    database.selectAll()?.let { musicList?.addAll(it) }
                    musicRecyclerAdapter.notifyDataSetChanged()
                } else {
                    musicList?.clear()
                    database.searchMusic(query)?.let { musicList?.addAll(it) }
                    musicRecyclerAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}