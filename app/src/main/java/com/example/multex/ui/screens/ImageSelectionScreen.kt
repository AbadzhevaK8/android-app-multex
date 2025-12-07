package com.example.multex.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.multex.R
import com.example.multex.SharedViewModel

@Composable
fun ImageSelectionScreen(navController: NavController, viewModel: SharedViewModel) {
    val imageUri1 by viewModel.imageUri1.collectAsState()
    val imageUri2 by viewModel.imageUri2.collectAsState()
    val rotation1 by viewModel.rotation1.collectAsState()
    val rotation2 by viewModel.rotation2.collectAsState()

    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> viewModel.onUri1Change(uri) }
    )

    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> viewModel.onUri2Change(uri) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Thumbnails row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImagePickerBox(
                    imageUri = imageUri1,
                    rotation = rotation1,
                    contentDescription = stringResource(R.string.select_image_1),
                    onClick = { launcher1.launch("image/*") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { viewModel.rotateImage1() }) {
                        Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Rotate Right")
                    }
                    IconButton(onClick = { viewModel.resetRotation1() }, enabled = rotation1 != 0f) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Reset Rotation")
                    }
                }
            }


            IconButton(onClick = { viewModel.swapImages() }) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swap Images",
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImagePickerBox(
                    imageUri = imageUri2,
                    rotation = rotation2,
                    contentDescription = stringResource(R.string.select_image_2),
                    onClick = { launcher2.launch("image/*") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { viewModel.rotateImage2() }) {
                        Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Rotate Right")
                    }
                    IconButton(onClick = { viewModel.resetRotation2() }, enabled = rotation2 != 0f) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Reset Rotation")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = { navController.navigate("editor") },
            enabled = imageUri1 != null && imageUri2 != null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.go_to_editor),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ImagePickerBox(imageUri: Uri?, rotation: Float, contentDescription: String, onClick: () -> Unit) {
    val imageModifier = Modifier
        .size(120.dp)
        .clip(RoundedCornerShape(8.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(imageModifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_image_placeholder),
            contentDescription = null, // decorative
            colorFilter = ColorFilter.tint(Color.Gray),
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )
        AsyncImage(
            model = imageUri,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation },
            contentScale = ContentScale.Crop,
        )
    }
}
