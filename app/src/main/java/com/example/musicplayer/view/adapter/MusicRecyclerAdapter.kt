package com.example.musicplayer.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.data.DBHelper
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.AdapterMusicBinding
import com.example.musicplayer.view.activity.MainActivity
import com.example.musicplayer.view.activity.MusicActivity
import java.text.SimpleDateFormat

class MusicRecyclerAdapter(val context: Context, val musicList: MutableList<Music>?) :
    RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {

    companion object {
        const val ALBUM_SIZE = 100
        const val LIKES_EMPTY = 0
        const val LIKES_FULL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =
            AdapterMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding
        val music = musicList?.get(position)

        binding.tvArtist.text = music?.artist
        binding.tvTitle.text = music?.title
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        val bitmap = music?.getAlbumImage(context, MusicRecyclerAdapter.ALBUM_SIZE)
        if (bitmap != null) {
            binding.ivAlbumArt.setImageBitmap(bitmap)
        } else {
            binding.ivAlbumArt.setImageResource(R.drawable.ic_music_100)
        }
        when (music?.likes) {
            LIKES_EMPTY -> {
                binding.ivItemLike.setImageResource(R.drawable.likes_empty)
            }
            LIKES_FULL -> {
                binding.ivItemLike.setImageResource(R.drawable.likes_full)
            }
        }
        binding.tvTitle.isSingleLine = true
        binding.tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.tvTitle.isSelected = true

        /*event*/
        binding.root.setOnClickListener {
            val playList: ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            val intent = Intent(binding.root.context, MusicActivity::class.java)
            intent.putExtra("playList", playList)
            intent.putExtra("position", position)
            intent.putExtra("music", music)
            binding.root.context.startActivity(intent)
        }

        /*save starButtonClickEvent */
        binding.ivItemLike.setOnClickListener {
            if (music?.likes == LIKES_EMPTY) {
                binding.ivItemLike.setImageResource(R.drawable.likes_full)
                music?.likes = LIKES_FULL
            } else {
                binding.ivItemLike.setImageResource(R.drawable.likes_empty)
                music?.likes = LIKES_EMPTY
            }
            if (music != null) {
                val dbHelper =
                    DBHelper(context, MainActivity.DATABASE_NAME, MainActivity.DATABASE_VERSION)
                val flag = dbHelper.updateLikes(music)
                if (flag == false) {
                    Log.d("musicplayer", "onBindViewHolder() 실패")
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList?.size ?: 0
    }

    class CustomViewHolder(val binding: AdapterMusicBinding) : RecyclerView.ViewHolder(binding.root)
}