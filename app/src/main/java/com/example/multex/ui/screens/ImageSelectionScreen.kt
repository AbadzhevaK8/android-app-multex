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
import androidx.compose.material3.Text
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
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.IntroShowcaseScope
import com.canopas.lib.showcase.component.rememberIntroShowcaseState
import com.example.multex.R
import com.example.multex.SharedViewModel

@Composable
fun ImageSelectionScreen(navController: NavController, viewModel: SharedViewModel) {
    val imageUri1 by viewModel.imageUri1.collectAsState()
    val imageUri2 by viewModel.imageUri2.collectAsState()
    val rotation1 by viewModel.rotation1.collectAsState()
    val rotation2 by viewModel.rotation2.collectAsState()
    val introShown by viewModel.imageSelectionIntroShown.collectAsState()

    val launcher1 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> viewModel.onUri1Change(uri) }
    )

    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> viewModel.onUri2Change(uri) }
    )

    val introShowcaseState = rememberIntroShowcaseState()

    IntroShowcase(
        showIntroShowCase = !introShown,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            viewModel.onImageSelectionIntroShown()
        },
        state = introShowcaseState
    ) {
        ImageSelectionContent(
            navController = navController,
            viewModel = viewModel,
            imageUri1 = imageUri1,
            imageUri2 = imageUri2,
            rotation1 = rotation1,
            rotation2 = rotation2,
            launcher1 = { launcher1.launch("image/*") },
            launcher2 = { launcher2.launch("image/*") }
        )
    }
}

@Composable
fun IntroShowcaseScope.ImageSelectionContent(
    navController: NavController,
    viewModel: SharedViewModel,
    imageUri1: Uri?,
    imageUri2: Uri?,
    rotation1: Float,
    rotation2: Float,
    launcher1: () -> Unit,
    launcher2: () -> Unit
) {
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
                    onClick = launcher1,
                    modifier = Modifier.introShowCaseTarget(
                        index = 0,
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.select_image_1),
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.showcase_select_image_1_message),
                                    color = Color.White
                                )
                            }
                        }
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { viewModel.rotateImage1() },
                        modifier = Modifier.introShowCaseTarget(
                            index = 2,
                            content = {
                                Column {
                                    Text(
                                        text = stringResource(R.string.showcase_rotate_image_title),
                                        color = Color.White
                                    )
                                    Text(
                                        text = stringResource(R.string.showcase_rotate_image_message),
                                        color = Color.White
                                    )
                                }
                            }
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = stringResource(R.string.rotate_right)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.resetRotation1() },
                        enabled = rotation1 != 0f
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = stringResource(R.string.reset_rotation)
                        )
                    }
                }
            }

            IconButton(
                onClick = { viewModel.swapImages() },
                modifier = Modifier.introShowCaseTarget(
                    index = 3,
                    content = {
                        Column {
                            Text(
                                text = stringResource(R.string.swap_images),
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.showcase_swap_images_message),
                                color = Color.White
                            )
                        }
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = stringResource(R.string.swap_images),
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
                    onClick = launcher2,
                    modifier = Modifier.introShowCaseTarget(
                        index = 1,
                        content = {
                            Column {
                                Text(
                                    text = stringResource(R.string.select_image_2),
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.showcase_select_image_2_message),
                                    color = Color.White
                                )
                            }
                        }
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { viewModel.rotateImage2() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = stringResource(R.string.rotate_right)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.resetRotation2() },
                        enabled = rotation2 != 0f
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = stringResource(R.string.reset_rotation)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = { navController.navigate("editor") },
            enabled = imageUri1 != null && imageUri2 != null,
            modifier = Modifier.introShowCaseTarget(
                index = 4,
                content = {
                    Column {
                        Text(
                            text = stringResource(R.string.go_to_editor),
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.showcase_go_to_editor_message),
                            color = Color.White
                        )
                    }
                }
            )
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
fun ImagePickerBox(
    imageUri: Uri?,
    rotation: Float,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageModifier = Modifier
        .size(120.dp)
        .clip(RoundedCornerShape(8.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))

    Box(
        modifier = modifier
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
