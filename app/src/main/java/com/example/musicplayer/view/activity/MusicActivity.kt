package com.example.musicplayer.view.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.example.musicplayer.R
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.ActivityMusicBinding
import kotlinx.coroutines.Job

class MusicActivity : AppCompatActivity() {
    private var _binding: ActivityMusicBinding? = null
    private val binding get() = _binding!!
    private var playList:MutableList<Parcelable>? = null
    private var position: Int = 0
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply{

        }
    }

}