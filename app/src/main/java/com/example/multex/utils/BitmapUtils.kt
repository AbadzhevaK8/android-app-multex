package com.example.multex.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

object BitmapUtils {
    fun saveBitmap(context: Context, bitmap: Bitmap, displayName: String): Uri? {
        val imageCollection = MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            val uri = context.contentResolver.insert(imageCollection, contentValues)
            uri?.let {
                val stream: OutputStream? = context.contentResolver.openOutputStream(it)
                stream?.let { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                }
            }
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
