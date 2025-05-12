package com.sashiomarda.wifimapping.ui.downloadMap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.components.CanvasGrid
import com.sashiomarda.wifimapping.data.Dbm
import com.sashiomarda.wifimapping.data.Grid
import com.sashiomarda.wifimapping.data.ImageFile
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.chooseWifi.PERMISSIONS_REQUEST_CODE
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.roomInput.RoomInputDestination
import com.sashiomarda.wifimapping.ui.viewmodel.DbmViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.ImageFileViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.random.Random

object DownloadMapDestination : NavigationDestination {
    override val route = "download_map"
    override val titleRes = R.string.download_map_title
    const val idHistory = "idHistory"
    val routeWithArgs = "${route}/{$idHistory}"
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadMapScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    gridViewModel: GridViewModel = viewModel(factory = AppViewModelProvider.Factory),
    previewGridViewModel: RoomParamsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dbmViewModel: DbmViewModel = viewModel(factory = AppViewModelProvider.Factory),
    imageFileViewModel: ImageFileViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allDbmListDb by dbmViewModel.allDbmUiStateList.collectAsState()
    val allGridListDb by gridViewModel.allGridUiStateList.collectAsState()
    val activity = context as Activity
    val allImageFileListDb by imageFileViewModel.allImageFileList.collectAsState()
    var timestampSaveImage by remember { mutableLongStateOf(0) }
    LaunchedEffect(Unit) {
        imageFileViewModel.startUpdateJob()
    }
    val isPermissionGranted = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isPermissionGranted.value = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    var roomData = previewGridViewModel.roomParamByIdsUiState.roomParamsDetails
    var layerListDisplay: MutableList<LayerList> = ArrayList()
    var layerNoClicked by remember { mutableIntStateOf(0) }
    var isShareImageButton by remember { mutableStateOf(false) }
    LaunchedEffect(allImageFileListDb) {
        if (Build.VERSION.SDK_INT < 34) {
            if (isPermissionGranted.value == true){
                val foundLayerNo = layerListDisplay.firstOrNull{it.layerNo == layerNoClicked}
                if (foundLayerNo != null) {
                    val uriImage =
                        scanFilePath(
                            context,
                            foundLayerNo.filePath
                        )
                    shareBitmap(context, uriImage)
                    isShareImageButton = true
                }
            }
        }
    }
    var gridPerLayer = if (allDbmListDb.dbmList.isEmpty()){
        1
    }else{
        if (roomData.layerCount != "") {
            allDbmListDb.dbmList.size / roomData.layerCount.toInt()
        }else{
            1
        }
    }

    var dbmList: MutableList<Dbm> = ArrayList()
    var gridList: MutableList<Grid> = ArrayList()
    var gridCount = 1
    if (allDbmListDb.dbmList.isNotEmpty() && allGridListDb.gridList.isNotEmpty()) {
        for (i in allDbmListDb.dbmList.indices) {
            if (gridCount > gridPerLayer) {
                gridCount = 1
            }
            if (gridCount <= gridPerLayer) {
                dbmList.add(
                    allDbmListDb.dbmList[i]
                )
                gridList.add(
                    allGridListDb.gridList[i]
                )
                if (gridCount == gridPerLayer) {
                    val layerNoDb = allDbmListDb.dbmList[i].layerNo
                    layerListDisplay.add(
                        LayerList(
                            layerNo = layerNoDb,
                            dbmListPerLayer = dbmList.toMutableList(),
                            gridListPerLayer = gridList.toMutableList(),
                        )
                    )
                    dbmList.clear()
                    gridList.clear()
                }
            }
            gridCount++
        }
    }

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(RoomInputDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Download Peta",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                )
                if (layerListDisplay.isNotEmpty() && roomData.id != 0) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        items(
                            items = layerListDisplay,
                            key = { layerList: LayerList ->
                                layerList.layerNo
                            }) {
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            ) {
                                var imageBitmap by mutableStateOf(ImageBitmap(1, 1))
                                val graphicsLayer = rememberGraphicsLayer()
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .drawWithContent {
                                            graphicsLayer.record {
                                                this@drawWithContent.drawContent()
                                            }
                                            drawLayer(graphicsLayer)
                                            coroutineScope.launch {
                                                var canvasBitmap = graphicsLayer.toImageBitmap()
                                                imageBitmap = canvasBitmap
                                                it.imageBitmap = imageBitmap
                                            }
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .background(Color.White)
                                    ) {
                                        Text(
                                            text = "Layer ${it.layerNo}",
                                            color = Color.Black
                                        )
                                        CanvasGrid(
                                            length = roomData.length.toFloat(),
                                            width = roomData.width.toFloat(),
                                            grid = roomData.gridDistance.toInt(),
                                            gridViewModel = gridViewModel,
                                            gridListDb = it.gridListPerLayer,
                                            dbmListDb = it.dbmListPerLayer,
                                            saveIdGridRouterPosition = {},
                                            screen = DownloadMapDestination.route,
                                            addChosenIdList = { ssidId, gridId -> },
                                            updateGridList = {}
                                        )
                                    }
                                    var isSaveImage = false
                                    if (allImageFileListDb.isNotEmpty()) {
                                        val foundImageFileDb =
                                            allImageFileListDb.firstOrNull { file ->
                                                file.layerNo == it.layerNo
                                            }
                                        if (foundImageFileDb == null) {
                                            isSaveImage = true
                                        } else {
                                            it.filePath = getFilePath(foundImageFileDb.fileName)
                                        }
                                    } else {
                                        isSaveImage = true
                                    }
                                    if (isSaveImage) {
                                        if (timestampSaveImage == 0.toLong()) {
                                            timestampSaveImage = System.currentTimeMillis()
                                        }
                                        if (Build.VERSION.SDK_INT < 34) {
                                            LaunchedEffect(isPermissionGranted.value) {
                                                if (isPermissionGranted.value == true) {
                                                    coroutineScope.launch {
                                                        if (it.imageBitmap != null) {
                                                            val uriFilePath =
                                                                it.imageBitmap!!.asAndroidBitmap()
                                                                    .saveToDisk(
                                                                        context,
                                                                        it.layerNo,
                                                                        it.gridListPerLayer[0].idHistory,
                                                                        imageFileViewModel,
                                                                        timestampSaveImage
                                                                    )
                                                            it.filePath = uriFilePath.filePath
//                                                        }
                                                        }
                                                    }

                                                }
                                            }
                                        } else {
                                            LaunchedEffect(allGridListDb) {
                                                coroutineScope.launch {
                                                    if (it.imageBitmap != null) {
                                                        val uriFilePath =
                                                            it.imageBitmap!!.asAndroidBitmap()
                                                                .saveToDisk(
                                                                    context,
                                                                    it.layerNo,
                                                                    it.gridListPerLayer[0].idHistory,
                                                                    imageFileViewModel,
                                                                    timestampSaveImage
                                                                )
                                                        it.filePath = uriFilePath.filePath
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Button(
                                        modifier = Modifier
                                            .padding(start = 8.dp),
                                        onClick = {
                                            coroutineScope.launch {
                                                if (Build.VERSION.SDK_INT < 34) {
                                                    isSaveImage = false
                                                    layerNoClicked = it.layerNo
                                                    if (isShareImageButton == true){
                                                        val uriImage =
                                                            scanFilePath(context, it.filePath)
                                                        shareBitmap(context, uriImage)
                                                    }
                                                } else {
                                                    val uriImage =
                                                        scanFilePath(context, it.filePath)
                                                    shareBitmap(context, uriImage)
                                                }
                                            }
                                            when {
                                                checkSelfPermission(
                                                    context,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                ) == PackageManager.PERMISSION_GRANTED -> {
                                                    isPermissionGranted.value = true
                                                }
                                                else -> {
                                                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Download")
                                    }
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {}
                ) {

                }
            }
        }
    }
}

data class LayerList(
    val layerNo: Int = 0,
    val dbmListPerLayer: MutableList<Dbm>,
    val gridListPerLayer: MutableList<Grid>,
    var filePath: String = "",
    var imageBitmap: ImageBitmap? = null
)

data class UriFilePath(
    val uri: Uri,
    val filePath: String
)


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

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
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

@RequiresApi(Build.VERSION_CODES.O)
private suspend fun Bitmap.saveToDisk(
    context: Activity,
    layerNo: Int,
    idHistory: Int,
    imageFileViewModel: ImageFileViewModel,
    timestamp: Long
): UriFilePath {
    val sb = StringBuilder()
    for (i in 1..5){
        sb.append(Random.nextInt(0, 9).toString())
    }
    val randomNumber = sb.toString()

    val imageFileName = "${timestamp}_${idHistory}_${layerNo}_${randomNumber}.png"

    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        imageFileName
    )

    file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

    imageFileViewModel.saveImageFile(ImageFile(
        timestamp = timestamp,
        fileName = imageFileName,
        idHistory = idHistory,
        layerNo = layerNo
    ))

    return UriFilePath(
        uri = scanFilePath(context, file.path) ?: throw Exception("File could not be saved"),
        filePath = file.path
    )
}

fun getFilePath(imageFileName: String): String{
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        imageFileName
    )
    return file.path
}

fun requestWriteStoragePermission(activity: Activity) {
    requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
        PERMISSIONS_REQUEST_CODE
    )
}