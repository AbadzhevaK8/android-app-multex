package com.example.multex.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
            val imageModifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))

            AsyncImage(
                model = imageUri1,
                contentDescription = stringResource(R.string.select_image_1),
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )

            AsyncImage(
                model = imageUri2,
                contentDescription = stringResource(R.string.select_image_2),
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { launcher1.launch("image/*") }) {
                Text(if (imageUri1 == null) stringResource(R.string.select_image_1) else stringResource(R.string.change_image_1))
            }
            Button(onClick = { launcher2.launch("image/*") }) {
                Text(if (imageUri2 == null) stringResource(R.string.select_image_2) else stringResource(R.string.change_image_2))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("editor") },
            enabled = imageUri1 != null && imageUri2 != null
        ) {
            Text(stringResource(R.string.go_to_editor))
        }
    }
}
