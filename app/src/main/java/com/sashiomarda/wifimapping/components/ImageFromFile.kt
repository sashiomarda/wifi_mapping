package com.sashiomarda.wifimapping.components

import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

@Composable
fun ImageFromFile(
    imageFileName: String,
    context: Context,
    coroutineScope: CoroutineScope
) {
    val filePath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        imageFileName
    ).path
    val imageBitmap = remember(filePath) {
        loadImageBitmapFromFile(filePath)
    }

    if (imageBitmap != null) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp
            Image(
                bitmap = imageBitmap,
                contentDescription = "Image from file",
                modifier = Modifier
                    .fillMaxHeight()
            )
            Button(
                modifier = Modifier
                    .padding(start = 8.dp),
                onClick = {
                    coroutineScope.launch {
                        val uriImage =
                            scanFilePath(context, filePath)
                        shareBitmap(context, uriImage)
                    }
                }
            ) {
                Text("Download")
            }
        }
    }else {
        Text(
            text = "Gambar tidak ditemukan atau gagal dimuat",
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun loadImageBitmapFromFile(path: String): ImageBitmap? {
    return try {
        val file = File(path)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(path)
            bitmap.asImageBitmap()
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun scanFilePath(context: Context, filePath: String): Uri? {
    return suspendCancellableCoroutine { continuation ->
        MediaScannerConnection.scanFile(
            context,
            arrayOf(filePath),
            arrayOf("image/png")
        ) { _, scannedUri ->
            if (scannedUri == null) {
                continuation.cancel(Exception("File $filePath could not be scanned"))
            } else {
                continuation.resume(scannedUri, onCancellation = null)
            }
        }
    }
}

private fun shareBitmap(context: Context, uri: Uri?) {
    if (uri != null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(context, createChooser(intent, "Share your image"), null)
    }
}
