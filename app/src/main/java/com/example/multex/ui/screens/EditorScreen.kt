package com.example.multex.ui.screens

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.example.multex.SharedViewModel
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(navController: NavController, viewModel: SharedViewModel) {
    val context = LocalContext.current

    val imageUri1 by viewModel.imageUri1.collectAsState()
    val imageUri2 by viewModel.imageUri2.collectAsState()

    // --- ИСПРАВЛЕНО: Собираем все состояния один раз в родительском Composable ---
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

    val bitmap1 by produceState<Bitmap?>(initialValue = null, imageUri1, brightness1, contrast1, saturation1, highlights1, shadows1) {
        imageUri1?.let {
            value = viewModel.loadAndProcessBitmap(context, it, brightness1, contrast1, saturation1, highlights1, shadows1)
        }
    }
    val bitmap2 by produceState<Bitmap?>(initialValue = null, imageUri2, brightness2, contrast2, saturation2, highlights2, shadows2) {
        imageUri2?.let {
            value = viewModel.loadAndProcessBitmap(context, it, brightness2, contrast2, saturation2, highlights2, shadows2)
        }
    }

    val captureController = rememberCaptureController()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save Image") },
                            onClick = {
                                showMenu = false
                                captureController.capture()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Capturable(
                controller = captureController,
                onCaptured = { capturedBitmap, error ->
                    if (capturedBitmap != null) {
                        viewModel.saveImage(context, capturedBitmap.asAndroidBitmap())
                    } else {
                        viewModel.showToast(context, "Capture failed: \${error?.message}")
                    }
                }
            ) {
                ImagePreview(bitmap1, bitmap2, blendMode, alpha1, alpha2)
            }

            Spacer(modifier = Modifier.height(16.dp))

            EditorTabs(viewModel = viewModel)
        }
    }
}

@Composable
fun ImagePreview(bitmap1: Bitmap?, bitmap2: Bitmap?, blendMode: androidx.compose.ui.graphics.BlendMode, alpha1: Float, alpha2: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap1 != null && bitmap2 != null) {
            val finalBitmap = remember(bitmap1, bitmap2, blendMode, alpha1, alpha2) {
                val result = createBitmap(bitmap1.width, bitmap1.height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(result)
                val paint1 = android.graphics.Paint().apply { this.alpha = (alpha1 * 255).toInt() }
                val paint2 = android.graphics.Paint().apply {
                    this.xfermode = PorterDuffXfermode(blendMode.toPorterDuffMode())
                    this.alpha = (alpha2 * 255).toInt()
                }
                canvas.drawBitmap(bitmap1, 0f, 0f, paint1)
                canvas.drawBitmap(bitmap2, 0f, 0f, paint2)
                result
            }
            Image(bitmap = finalBitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
        } else {
            Text("Loading images...")
        }
    }
}

@Composable
fun EditorTabs(viewModel: SharedViewModel) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Image 1", "Image 2", "Blend")

    Column {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
            }
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) {
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
    // --- ИСПРАВЛЕНО: Получаем состояния для Image 1 напрямую ---
    val alpha by viewModel.alpha1.collectAsState()
    val brightness by viewModel.brightness1.collectAsState()
    val contrast by viewModel.contrast1.collectAsState()
    val saturation by viewModel.saturation1.collectAsState()
    val highlights by viewModel.highlights1.collectAsState()
    val shadows by viewModel.shadows1.collectAsState()

    Column(Modifier.padding(vertical = 8.dp)) {
        AdjustmentSlider("Transparency", alpha, { viewModel.onAlpha1Change(it) }, 0f..1f)
        AdjustmentSlider("Brightness", brightness, { viewModel.onBrightness1Change(it) }, 0f..2f)
        AdjustmentSlider("Contrast", contrast, { viewModel.onContrast1Change(it) }, 0f..2f)
        AdjustmentSlider("Saturation", saturation, { viewModel.onSaturation1Change(it) }, 0f..2f)
        AdjustmentSlider("Highlights", highlights, { viewModel.onHighlights1Change(it) }, 0f..1f)
        AdjustmentSlider("Shadows", shadows, { viewModel.onShadows1Change(it) }, 0f..1f)
    }
}

@Composable
fun Image2Settings(viewModel: SharedViewModel) {
    // --- ИСПРАВЛЕНО: Получаем состояния для Image 2 напрямую ---
    val alpha by viewModel.alpha2.collectAsState()
    val brightness by viewModel.brightness2.collectAsState()
    val contrast by viewModel.contrast2.collectAsState()
    val saturation by viewModel.saturation2.collectAsState()
    val highlights by viewModel.highlights2.collectAsState()
    val shadows by viewModel.shadows2.collectAsState()

    Column(Modifier.padding(vertical = 8.dp)) {
        AdjustmentSlider("Transparency", alpha, { viewModel.onAlpha2Change(it) }, 0f..1f)
        AdjustmentSlider("Brightness", brightness, { viewModel.onBrightness2Change(it) }, 0f..2f)
        AdjustmentSlider("Contrast", contrast, { viewModel.onContrast2Change(it) }, 0f..2f)
        AdjustmentSlider("Saturation", saturation, { viewModel.onSaturation2Change(it) }, 0f..2f)
        AdjustmentSlider("Highlights", highlights, { viewModel.onHighlights2Change(it) }, 0f..1f)
        AdjustmentSlider("Shadows", shadows, { viewModel.onShadows2Change(it) }, 0f..1f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlendSettings(viewModel: SharedViewModel) {
    val blendMode by viewModel.blendMode.collectAsState()
    val options = listOf(
        "Screen" to androidx.compose.ui.graphics.BlendMode.Screen,
        "Multiply" to androidx.compose.ui.graphics.BlendMode.Multiply,
        "Overlay" to androidx.compose.ui.graphics.BlendMode.Overlay
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (label, mode) ->
            val selected = blendMode == mode
            FilterChip(
                selected = selected,
                onClick = {
                    val newMode = if (selected) androidx.compose.ui.graphics.BlendMode.SrcOver else mode
                    viewModel.onBlendModeChange(newMode)
                },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun AdjustmentSlider(label: String, value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = "$label: ${"%.2f".format(value)}", style = MaterialTheme.typography.labelMedium)
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}

// Утилиты для конвертации BlendMode
fun androidx.compose.ui.graphics.BlendMode.toPorterDuffMode(): PorterDuff.Mode {
    return when (this) {
        androidx.compose.ui.graphics.BlendMode.Clear -> PorterDuff.Mode.CLEAR
        androidx.compose.ui.graphics.BlendMode.Src -> PorterDuff.Mode.SRC
        androidx.compose.ui.graphics.BlendMode.Dst -> PorterDuff.Mode.DST
        androidx.compose.ui.graphics.BlendMode.SrcOver -> PorterDuff.Mode.SRC_OVER
        androidx.compose.ui.graphics.BlendMode.DstOver -> PorterDuff.Mode.DST_OVER
        androidx.compose.ui.graphics.BlendMode.SrcIn -> PorterDuff.Mode.SRC_IN
        androidx.compose.ui.graphics.BlendMode.DstIn -> PorterDuff.Mode.DST_IN
        androidx.compose.ui.graphics.BlendMode.SrcOut -> PorterDuff.Mode.SRC_OUT
        androidx.compose.ui.graphics.BlendMode.DstOut -> PorterDuff.Mode.DST_OUT
        androidx.compose.ui.graphics.BlendMode.SrcAtop -> PorterDuff.Mode.SRC_ATOP
        androidx.compose.ui.graphics.BlendMode.DstAtop -> PorterDuff.Mode.DST_ATOP
        androidx.compose.ui.graphics.BlendMode.Xor -> PorterDuff.Mode.XOR
        androidx.compose.ui.graphics.BlendMode.Plus -> PorterDuff.Mode.ADD
        androidx.compose.ui.graphics.BlendMode.Modulate -> PorterDuff.Mode.MULTIPLY
        androidx.compose.ui.graphics.BlendMode.Screen -> PorterDuff.Mode.SCREEN
        androidx.compose.ui.graphics.BlendMode.Overlay -> PorterDuff.Mode.OVERLAY
        androidx.compose.ui.graphics.BlendMode.Darken -> PorterDuff.Mode.DARKEN
        androidx.compose.ui.graphics.BlendMode.Lighten -> PorterDuff.Mode.LIGHTEN
        androidx.compose.ui.graphics.BlendMode.Multiply -> PorterDuff.Mode.MULTIPLY
        else -> PorterDuff.Mode.SRC_OVER
    }
}
