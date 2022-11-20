package com.example.musicplayer.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(
    val id: String,
    val title: String?,
    val artist: String?,
    val albumId: String?,
    val duration: Int,
    val likes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    companion object : Parceler<Music> {
        override fun create(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun Music.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(albumId)
            parcel.writeInt(duration)
            parcel.writeInt(likes)
        }
    }

    /*Album Uri*/
    private fun getAlbumUri(): Uri {
        return Uri.parse("content://media/external/audio/albumart/$albumId")
    }

    /*Album Image*/
    @SuppressLint("Recycle")
    fun getAlbumImage(context: Context, size: Int): Bitmap? {
        val contentResolver = context.contentResolver
        val uri = getAlbumUri()
        val options = BitmapFactory.Options()
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var bitmap: Bitmap?

        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "read")
            bitmap = BitmapFactory.decodeFileDescriptor(
                parcelFileDescriptor?.fileDescriptor, null, options
            )
            if (bitmap != null) {
                val tempBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
                bitmap.recycle()
                bitmap = tempBitmap
            }
            return bitmap
        } catch (e: java.lang.Exception) {
            Log.d("Music", "${e.printStackTrace()}")
        } finally {
            try {
                parcelFileDescriptor?.close()
            } catch (e: Exception) {
                Log.d("Music", "${e.printStackTrace()}")
            }
        }
        return null
    }

    /*Music Uri*/
    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }
}
