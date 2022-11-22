package com.example.musicplayer.view.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.R
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.ActivityMusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class MusicActivity : AppCompatActivity() {
    private var _binding: ActivityMusicBinding? = null
    private val binding get() = _binding!!
    private var playList: MutableList<Parcelable>? = null
    private var position: Int = 0
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null

    companion object {
        const val ALBUM_SIZE = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setMusicController()
            setMusicWindow()
        }
    }

    private fun setMusicController() {
        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())

        binding.seekBar.apply {
            max = mediaPlayer?.duration!!

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, process: Int, flag: Boolean) {
                    if (flag) mediaPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    Log.d("musicplayer", "seekBar changeListener start")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    Log.d("musicplayer", "seekBar changeListener stop")
                }
            })
        }
        binding.btnList.setOnClickListener {
            messengerJob?.cancel()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            finish()
        }
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
                        val currentPosition = mediaPlayer?.currentPosition!!
                        binding.seekBar.progress = currentPosition

                        runOnUiThread {
                            val currentDuration = SimpleDateFormat(
                                "mm:ss", Locale.getDefault()
                            ).format(mediaPlayer!!.currentPosition)
                            binding.tvDuration.text = currentDuration
                        }
                        try {
                            delay(1000)
                        } catch (e: Exception) {
                            Log.d("musicplayer", "duration : ${e.printStackTrace()}")
                        }
                    }
                    runOnUiThread {
                        binding.tvDuration.text = resources.getString(R.string.tv_duration_start)
                    }
                    binding.btnPlayPause.setImageResource(R.drawable.ic_play_24)
                    binding.seekBar.progress = 0
                }
            }
        }
    }

    private fun setMusicWindow() {
        playList = intent.getParcelableArrayListExtra("playlist")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music

        binding.apply {
            tvTitle.text = music?.title
            tvArtist.text = music?.artist
            tvDuration.text = resources.getString(R.string.tv_duration_start)
            tvDurationTotal.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(music?.duration)
        }
        val bitmap = music?.getAlbumImage(this, ALBUM_SIZE)

        if (bitmap != null) {
            binding.ivAlbum.setImageBitmap(bitmap)
        } else {
            binding.ivAlbum.setImageResource(R.drawable.ic_music_100)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            messengerJob?.cancel()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}