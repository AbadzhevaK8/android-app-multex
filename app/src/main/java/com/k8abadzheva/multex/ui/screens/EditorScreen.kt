@file:Suppress("OPT_IN_USAGE")
@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)

package com.k8abadzheva.multex.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.k8abadzheva.multex.R
import com.k8abadzheva.multex.SharedViewModel
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun EditorScreen(navController: NavController, viewModel: SharedViewModel) {
    val context = LocalContext.current
    var showUI by remember { mutableStateOf(true) }

    val imageUri1 by viewModel.imageUri1.collectAsState()
    val imageUri2 by viewModel.imageUri2.collectAsState()
    val rotation1 by viewModel.rotation1.collectAsState()
    val rotation2 by viewModel.rotation2.collectAsState()

    val blendMode by viewModel.blendMode.collectAsState()
    val alpha1 by viewModel.alpha1.collectAsState()
    val alpha2 by viewModel.alpha2.collectAsState()
    val brightness1 by viewModel.brightness1.collectAsState()
    val contrast1 by viewModel.contrast1.collectAsState()
    val saturation1 by viewModel.saturation1.collectAsState()
    val highlights1 by viewModel.highlights1.collectAsState()
    val shadows1 by viewModel.shadows1.collectAsState()
    val brightness2 by viewModel.brightness2.collectAsState()
    val contrast2 by viewModel.contrast2.collectAsState()
    val saturation2 by viewModel.saturation2.collectAsState()
    val highlights2 by viewModel.highlights2.collectAsState()
    val shadows2 by viewModel.shadows2.collectAsState()

    val bitmap1 by produceState<Bitmap?>(initialValue = null, imageUri1, rotation1, brightness1, contrast1, saturation1, highlights1, shadows1) {
        imageUri1?.let {
            value = viewModel.loadAndProcessBitmap(context, it, rotation1, brightness1, contrast1, saturation1, highlights1, shadows1)
        }
    }
    val bitmap2 by produceState<Bitmap?>(initialValue = null, imageUri2, rotation2, brightness2, contrast2, saturation2, highlights2, shadows2) {
        imageUri2?.let {
            value = viewModel.loadAndProcessBitmap(context, it, rotation2, brightness2, contrast2, saturation2, highlights2, shadows2)
        }
    }

    val captureController = rememberCaptureController()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(if (showUI) 7f else 1f)
                .fillMaxWidth()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showUI = !showUI },
            contentAlignment = Alignment.Center
        ) {
            ImagePreview(
                bitmap1,
                bitmap2,
                blendMode,
                alpha1,
                alpha2,
                modifier = Modifier.capturable(captureController)
            )
        }

        AnimatedVisibility(
            visible = showUI,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.weight(3f)
        ) {
            Column {
                Column(modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())) {
                    EditorTabs(viewModel = viewModel)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                    IconButton(onClick = { viewModel.resetSettings() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.reset))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = captureController.captureAsync().await()
                                viewModel.shareImage(context, bitmap.asAndroidBitmap())
                            } catch (e: Exception) {
                                viewModel.showToast(context, "Capture failed: ${e.message}")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share))
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = captureController.captureAsync().await()
                                viewModel.saveImage(context, bitmap.asAndroidBitmap())
                            } catch (e: Exception) {
                                viewModel.showToast(context, "Capture failed: ${e.message}")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Done, contentDescription = stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun ImagePreview(
    bitmap1: Bitmap?,
    bitmap2: Bitmap?,
    blendMode: androidx.compose.ui.graphics.BlendMode,
    alpha1: Float,
    alpha2: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap1 != null && bitmap2 != null) {
            Image(
                bitmap = bitmap1.asImageBitmap(),
                contentDescription = stringResource(R.string.image_1),
                modifier = Modifier
                    .wrapContentSize()
                    .graphicsLayer { this.alpha = alpha1 },
                contentScale = ContentScale.Fit
            )

            Image(
                bitmap = bitmap2.asImageBitmap(),
                contentDescription = stringResource(R.string.image_2),
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        this.alpha = alpha2
                        this.blendMode = blendMode
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            Text(stringResource(R.string.loading_images), color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTabs(viewModel: SharedViewModel) {
    var tabIndex by remember { mutableIntStateOf(2) }
    val tabs = listOf(stringResource(R.string.image_1), stringResource(R.string.image_2), stringResource(R.string.blend))

    Column {
        PrimaryTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            when (tabIndex) {
                0 -> Image1Settings(viewModel)
                1 -> Image2Settings(viewModel)
                2 -> BlendSettings(viewModel)
            }
        }
    }
}

@Composable
fun Image1Settings(viewModel: SharedViewModel) {
    val alpha by viewModel.alpha1.collectAsState()
    val brightness by viewModel.brightness1.collectAsState()
    val contrast by viewModel.contrast1.collectAsState()
    val saturation by viewModel.saturation1.collectAsState()
    val highlights by viewModel.highlights1.collectAsState()
    val shadows by viewModel.shadows1.collectAsState()
    val rotation by viewModel.rotation1.collectAsState()

    Column(Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.rotateImage1() }) {
                Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = stringResource(R.string.rotate_90_degrees))
            }
            IconButton(onClick = { viewModel.resetRotation1() }, enabled = rotation != 0f) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.reset_rotation))
            }
        }
        AdjustmentSlider(stringResource(R.string.transparency), alpha, { viewModel.onAlpha1Change(it) }, 0f..1f)
        AdjustmentSlider(stringResource(R.string.brightness), brightness, { viewModel.onBrightness1Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.contrast), contrast, { viewModel.onContrast1Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.saturation), saturation, { viewModel.onSaturation1Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.highlights), highlights, { viewModel.onHighlights1Change(it) }, 0f..1f)
        AdjustmentSlider(stringResource(R.string.shadows), shadows, { viewModel.onShadows1Change(it) }, 0f..1f)
    }
}

@Composable
fun Image2Settings(viewModel: SharedViewModel) {
    val alpha by viewModel.alpha2.collectAsState()
    val brightness by viewModel.brightness2.collectAsState()
    val contrast by viewModel.contrast2.collectAsState()
    val saturation by viewModel.saturation2.collectAsState()
    val highlights by viewModel.highlights2.collectAsState()
    val shadows by viewModel.shadows2.collectAsState()
    val rotation by viewModel.rotation2.collectAsState()

    Column(Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { viewModel.rotateImage2() }) {
                Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = stringResource(R.string.rotate_90_degrees))
            }
            IconButton(onClick = { viewModel.resetRotation2() }, enabled = rotation != 0f) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.reset_rotation))
            }
        }
        AdjustmentSlider(stringResource(R.string.transparency), alpha, { viewModel.onAlpha2Change(it) }, 0f..1f)
        AdjustmentSlider(stringResource(R.string.brightness), brightness, { viewModel.onBrightness2Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.contrast), contrast, { viewModel.onContrast2Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.saturation), saturation, { viewModel.onSaturation2Change(it) }, 0f..2f)
        AdjustmentSlider(stringResource(R.string.highlights), highlights, { viewModel.onHighlights2Change(it) }, 0f..1f)
        AdjustmentSlider(stringResource(R.string.shadows), shadows, { viewModel.onShadows2Change(it) }, 0f..1f)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlendSettings(viewModel: SharedViewModel) {
    val blendMode by viewModel.blendMode.collectAsState()
    val options = listOf(
        stringResource(R.string.screen) to androidx.compose.ui.graphics.BlendMode.Screen,
        stringResource(R.string.multiply) to androidx.compose.ui.graphics.BlendMode.Multiply,
        stringResource(R.string.overlay) to androidx.compose.ui.graphics.BlendMode.Overlay,
        stringResource(R.string.lighten) to androidx.compose.ui.graphics.BlendMode.Lighten,
        stringResource(R.string.darken) to androidx.compose.ui.graphics.BlendMode.Darken,
        stringResource(R.string.difference) to androidx.compose.ui.graphics.BlendMode.Difference,
        stringResource(R.string.color) to androidx.compose.ui.graphics.BlendMode.Color,
        stringResource(R.string.hard_light) to androidx.compose.ui.graphics.BlendMode.Hardlight
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (label, mode) ->
            FilterChip(
                selected = blendMode == mode,
                onClick = { viewModel.onBlendModeChange(mode) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun AdjustmentSlider(label: String, value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(text = "$label: ${String.format(Locale.US, "%.2f", value)}", style = MaterialTheme.typography.labelSmall)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            thumb = {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(1.dp)
                )
            }
        )
    }
}
