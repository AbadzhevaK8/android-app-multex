package com.k8abadzheva.multex

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.BlendMode
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs: SharedPreferences = application.getSharedPreferences("multex_prefs", Context.MODE_PRIVATE)

    private val _imageUri1 = MutableStateFlow<Uri?>(null)
    val imageUri1: StateFlow<Uri?> = _imageUri1.asStateFlow()

    private val _imageUri2 = MutableStateFlow<Uri?>(null)
    val imageUri2: StateFlow<Uri?> = _imageUri2.asStateFlow()

    private val _rotation1 = MutableStateFlow(0f)
    val rotation1: StateFlow<Float> = _rotation1.asStateFlow()

    private val _rotation2 = MutableStateFlow(0f)
    val rotation2: StateFlow<Float> = _rotation2.asStateFlow()

    private val _blendMode = MutableStateFlow(BlendMode.Screen)
    val blendMode: StateFlow<BlendMode> = _blendMode.asStateFlow()

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

    private val _imageSelectionIntroShown = MutableStateFlow(prefs.getBoolean("image_selection_intro_shown", false))
    val imageSelectionIntroShown: StateFlow<Boolean> = _imageSelectionIntroShown.asStateFlow()

    private val _editorIntroShown = MutableStateFlow(prefs.getBoolean("editor_intro_shown", false))
    val editorIntroShown: StateFlow<Boolean> = _editorIntroShown.asStateFlow()

    @SuppressLint("UseKtx")
    fun onImageSelectionIntroShown() {
        _imageSelectionIntroShown.value = true
        prefs.edit().putBoolean("image_selection_intro_shown", true).apply()
    }

    @SuppressLint("UseKtx")
    fun onEditorIntroShown() {
        _editorIntroShown.value = true
        prefs.edit().putBoolean("editor_intro_shown", true).apply()
    }

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

        val tempRot = _rotation1.value
        _rotation1.value = _rotation2.value
        _rotation2.value = tempRot
    }

    fun rotateImage1() {
        _rotation1.value = (_rotation1.value + 90f) % 360f
    }

    fun rotateImage2() {
        _rotation2.value = (_rotation2.value + 90f) % 360f
    }

    fun resetRotation1() {
        _rotation1.value = 0f
    }

    fun resetRotation2() {
        _rotation2.value = 0f
    }

    fun onBlendModeChange(blendMode: BlendMode) {
        _blendMode.value = blendMode
    }

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
        _rotation1.value = 0f
        _rotation2.value = 0f
    }

    fun loadAndProcessBitmap(context: Context, uri: Uri, rotation: Float, brightness: Float, contrast: Float, saturation: Float, highlights: Float, shadows: Float): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val adjustedBitmap = createBitmap(originalBitmap.width, originalBitmap.height)
            val canvas = Canvas(adjustedBitmap)
            val paint = Paint()
            val finalMatrix = ColorMatrix()

            val saturationMatrix = ColorMatrix().apply { setSaturation(saturation) }
            finalMatrix.postConcat(saturationMatrix)

            val contrastValue = contrast
            val contrastMatrix = ColorMatrix(floatArrayOf(
                contrastValue, 0f, 0f, 0f, (1 - contrastValue) * 128,
                0f, contrastValue, 0f, 0f, (1 - contrastValue) * 128,
                0f, 0f, contrastValue, 0f, (1 - contrastValue) * 128,
                0f, 0f, 0f, 1f, 0f
            ))
            finalMatrix.postConcat(contrastMatrix)

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

            if (rotation == 0f) {
                return adjustedBitmap
            }
            val rotationMatrix = Matrix().apply { postRotate(rotation) }
            Bitmap.createBitmap(adjustedBitmap, 0, 0, adjustedBitmap.width, adjustedBitmap.height, rotationMatrix, true)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveImage(context: Context, bitmap: Bitmap) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Multex_Image_${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    } else {
                        throw Exception("Content resolver returned null OutputStream")
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                showToast(context, "Image saved to Pictures folder")

            } catch (e: Exception) {
                e.printStackTrace()
                showToast(context, "Error saving image: ${e.message}")
                resolver.delete(uri, null, null) // Clean up the pending entry
            }
        } else {
            showToast(context, "Error saving image: Could not create MediaStore entry.")
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
