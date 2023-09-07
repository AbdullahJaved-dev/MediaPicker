package com.sdsol.mediapicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.abdullah.compressmedia.compress_helper.CompressionListener
import com.abdullah.compressmedia.compress_helper.bytesToMegabytes
import com.abdullah.compressmedia.compress_helper.compressImage
import com.abdullah.compressmedia.compress_helper.compressVideo
import com.abdullah.compressmedia.compress_helper.createImageFile
import com.abdullah.compressmedia.compress_helper.createVideoFile
import com.abdullah.compressmedia.compress_helper.enums.MediaType
import com.abdullah.compressmedia.compress_helper.getFileDataFromUri
import com.abdullah.compressmedia.compress_helper.getFileSizeFromContentUri
import com.abdullah.compressmedia.compress_helper.getMediaType
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.sdsol.mediapicker.ui.theme.MediaPickerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold {
                PickMedia(paddingValues = it)
                //SendEmail(it)
            }
        }
    }
}

@Composable
fun SendEmail(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        val pdfPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedPdfUri: Uri? = result.data?.data

                if (selectedPdfUri != null) {
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.type = "message/rfc822"
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email Body")
                    emailIntent.putExtra(Intent.EXTRA_STREAM, selectedPdfUri)
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                }
            }
        }

        Button(onClick = {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            pdfPickerLauncher.launch(intent)
        }) {
            Text(text = "Send Email")
        }

    }
}


@OptIn(ExperimentalPermissionsApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun PickMedia(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        var compressedImage by remember {
            mutableStateOf<String?>(null)
        }

        var actualImage by remember {
            mutableStateOf<String?>(null)
        }

        var video by remember {
            mutableStateOf<Uri?>(null)
        }

        var video2 by remember {
            mutableStateOf<Uri?>(null)
        }

        var showProgressDialog by remember {
            mutableStateOf(false)
        }

        var progressValue by remember {
            mutableFloatStateOf(1f)
        }

        if (showProgressDialog) {
            //"Compressing Video..."
            AnimatedCircularProgressIndicator(progressValue)
        }

        DisposableEffect(key1 = Unit) {
            onDispose {
                if (showProgressDialog) {
                    showProgressDialog = false
                }
            }
        }

        val mainCoroutineScope = rememberCoroutineScope {
            Dispatchers.Main
        }

        val singleMediaPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            when (context.getMediaType(uri)) {
                MediaType.Image -> {
                    val actualFileData = context.getFileDataFromUri(uri)
                    actualFileData?.let { fileData ->
                        Log.d("Image Compress", "Actual File Size: ${fileData.third} MB")

                        actualImage = actualFileData.second
                        val compressedFile = context.compressImage(fileData.second)

                        compressedImage = compressedFile?.absolutePath

                        Log.d(
                            "Image Compress",
                            "Compressed File Size: ${
                                compressedFile?.length()?.bytesToMegabytes()
                            } MB"
                        )
                    }
                }

                MediaType.Video -> {
                    mainCoroutineScope.launch {
                        showProgressDialog = true
                        val fileSize = withContext(Dispatchers.IO) {
                            context.getFileSizeFromContentUri(uri)
                        }
                        showProgressDialog = false
                        if (fileSize.toInt() != -1) {
                            println("Video Compress: Actual Video Size $fileSize MB")
                        } else {
                            println("Unable to determine file size for the given content URI")
                        }
                        video = uri

                        context.compressVideo(
                            uri,
                            listener = object : CompressionListener {
                                override fun onProgress(
                                    index: Int,
                                    percent: Float
                                ) {
                                    mainCoroutineScope.launch {
                                        progressValue = percent
                                    }
                                }

                                override fun onStart(index: Int) {
                                    progressValue = 0f
                                    showProgressDialog = true
                                }

                                override fun onSuccess(
                                    index: Int,
                                    size: Long,
                                    path: String?
                                ) {
                                    showProgressDialog = false

                                    path?.let { p ->
                                        println("Video Compress: Compressed Video Size ${size.bytesToMegabytes()} MB")
                                        video2 = File(p).toUri()
                                    }

                                }

                                override fun onFailure(
                                    index: Int,
                                    failureMessage: String
                                ) {
                                    showProgressDialog = false
                                }

                                override fun onCancelled(index: Int) {
                                    showProgressDialog = false
                                }
                            }
                        )
                    }
                }

                MediaType.Unknown -> {

                }
            }
        }

        val galleryPermissionState = rememberMultiplePermissionsState(
            permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            else listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        ) { permissions ->
            if (permissions.values.all { true })
                singleMediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
        }

        var showCameraDialog by remember {
            mutableStateOf(false)
        }

        var currentMediaUri by remember { mutableStateOf<Uri?>(null) }

        val takePicture =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    if (success && currentMediaUri != null) {
                        val actualFileData = context.getFileDataFromUri(currentMediaUri!!)
                        actualFileData?.let { fileData ->
                            Log.d("Image Compress", "Actual File Size: ${fileData.third} MB")

                            actualImage = actualFileData.second
                            val compressedFile = context.compressImage(fileData.second)

                            compressedImage = compressedFile?.absolutePath

                            Log.d(
                                "Image Compress",
                                "Compressed File Size: ${
                                    compressedFile?.length()?.bytesToMegabytes()
                                } MB"
                            )
                        }
                    }
                })

        val takeVideo =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.CaptureVideo(),
                onResult = { success ->
                    if (success && currentMediaUri != null) {
                        mainCoroutineScope.launch {
                            showProgressDialog = true
                            val fileSize = withContext(Dispatchers.IO) {
                                context.getFileSizeFromContentUri(currentMediaUri)
                            }
                            showProgressDialog = false
                            if (fileSize.toInt() != -1) {
                                println("Video Compress: Actual Video Size $fileSize MB")
                            } else {
                                println("Unable to determine file size for the given content URI")
                            }
                            video = currentMediaUri

                            context.compressVideo(
                                currentMediaUri!!,
                                listener = object : CompressionListener {
                                    override fun onProgress(
                                        index: Int,
                                        percent: Float
                                    ) {

                                    }

                                    override fun onStart(index: Int) {
                                        progressValue = 0f
                                        showProgressDialog = true
                                    }

                                    override fun onSuccess(
                                        index: Int,
                                        size: Long,
                                        path: String?
                                    ) {
                                        showProgressDialog = false

                                        path?.let { p ->
                                            println("Video Compress: Compressed Video Size ${size.bytesToMegabytes()} MB")
                                            video2 = File(p).toUri()
                                        }
                                    }

                                    override fun onFailure(
                                        index: Int,
                                        failureMessage: String
                                    ) {
                                        showProgressDialog = false
                                    }

                                    override fun onCancelled(index: Int) {
                                        showProgressDialog = false
                                    }
                                }
                            )
                        }
                    }
                })

        if (showCameraDialog) {
            CameraDialog(onPhotoSelect = {
                showCameraDialog = false
                val capturedImageFile = context.createImageFile()
                if (capturedImageFile != null) {
                    currentMediaUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        capturedImageFile
                    )
                }
                takePicture.launch(currentMediaUri)
            }, onVideoSelect = {
                showCameraDialog = false
                val capturedImageFile = context.createVideoFile()
                if (capturedImageFile != null) {
                    currentMediaUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        capturedImageFile
                    )
                }
                takeVideo.launch(currentMediaUri)
            })
        }

        val cameraPermissionState = rememberMultiplePermissionsState(
            permissions = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).also {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    it.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    it.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    it.add(Manifest.permission.READ_MEDIA_IMAGES)
                    it.add(Manifest.permission.READ_MEDIA_VIDEO)
                }
            }
        ) { permissions ->
            if (permissions.values.all { true })
                showCameraDialog = true
        }

        Button(modifier = Modifier.padding(top = 16.dp), onClick = {
            if (galleryPermissionState.allPermissionsGranted) {
                Log.d(
                    "VideoPicker",
                    "Selection Time: ${System.currentTimeMillis()}"
                )
                singleMediaPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            } else
                galleryPermissionState.launchMultiplePermissionRequest()
        }) {
            Text(text = "Pick Media")
        }

        Button(onClick = {
            if (cameraPermissionState.allPermissionsGranted)
                showCameraDialog = true
            else
                cameraPermissionState.launchMultiplePermissionRequest()
        }) {
            Text(text = "Capture Media")
        }

        if (compressedImage != null) {
            GlideImage(
                model = compressedImage,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        if (actualImage != null) {
            GlideImage(
                model = actualImage,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        if (video != null) {
            val mExoPlayer = remember(context) {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(video!!)
                    setMediaItem(mediaItem)
                    prepare()

                    addListener(
                        object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                super.onPlayerError(error)

                                //error.localizedMessage?.let { showToast(mContext, it) }
                            }
                        }
                    )
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    mExoPlayer.stop()
                    mExoPlayer.release()
                }
            }

            AndroidView(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f), factory = { c ->
                    PlayerView(c).apply {
                        player = mExoPlayer
                    }
                })
        }

        if (video2 != null) {
            val mExoPlayer = remember(context) {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(video2!!)
                    setMediaItem(mediaItem)
                    prepare()

                    addListener(
                        object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                super.onPlayerError(error)

                                //error.localizedMessage?.let { showToast(mContext, it) }
                            }
                        }
                    )
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    mExoPlayer.stop()
                    mExoPlayer.release()
                }
            }

            AndroidView(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f), factory = { c ->
                    PlayerView(c).apply {
                        player = mExoPlayer
                    }
                })
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaPickerTheme {
        Greeting("Android")
    }
}