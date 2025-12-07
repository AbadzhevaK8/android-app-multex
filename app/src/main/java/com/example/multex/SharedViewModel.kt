package com.example.multex

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.ui.graphics.BlendMode
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class SharedViewModel : ViewModel() {
    private val _imageUri1 = MutableStateFlow<Uri?>(null)
    val imageUri1: StateFlow<Uri?> = _imageUri1.asStateFlow()

    private val _imageUri2 = MutableStateFlow<Uri?>(null)
    val imageUri2: StateFlow<Uri?> = _imageUri2.asStateFlow()

    private val _blendMode = MutableStateFlow(BlendMode.Screen)
    val blendMode: StateFlow<BlendMode> = _blendMode.asStateFlow()

    // --- ИСПРАВЛЕНО: _alpha разделена на _alpha1 и _alpha2 ---
    private val _alpha1 = MutableStateFlow(1f)
    val alpha1: StateFlow<Float> = _alpha1.asStateFlow()

    private val _alpha2 = MutableStateFlow(1f)
    val alpha2: StateFlow<Float> = _alpha2.asStateFlow()

    // Adjustment properties for Image 1
    private val _brightness1 = MutableStateFlow(1f)
    val brightness1: StateFlow<Float> = _brightness1.asStateFlow()
    private val _contrast1 = MutableStateFlow(1f)
    val contrast1: StateFlow<Float> = _contrast1.asStateFlow()
    private val _saturation1 = MutableStateFlow(1f)
    val saturation1: StateFlow<Float> = _saturation1.asStateFlow()
    private val _highlights1 = MutableStateFlow(0f)
    val highlights1: StateFlow<Float> = _highlights1.asStateFlow()
    private val _shadows1 = MutableStateFlow(0f)
    val shadows1: StateFlow<Float> = _shadows1.asStateFlow()

    // Adjustment properties for Image 2
    private val _brightness2 = MutableStateFlow(1f)
    val brightness2: StateFlow<Float> = _brightness2.asStateFlow()
    private val _contrast2 = MutableStateFlow(1f)
    val contrast2: StateFlow<Float> = _contrast2.asStateFlow()
    private val _saturation2 = MutableStateFlow(1f)
    val saturation2: StateFlow<Float> = _saturation2.asStateFlow()
    private val _highlights2 = MutableStateFlow(0f)
    val highlights2: StateFlow<Float> = _highlights2.asStateFlow()
    private val _shadows2 = MutableStateFlow(0f)
    val shadows2: StateFlow<Float> = _shadows2.asStateFlow()


    fun onUri1Change(uri: Uri?) {
        _imageUri1.value = uri
    }

    fun onUri2Change(uri: Uri?) {
        _imageUri2.value = uri
    }

    fun swapImages() {
        val tempUri = _imageUri1.value
        _imageUri1.value = _imageUri2.value
        _imageUri2.value = tempUri
    }

    fun onBlendModeChange(blendMode: BlendMode) {
        _blendMode.value = blendMode
    }

    // --- ИСПРАВЛЕНО: onAlphaChange заменена на onAlpha1Change и onAlpha2Change ---
    fun onAlpha1Change(value: Float) {
        _alpha1.value = value
    }

    fun onAlpha2Change(value: Float) {
        _alpha2.value = value
    }

    // --- Image 1 Adjustment Handlers ---
    fun onBrightness1Change(value: Float) { _brightness1.value = value }
    fun onContrast1Change(value: Float) { _contrast1.value = value }
    fun onSaturation1Change(value: Float) { _saturation1.value = value }
    fun onHighlights1Change(value: Float) { _highlights1.value = value }
    fun onShadows1Change(value: Float) { _shadows1.value = value }

    // --- Image 2 Adjustment Handlers ---
    fun onBrightness2Change(value: Float) { _brightness2.value = value }
    fun onContrast2Change(value: Float) { _contrast2.value = value }
    fun onSaturation2Change(value: Float) { _saturation2.value = value }
    fun onHighlights2Change(value: Float) { _highlights2.value = value }
    fun onShadows2Change(value: Float) { _shadows2.value = value }

    fun resetSettings() {
        _blendMode.value = BlendMode.Screen
        _alpha1.value = 1f
        _alpha2.value = 1f
        _brightness1.value = 1f
        _contrast1.value = 1f
        _saturation1.value = 1f
        _highlights1.value = 0f
        _shadows1.value = 0f
        _brightness2.value = 1f
        _contrast2.value = 1f
        _saturation2.value = 1f
        _highlights2.value = 0f
        _shadows2.value = 0f
    }

    fun loadAndProcessBitmap(context: Context, uri: Uri, brightness: Float, contrast: Float, saturation: Float, highlights: Float, shadows: Float): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val adjustedBitmap = createBitmap(originalBitmap.width, originalBitmap.height)
            val canvas = Canvas(adjustedBitmap)
            val paint = Paint()
            val finalMatrix = ColorMatrix()

            // 1. Насыщенность
            val saturationMatrix = ColorMatrix().apply { setSaturation(saturation) }
            finalMatrix.postConcat(saturationMatrix)

            // 2. Контраст
            val contrastValue = contrast
            val contrastMatrix = ColorMatrix(floatArrayOf(
                contrastValue, 0f, 0f, 0f, (1 - contrastValue) * 128,
                0f, contrastValue, 0f, 0f, (1 - contrastValue) * 128,
                0f, 0f, contrastValue, 0f, (1 - contrastValue) * 128,
                0f, 0f, 0f, 1f, 0f
            ))
            finalMatrix.postConcat(contrastMatrix)

            // 3. Общая коррекция яркости (Brightness, Highlights, Shadows)
            val brightnessTotal = (brightness - 1f) * 255f
            val highlightsTotal = highlights * 100f
            val shadowsTotal = shadows * -100f
            val totalAdjustment = brightnessTotal + highlightsTotal + shadowsTotal
            val adjustmentMatrix = ColorMatrix(floatArrayOf(
                1f, 0f, 0f, 0f, totalAdjustment,
                0f, 1f, 0f, 0f, totalAdjustment,
                0f, 0f, 1f, 0f, totalAdjustment,
                0f, 0f, 0f, 1f, 0f
            ))
            finalMatrix.postConcat(adjustmentMatrix)

            paint.colorFilter = ColorMatrixColorFilter(finalMatrix)
            canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

            adjustedBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveImage(context: Context, bitmap: Bitmap) {
        try {
            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDirectory, "Multex_Image_${System.currentTimeMillis()}.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            showToast(context, "Image saved to Pictures folder")
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(context, "Error saving image: ${e.message}")
        }
    }

    fun shareImage(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "Multex_Image_${System.currentTimeMillis()}.png")
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()

            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(context, "Error sharing image: ${e.message}")
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
