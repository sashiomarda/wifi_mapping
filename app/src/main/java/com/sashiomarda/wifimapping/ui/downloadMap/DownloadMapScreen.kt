package com.sashiomarda.wifimapping.ui.downloadMap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.data.Dbm
import com.sashiomarda.wifimapping.data.Grid
import com.sashiomarda.wifimapping.data.ImageFile
import com.sashiomarda.wifimapping.ui.AppViewModelProvider
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import com.sashiomarda.wifimapping.ui.roomInput.RoomInputDestination
import com.sashiomarda.wifimapping.ui.viewmodel.DbmViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.ImageFileViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import java.io.File
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsDetails
import androidx.core.graphics.createBitmap
import kotlin.math.ceil
import androidx.core.graphics.scale
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.startActivity
import com.sashiomarda.wifimapping.components.ImageFromFile
import com.sashiomarda.wifimapping.components.loadImageBitmapFromFile
import com.sashiomarda.wifimapping.components.scanFilePath
import com.sashiomarda.wifimapping.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.FileOutputStream
import kotlin.io.copyTo

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
            if (Build.VERSION.SDK_INT < 34) {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    var roomData = previewGridViewModel.roomParamByIdsUiState.roomParamsDetails
    var layerListDb: MutableList<LayerList> = ArrayList()
    var layerListDisplay: MutableList<ImageFile> = ArrayList()
    if (allImageFileListDb.isNotEmpty()){
        allImageFileListDb.forEach { file ->
            layerListDisplay.add(file)
        }
    }
    var isButton2DExpandClicked by remember { mutableStateOf(false) }
    var isButton3DExpandClicked by remember { mutableStateOf(false) }

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
                    layerListDb.add(
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
    var isScrollEnabled by remember { mutableStateOf(true) }
    if (isPermissionGranted.value) {
        isScrollEnabled = false
        if (layerListDisplay.isNotEmpty()) {
            isScrollEnabled = true
        }
    }

    var isUploading2DImages by remember { mutableStateOf(true) }
    var resultUploading2DImages by remember { mutableStateOf<String?>(null) }

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
                if (layerListDb.isNotEmpty() && roomData.id != 0) {
                    val configuration = LocalConfiguration.current
                    layerListDb.forEachIndexed { index, layerList ->
                        var isSaveImage = false
                        LaunchedEffect(isPermissionGranted.value) {
                            if (timestampSaveImage == 0.toLong()) {
                                timestampSaveImage = System.currentTimeMillis()
                            }
                            val foundImageFileDb = allImageFileListDb.firstOrNull { file ->
                                file.layerNo == layerList.layerNo
                            }
                            if (foundImageFileDb == null) {
                                isSaveImage = true
                            } else {
                                isSaveImage = false
                            }
                            if (isSaveImage) {
                                if (Build.VERSION.SDK_INT < 34) {
                                    if (isPermissionGranted.value) {
                                        saveGridCanvasAsImage(
                                            context,
                                            layerList,
                                            roomData,
                                            configuration,
                                            timestampSaveImage,
                                            imageFileViewModel
                                        )
                                    }
                                }else{
                                    saveGridCanvasAsImage(
                                        context,
                                        layerList,
                                        roomData,
                                        configuration,
                                        timestampSaveImage,
                                        imageFileViewModel
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                        ) {

                            Card(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            isButton2DExpandClicked = !isButton2DExpandClicked
                                            if (Build.VERSION.SDK_INT < 34) {
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
                                        },
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Text("Peta 2D")
                                    Icon(
                                        imageVector = if (isButton2DExpandClicked) {
                                            Icons.Filled.KeyboardArrowUp
                                        } else {
                                            Icons.Filled.KeyboardArrowDown
                                        },
                                        contentDescription = "expand button",
                                        tint = Color(0xFF479AFF)
                                    )
                                }
                                if (isButton2DExpandClicked) {
                                    Column(
                                        modifier = Modifier,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .height(500.dp),
                                        ) {
                                            items(
                                                items = layerListDisplay,
                                                key = { imageFile: ImageFile ->
                                                    imageFile.layerNo
                                                }) {
                                                if (it.is3d == false) {
                                                    Column(
                                                        modifier = Modifier
                                                            .padding(10.dp)
                                                    ) {
                                                        Text(
                                                            text = "Layer ${it.layerNo}",
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(bottom = 4.dp),
                                                            textAlign = TextAlign.Center
                                                        )
                                                        ImageFromFile(
                                                            imageFileName = it.fileName,
                                                            context = context,
                                                            coroutineScope = coroutineScope
                                                        )
                                                    }
                                                    HorizontalDivider(
                                                        color = Color.LightGray,
                                                        modifier = Modifier
                                                            .padding(bottom = 10.dp)
                                                    )
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                isButton3DExpandClicked = !isButton3DExpandClicked
                                                val found3DImage = allImageFileListDb.firstOrNull{it.is3d == true}
                                                if (found3DImage == null) {
                                                    if (Build.VERSION.SDK_INT < 34) {
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
                                                        if (isPermissionGranted.value) {
                                                            if (allImageFileListDb.isNotEmpty()) {
                                                                val imageParts =
                                                                    getImageParts(allImageFileListDb = allImageFileListDb)
                                                                CoroutineScope(Dispatchers.IO).launch {
                                                                    try {
                                                                        val response =
                                                                            RetrofitClient.instance.uploadImages(
                                                                                imageParts
                                                                            )
                                                                        withContext(Dispatchers.Main) {
                                                                            isUploading2DImages =
                                                                                false
                                                                            resultUploading2DImages =
                                                                                if (response.isSuccessful) "Upload berhasil" else "Upload gagal: ${response.code()}"
                                                                            if (response.isSuccessful) {
                                                                                save3dImage(response = response,
                                                                                    allImageFileListDb = allImageFileListDb,
                                                                                    imageFileViewModel = imageFileViewModel
                                                                                )
                                                                            }

                                                                        }
                                                                    } catch (e: Exception) {
                                                                        withContext(Dispatchers.Main) {
                                                                            isUploading2DImages =
                                                                                false
                                                                            resultUploading2DImages =
                                                                                "Error: ${e.localizedMessage}"
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        if (allImageFileListDb.isNotEmpty()) {
                                                            val imageParts =
                                                                getImageParts(allImageFileListDb = allImageFileListDb)
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                try {
                                                                    val response =
                                                                        RetrofitClient.instance.uploadImages(
                                                                            imageParts
                                                                        )
                                                                    withContext(Dispatchers.Main) {
                                                                        isUploading2DImages = false
                                                                        resultUploading2DImages =
                                                                            if (response.isSuccessful) "Upload berhasil" else "Upload gagal: ${response.code()}"
                                                                        if (response.isSuccessful) {
                                                                            save3dImage(response = response,
                                                                                allImageFileListDb = allImageFileListDb,
                                                                                imageFileViewModel = imageFileViewModel
                                                                            )
                                                                        }
                                                                    }
                                                                } catch (e: Exception) {
                                                                    withContext(Dispatchers.Main) {
                                                                        isUploading2DImages = false
                                                                        resultUploading2DImages =
                                                                            "Error: ${e.localizedMessage}"
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Text("Peta 3D")
                                        Icon(
                                            imageVector = if (isButton3DExpandClicked) {
                                                Icons.Filled.KeyboardArrowUp
                                            } else {
                                                Icons.Filled.KeyboardArrowDown
                                            },
                                            contentDescription = "expand button",
                                            tint = Color(0xFF479AFF)
                                        )
                                    }
                                    if (isButton3DExpandClicked) {
                                        val brokenImage = painterResource(id = R.drawable.ic_broken_image)
                                        val loadingImage = painterResource(id = R.drawable.loading_img)
                                        Box() {
                                            val image3DFileName =
                                                allImageFileListDb.firstOrNull { it.is3d == true }
                                            if (image3DFileName != null) {
                                                isUploading2DImages = false
                                            }

                                            if (isUploading2DImages) {
                                                Image(
                                                    painter = loadingImage,
                                                    contentDescription = "loading peta 3D",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.wrapContentSize()
                                                )
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    CircularProgressIndicator()
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text("Tunggu sebentar sedang memproses gambar...")
                                                }
                                            } else {
                                                Column(
                                                    modifier = Modifier,
                                                ) {
                                                    val image3DFileName =
                                                        allImageFileListDb.firstOrNull { it.is3d == true }
                                                    if (image3DFileName != null) {
                                                        val filePath = File(
                                                            Environment.getExternalStoragePublicDirectory(
                                                                Environment.DIRECTORY_PICTURES
                                                            ),
                                                            image3DFileName.fileName
                                                        ).path
                                                        val image3DBitmap = remember(filePath) {
                                                            loadImageBitmapFromFile(filePath)
                                                        }
                                                        if (image3DBitmap != null) {
                                                            Row(modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(10.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center,
                                                            ) {
                                                                Image(
                                                                    bitmap = image3DBitmap,
                                                                    contentDescription = "Image from file",
                                                                    modifier = Modifier
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
                                                        }
                                                    } else {
                                                        Image(
                                                            painter = brokenImage,
                                                            contentDescription = "peta 3D",
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(200.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!isScrollEnabled) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitPointerEvent() // Menangkap semua gesture
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Tunggu sebentar sedang memproses gambar...")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun save3dImage(
    response: Response<ResponseBody>,
    allImageFileListDb: List<ImageFile>,
    imageFileViewModel: ImageFileViewModel
) {
    val inputStream =
        response.body()
            ?.byteStream()
    if (inputStream != null) {
        val timestamp =
            allImageFileListDb[0].timestamp
        val idHistory =
            allImageFileListDb[0].idHistory
        val layerNo = 0

        val sb =
            StringBuilder()
        for (i in 1..5) {
            sb.append(
                Random.nextInt(
                    0,
                    9
                ).toString()
            )
        }
        val randomNumber =
            sb.toString()
        val outputFileName =
            "${timestamp}_${idHistory}_${layerNo}_${randomNumber}_3d.png"

        val outputFile =
            File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ),
                outputFileName
            )
        val outputStream =
            FileOutputStream(
                outputFile
            )

        inputStream.copyTo(
            outputStream
        )
        outputStream.close()
        inputStream.close()

        imageFileViewModel.saveImageFile(
            ImageFile(
                timestamp = timestamp,
                fileName = outputFileName,
                idHistory = idHistory,
                layerNo = layerNo,
                is3d = true
            )
        )
    }
}

fun getImageParts(allImageFileListDb: List<ImageFile>): List<MultipartBody.Part> {
    var files : MutableList<File> = ArrayList()
    allImageFileListDb.forEach { imageFile ->
        files.add(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                imageFile.fileName
            )
        )
    }
    return files.mapIndexed { index, file ->
        val reqFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("images", file.name, reqFile)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun saveGridCanvasAsImage(
    context: Context,
    layerList: LayerList,
    roomData: RoomParamsDetails,
    configuration: Configuration,
    timestampSaveImage: Long,
    imageFileViewModel: ImageFileViewModel
) {
    val idHistory = layerList.dbmListPerLayer[0].idHistory
    val length = roomData.length.toFloat()
    val width = roomData.width.toFloat()
    val grid = roomData.gridDistance.toFloat()
    var gridCmToM = grid.toFloat().div(100)
    val screenWidth = configuration.screenWidthDp * 0.86
    var canvasHeight = 0.0
    var canvasWidth = 0.0
    var aspectRatioWidth = 0.2F
    var gridVerticalAmount = 0.0f
    var gridHorizontalAmount = 0.0f
    if (length > width) {
        aspectRatioWidth = width / length
        canvasHeight = screenWidth
        canvasWidth = screenWidth * aspectRatioWidth
        gridVerticalAmount = length / gridCmToM
        gridHorizontalAmount = width / gridCmToM
    } else {
        aspectRatioWidth = length / width
        canvasWidth = screenWidth
        canvasHeight = screenWidth * aspectRatioWidth
        gridVerticalAmount = width / gridCmToM
        gridHorizontalAmount = length / gridCmToM
    }
    canvasWidth = ceil(canvasWidth)
    val gridHeight = canvasWidth * gridCmToM / width
    val gridWidth = canvasHeight * gridCmToM / length
    val layerNo = layerList.layerNo
    val cellSize = (gridWidth * 1) - 5
    var repeatX = 0
    var repeatY = 0
    if (length > width) {
        repeatX = gridVerticalAmount.toInt()
        repeatY = gridHorizontalAmount.toInt()
    } else {
        repeatX = gridHorizontalAmount.toInt()
        repeatY = gridVerticalAmount.toInt()
    }

    val bitmap = createBitmap(canvasHeight.toInt(), canvasWidth.toInt())
    val canvas = Canvas(bitmap)


    val whitePaint = Paint().apply {
        color = "#FFFFFFFF".toColorInt()
        style = Paint.Style.FILL
    }
    val greenPaint = Paint().apply {
        color = "#FF1AFF00".toColorInt()
        style = Paint.Style.FILL
    }

    val yellowPaint = Paint().apply {
        color = "#FFFFEB3B".toColorInt()
        style = Paint.Style.FILL
    }

    val orangePaint = Paint().apply {
        color = "#FFFF9800".toColorInt()
        style = Paint.Style.FILL
    }

    val redPaint = Paint().apply {
        color = "#FFFF0000".toColorInt()
        style = Paint.Style.FILL
    }

    val borderPaint = Paint().apply {
        color = 0x00000000
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    var dbmGridMap = HashMap<Int, Int>()
    var gridListDb = layerList.gridListPerLayer
    var dbmListDb = layerList.dbmListPerLayer

    for (i in dbmListDb) {
        dbmGridMap[i.idGrid] = i.dbm
    }
    val iconBitmap = BitmapFactory.decodeResource(context.resources, android.R.drawable.star_on)

    for (i in gridListDb.indices) {
        var count = 0
        for (row in 0 until repeatY) {
            for (col in 0 until repeatX) {
                var dbm = dbmGridMap[gridListDb.get(i).id]
                if (dbm != null) {
                    if (count == i) {
                        val left = col * cellSize.toFloat()
                        val top = row * cellSize.toFloat()
                        val right = left + cellSize
                        val bottom = top + cellSize

                        val fillPaint = if (dbm == 0) {
                            whitePaint
                        } else if (dbm >= -67) {
                            greenPaint
                        } else if (dbm >= -70 && dbm <= -68) {
                            yellowPaint
                        } else if (dbm >= -80 && dbm <= -71) {
                            orangePaint
                        } else if (dbm < -80) {
                            redPaint
                        } else {
                            redPaint
                        }
                        canvas.drawRect(left, top, right.toFloat(), bottom.toFloat(), fillPaint)
                        canvas.drawRect(left, top, right.toFloat(), bottom.toFloat(), borderPaint)
                        if (gridListDb.get(i).idWifi != 0) {
                            val scaledIcon = iconBitmap.scale(10, 10)
                            canvas.drawBitmap(scaledIcon, left, top, null)
                        }
                    }
                }
                count += 1
            }
        }
    }
    val sb = StringBuilder()
    for (i in 1..5){
        sb.append(Random.nextInt(0, 9).toString())
    }
    val randomNumber = sb.toString()
    val imageFileName = "${timestampSaveImage}_${idHistory}_${layerNo}_${randomNumber}.png"

    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        imageFileName
    )
    file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 100)
    scanFilePath(context, file.path) ?: throw Exception("File could not be saved")

    imageFileViewModel.saveImageFile(ImageFile(
        timestamp = timestampSaveImage,
        fileName = imageFileName,
        idHistory = idHistory,
        layerNo = layerNo
    ))
}


data class LayerList(
    val layerNo: Int = 0,
    val dbmListPerLayer: MutableList<Dbm>,
    val gridListPerLayer: MutableList<Grid>,
    var filePath: String = "",
    var imageBitmap: ImageBitmap? = null
)

private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
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
