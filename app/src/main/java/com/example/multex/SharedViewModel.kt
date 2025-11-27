package com.example.multex

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _imageUri1 = MutableStateFlow<Uri?>(null)
    val imageUri1 = _imageUri1.asStateFlow()

    private val _imageUri2 = MutableStateFlow<Uri?>(null)
    val imageUri2 = _imageUri2.asStateFlow()

    fun setUris(uri1: Uri, uri2: Uri) {
        _imageUri1.value = uri1
        _imageUri2.value = uri2
    }
}
