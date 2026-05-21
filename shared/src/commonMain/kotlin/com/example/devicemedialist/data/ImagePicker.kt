package com.example.devicemedialist.data

import androidx.compose.runtime.Composable

expect class ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (String?) -> Unit): ImagePickerLauncher
