package com.example.multex.ui.screens

import android.content.Intent
import android.graphics.ColorMatrix as AndroidColorMatrix
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.multex.R
import com.example.multex.SharedViewModel
import com.example.multex.utils.BitmapUtils
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(viewModel: SharedViewModel) {
    val imageUri1 by viewModel.imageUri1.collectAsState()
    val imageUri2 by viewModel.imageUri2.collectAsState()

    // Show a loading state or handle null URIs gracefully
    if (imageUri1 == null || imageUri2 == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Or a message
        }
        return
    }

    var alpha by remember { mutableFloatStateOf(1f) }
    var blendMode by remember { mutableStateOf(BlendMode.Screen) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Image 1 adjustments
    var brightness1 by remember { mutableFloatStateOf(0f) }
    var contrast1 by remember { mutableFloatStateOf(1f) }
    var saturation1 by remember { mutableFloatStateOf(1f) }
    var highlights1 by remember { mutableFloatStateOf(0f) }
    var shadows1 by remember { mutableFloatStateOf(0f) }

    // Image 2 adjustments
    var brightness2 by remember { mutableFloatStateOf(0f) }
    var contrast2 by remember { mutableFloatStateOf(1f) }
    var saturation2 by remember { mutableFloatStateOf(1f) }
    var highlights2 by remember { mutableFloatStateOf(0f) }
    var shadows2 by remember { mutableFloatStateOf(0f) }

    val colorMatrix1 = remember(brightness1, contrast1, saturation1, highlights1, shadows1) {
        getColorMatrix(brightness1, contrast1, saturation1, highlights1, shadows1)
    }

    val colorMatrix2 = remember(brightness2, contrast2, saturation2, highlights2, shadows2) {
        getColorMatrix(brightness2, contrast2, saturation2, highlights2, shadows2)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    var captureAction by remember { mutableStateOf<String?>(null) }

    val editedImageFilename = stringResource(R.string.edited_image_filename)
    val imageSavedSuccessfully = stringResource(R.string.image_saved_successfully)
    val failedToSaveImage = stringResource(R.string.failed_to_save_image)
    val shareImageFilename = stringResource(R.string.share_image_filename)
    val shareDialogTitle = stringResource(R.string.share_dialog_title)
    val failedToShare = stringResource(R.string.failed_to_share)
    val captureError = stringResource(R.string.capture_error)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Capturable(
            controller = captureController,
            onCaptured = { bitmap, error ->
                bitmap?.let { imageBitmap ->
                    val androidBitmap = imageBitmap.asAndroidBitmap()
                    scope.launch {
                        when (captureAction) {
                            "save" -> {
                                if (BitmapUtils.saveBitmap(context, androidBitmap, editedImageFilename) != null) {
                                    Toast.makeText(context, imageSavedSuccessfully, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, failedToSaveImage, Toast.LENGTH_SHORT).show()
                                }
                            }
                            "share" -> {
                                BitmapUtils.saveBitmap(context, androidBitmap, shareImageFilename)?.let { uri ->
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/jpeg"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                    }
                                    context.startActivity(Intent.createChooser(intent, shareDialogTitle))
                                } ?: Toast.makeText(context, failedToShare, Toast.LENGTH_SHORT).show()
                            }
                        }
                        captureAction = null // Reset action
                    }
                } ?: error?.let {
                    Toast.makeText(context, String.format(captureError, it.message), Toast.LENGTH_SHORT).show()
                    captureAction = null
                }
            }
        ) {
            Box(modifier = Modifier.size(300.dp)) {
                AsyncImage(
                    model = imageUri1,
                    contentDescription = stringResource(R.string.image_1),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix1)
                )
                AsyncImage(
                    model = imageUri2,
                    contentDescription = stringResource(R.string.image_2),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            alpha = alpha,
                            blendMode = blendMode
                        ),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix2)
                )
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text(stringResource(R.string.image_1)) })
            Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text(stringResource(R.string.image_2)) })
        }

        when (selectedTabIndex) {
            0 -> AdjustmentControls(
                brightness = brightness1, onBrightnessChange = { brightness1 = it },
                contrast = contrast1, onContrastChange = { contrast1 = it },
                saturation = saturation1, onSaturationChange = { saturation1 = it },
                highlights = highlights1, onHighlightsChange = { highlights1 = it },
                shadows = shadows1, onShadowsChange = { shadows1 = it },
            )
            1 -> AdjustmentControls(
                brightness = brightness2, onBrightnessChange = { brightness2 = it },
                contrast = contrast2, onContrastChange = { contrast2 = it },
                saturation = saturation2, onSaturationChange = { saturation2 = it },
                highlights = highlights2, onHighlightsChange = { highlights2 = it },
                shadows = shadows2, onShadowsChange = { shadows2 = it },
            )
        }

        Text(stringResource(R.string.transparency))
        Slider(value = alpha, onValueChange = { alpha = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())

        Text(stringResource(R.string.blending_mode))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { blendMode = BlendMode.Screen }) { Text(stringResource(R.string.screen)) }
            Button(onClick = { blendMode = BlendMode.Multiply }) { Text(stringResource(R.string.multiply)) }
            Button(onClick = { blendMode = BlendMode.Overlay }) { Text(stringResource(R.string.overlay)) }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                captureAction = "save"
                captureController.capture()
            }) { Text(stringResource(R.string.save)) }
            Button(onClick = {
                captureAction = "share"
                captureController.capture()
            }) { Text(stringResource(R.string.share)) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun getColorMatrix(
    brightness: Float,
    contrast: Float,
    saturation: Float,
    highlights: Float,
    shadows: Float
): ColorMatrix {
    val shadowEffect = shadows * 0.5f
    val highlightEffect = highlights * 0.5f
    val finalContrast = (contrast - shadowEffect + highlightEffect).coerceAtLeast(0f)
    val finalBrightness = brightness + shadowEffect + highlightEffect

    val androidMatrix = AndroidColorMatrix()
    androidMatrix.setSaturation(saturation)

    val contrastMatrix = AndroidColorMatrix().apply {
        set(floatArrayOf(
            finalContrast, 0f, 0f, 0f, 0f,
            0f, finalContrast, 0f, 0f, 0f,
            0f, 0f, finalContrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
    }
    androidMatrix.postConcat(contrastMatrix)

    val brightnessValue = finalBrightness * 255
    val brightnessMatrix = AndroidColorMatrix().apply {
        set(floatArrayOf(
            1f, 0f, 0f, 0f, brightnessValue,
            0f, 1f, 0f, 0f, brightnessValue,
            0f, 0f, 1f, 0f, brightnessValue,
            0f, 0f, 0f, 1f, 0f
        ))
    }
    androidMatrix.postConcat(brightnessMatrix)

    return ColorMatrix(androidMatrix.array)
}

@Composable
fun AdjustmentControls(
    brightness: Float, onBrightnessChange: (Float) -> Unit,
    contrast: Float, onContrastChange: (Float) -> Unit,
    saturation: Float, onSaturationChange: (Float) -> Unit,
    highlights: Float, onHighlightsChange: (Float) -> Unit,
    shadows: Float, onShadowsChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
        Text(stringResource(R.string.brightness))
        Slider(value = brightness, onValueChange = onBrightnessChange, valueRange = -1f..1f)

        Text(stringResource(R.string.contrast))
        Slider(value = contrast, onValueChange = onContrastChange, valueRange = 0f..2f)

        Text(stringResource(R.string.saturation))
        Slider(value = saturation, onValueChange = onSaturationChange, valueRange = 0f..2f)

        Text(stringResource(R.string.highlights))
        Slider(value = highlights, onValueChange = onHighlightsChange, valueRange = -1f..1f)

        Text(stringResource(R.string.shadows))
        Slider(value = shadows, onValueChange = onShadowsChange, valueRange = -1f..1f)
    }
}
