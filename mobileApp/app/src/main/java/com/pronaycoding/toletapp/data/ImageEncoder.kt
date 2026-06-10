package com.pronaycoding.toletapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

object ImageEncoder {
    private const val MAX_WIDTH = 800
    private const val JPEG_QUALITY = 60

    suspend fun encodeToBase64(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Could not read selected image.")

        inputStream.use { stream ->
            val original = BitmapFactory.decodeStream(stream)
                ?: throw IllegalStateException("Could not decode selected image.")

            val scaled = scaleBitmap(original)
            if (scaled !== original) {
                original.recycle()
            }

            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            scaled.recycle()

            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        }
    }

    fun decodeFromBase64(base64: String): ByteArray {
        return Base64.decode(base64, Base64.DEFAULT)
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        if (bitmap.width <= MAX_WIDTH) return bitmap

        val scale = MAX_WIDTH.toFloat() / bitmap.width.toFloat()
        val height = max(1, (bitmap.height * scale).toInt())
        return Bitmap.createScaledBitmap(bitmap, MAX_WIDTH, height, true)
    }
}
