package com.example.musicplayer.view.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.R
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.ActivityMusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class MusicActivity : AppCompatActivity() {
    private var _binding: ActivityMusicBinding? = null
    private val binding get() = _binding!!
    private var playList: MutableList<Parcelable>? = null
    private var position: Int = 0
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null

    companion object {
        const val ALBUM_SIZE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*intent - setting musicList*/
        playList = intent.getParcelableArrayListExtra("playList")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music

        /*binding data at view*/
        binding.tvTitle.text = music?.title
        binding.tvArtist.text = music?.artist
        binding.tvDurationTotal.text = SimpleDateFormat("mm:ss").format(music?.duration)
        binding.tvDuration.text = "00:00"
        val bitmap = music?.getAlbumImage(this, ALBUM_SIZE)
        if (bitmap != null) {
            binding.ivAlbum.setImageBitmap(bitmap)
        } else {
            binding.ivAlbum.setImageResource(R.drawable.ic_music_100)
        }

        /*register musicfile uri - mediaPlayer*/
        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())

        /*change play location on seekbar*/
        binding.seekBar.max = mediaPlayer!!.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.d("musicplayer", "seekbar-play")
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                Log.d("musicplayer", "seekbar-stop")
            }
        })

        /*listButton event*/
        binding.btnList.setOnClickListener {
            mediaPlayer?.stop()
            messengerJob?.cancel()
            finish()
        }

        /*play-pause button event*/
        binding.btnPlayPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_24)
            } else {
                mediaPlayer?.start()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_24)

                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                messengerJob = backgroundScope.launch {
                    while (mediaPlayer?.isPlaying == true) {
                        runOnUiThread {
                            var currentPosition = mediaPlayer?.currentPosition!!
                            binding.seekBar.progress = currentPosition
                            val currentDuration =
                                SimpleDateFormat("mm:ss").format(mediaPlayer!!.currentPosition)
                            binding.tvDuration.text = currentDuration
                        }
                        try {
                            /*set delay*/
                            delay(1000)
                        } catch (e: Exception) {
                            Log.d("musicplayer", "thread error")
                        }
                    }//end of while
                    runOnUiThread {
                        if (mediaPlayer!!.currentPosition >= (binding.seekBar.max - 1000)) {
                            binding.seekBar.progress = 0
                            binding.tvDuration.text = "00:00"
                        }
                        binding.btnPlayPause.setImageResource(R.drawable.ic_play_24)
                    }
                }//end of messengerJob
            }
        }
        binding.btnNext.setOnClickListener {
            nextMusic(2)
        }
        binding.btnPrevious.setOnClickListener {
            nextMusic(1)
        }
    }

    /*rewind forward button event*/
    fun nextMusic(type: Int) {
        val playList: ArrayList<Parcelable> = playList as ArrayList<Parcelable>
        val intent = Intent(binding.root.context, MusicActivity::class.java)
        intent.putExtra("playList", playList)
        val position =
            when (type) {
                1 -> {
                    position - 1
                }
                else -> {
                    position + 1
                }
            }
        when (position) {
            -1 -> Toast.makeText(this, "First Music in the List", Toast.LENGTH_SHORT).show()
            else -> {
                intent.putExtra("position", position)
                binding.root.context.startActivity(intent)
                mediaPlayer?.stop()
                messengerJob?.cancel()
                finish()
            }
        }
    }
}