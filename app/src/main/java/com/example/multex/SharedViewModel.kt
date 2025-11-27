package com.example.multex

import android.net.Uri
import androidx.compose.ui.graphics.BlendMode
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _imageUri1 = MutableStateFlow<Uri?>(null)
    val imageUri1: StateFlow<Uri?> = _imageUri1.asStateFlow()

    private val _imageUri2 = MutableStateFlow<Uri?>(null)
    val imageUri2: StateFlow<Uri?> = _imageUri2.asStateFlow()

    private val _blendMode = MutableStateFlow(BlendMode.Screen)
    val blendMode: StateFlow<BlendMode> = _blendMode.asStateFlow()

    fun onUri1Change(uri: Uri?) {
        _imageUri1.value = uri
    }

    fun onUri2Change(uri: Uri?) {
        _imageUri2.value = uri
    }

    fun onBlendModeChange(blendMode: BlendMode) {
        _blendMode.value = blendMode
    }
}