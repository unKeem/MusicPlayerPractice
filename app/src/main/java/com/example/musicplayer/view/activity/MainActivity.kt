package com.example.musicplayer.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayer.R
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.databinding.ActivityMusicBinding
import com.example.musicplayer.view.adapter.MusicRecyclerAdapter
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var _binding:ActivityMainBinding?= null
    private val binding get() = _binding!!
    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var musicRecyclerAdapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf()

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
    }

}