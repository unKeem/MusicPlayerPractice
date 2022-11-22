package com.example.musicplayer.view.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.data.Music
import com.example.musicplayer.databinding.AdapterMusicBinding
import com.example.musicplayer.util.DBHelper
import com.example.musicplayer.view.activity.MainActivity
import com.example.musicplayer.view.activity.MusicActivity
import java.text.SimpleDateFormat
import java.util.*

class MusicRecyclerAdapter(private val musicList: ArrayList<Music>) :
    RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {
    //정적멤버상수
    companion object {
        const val ALBUM_SIZE = 500
        const val LIKES_EMPTY = 0
        const val LIKES_FULL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding =
            AdapterMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(musicList[position])
    }

    override fun getItemCount(): Int = musicList.size

    inner class CustomViewHolder(private val binding: AdapterMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Music) {
            binding.apply {
                tvArtist.text = data.artist
                tvTitle.text = data.title
                tvDuration.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(data.duration)

                val bitmap = data.getAlbumImage(itemView.context, ALBUM_SIZE)

                if (bitmap != null) {
                    binding.ivAlbum.setImageBitmap(bitmap)
                } else {
                    binding.ivAlbum.setImageResource(R.drawable.ic_music_100)
                }

                when (data.likes) {
                    LIKES_EMPTY -> binding.ivLikes.setImageResource(R.drawable.likes_empty)
                    LIKES_FULL -> binding.ivLikes.setImageResource(R.drawable.likes_full)
                }

                binding.ivLikes.setOnClickListener {
                    when (data.likes) {
                        LIKES_EMPTY -> binding.ivLikes.setImageResource(R.drawable.likes_full)
                        LIKES_FULL -> binding.ivLikes.setImageResource(R.drawable.likes_empty)
                    }

                    val database = DBHelper(
                        itemView.context,
                        MainActivity.DATABASE_NAME,
                        null,
                        MainActivity.DATABASE_VERSION
                    )
                    val flag = database.updateLikes(data)

                    if (!flag) {
                        Log.d("musicplayer", "adapter viewHolder bind() btnLikes")
                    } else {
                        notifyItemChanged(adapterPosition)
                    }
                }
                binding.root.setOnClickListener {
                    val intent = Intent(itemView.context, MusicActivity::class.java)
                    intent.putParcelableArrayListExtra("playlist", musicList)
                    intent.putExtra("position", adapterPosition)
                    binding.root.context.startActivity(intent)
                }
            }
        }
    }
}